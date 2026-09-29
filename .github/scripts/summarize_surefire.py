#!/usr/bin/env python3
"""Build a run report from Surefire XML.

Usage: summarize_surefire.py <surefire-dir> <out-dir> <known-failures-file>

Writes <out-dir>/summary.json and <out-dir>/summary.md, prints the markdown,
and exits 1 only when a test fails that is not listed in known-failures
(TC ids of filed product bugs). Known product bugs still run and still show
as failed in the report; they just don't hide new regressions behind a
permanently red build.
"""
import json
import os
import re
import sys
import xml.etree.ElementTree as ET
from datetime import datetime, timezone

METHOD_ID = re.compile(r"^tc([A-Za-z]+?)(\d+)[a-z]?_")  # tcCart09b_x -> TC-CART-09


def tc_id(name: str) -> str:
    """tcApiAuth01_x -> TC-APIAUTH-01, tcLogout08_x -> TC-LOGOUT-08."""
    m = METHOD_ID.match(name)
    return f"TC-{m.group(1).upper()}-{m.group(2)}" if m else ""


def load_known(path: str) -> dict:
    known = {}
    if not os.path.exists(path):
        return known
    with open(path, encoding="utf-8") as f:
        for line in f:
            line = line.split("#", 1)[0].strip()
            if not line:
                continue
            tid, _, ref = line.partition(" ")
            known[tid] = ref.strip()
    return known


def parse(surefire_dir: str) -> list:
    cases = []
    for fn in sorted(os.listdir(surefire_dir)):
        if not (fn.startswith("TEST-") and fn.endswith(".xml")):
            continue
        root = ET.parse(os.path.join(surefire_dir, fn)).getroot()
        for tc in root.iter("testcase"):
            name = tc.get("name", "")
            cls = tc.get("classname", "")
            status, message = "passed", ""
            for tag in ("failure", "error"):
                el = tc.find(tag)
                if el is not None:
                    status = "failed"
                    message = (el.get("message") or el.text or "").strip()
            if tc.find("skipped") is not None:
                status = "skipped"
            cases.append({
                "id": tc_id(name),
                "class": cls.rsplit(".", 1)[-1],
                "method": name,
                "status": status,
                "timeSec": float(tc.get("time") or 0),
                "message": " ".join(message.split())[:300],
            })
    return cases


def main() -> int:
    surefire_dir, out_dir, known_file = sys.argv[1:4]
    os.makedirs(out_dir, exist_ok=True)
    known = load_known(known_file)
    cases = parse(surefire_dir) if os.path.isdir(surefire_dir) else []

    failed = [c for c in cases if c["status"] == "failed"]
    for c in failed:
        # an entry may be a TC id or a method name (when one TC id spans several methods)
        key = c["method"] if c["method"] in known else c["id"]
        c["known"] = key in known
        c["bug"] = known.get(key, "")
    new_failures = [c for c in failed if not c["known"]]

    per_class = {}
    for c in cases:
        k = per_class.setdefault(c["class"], {"passed": 0, "failed": 0, "skipped": 0})
        k[c["status"]] += 1

    totals = {s: sum(1 for c in cases if c["status"] == s) for s in ("passed", "failed", "skipped")}
    if not cases:
        result = "no-results"
    elif new_failures:
        result = "red"
    elif failed:
        result = "green-with-known-bugs"
    else:
        result = "green"

    summary = {
        "generatedAt": datetime.now(timezone.utc).isoformat(timespec="seconds"),
        "commit": os.environ.get("GITHUB_SHA", ""),
        "runUrl": (f"{os.environ['GITHUB_SERVER_URL']}/{os.environ['GITHUB_REPOSITORY']}"
                   f"/actions/runs/{os.environ['GITHUB_RUN_ID']}") if os.environ.get("GITHUB_RUN_ID") else "",
        "result": result,
        "totals": totals,
        "perClass": per_class,
        "failures": failed,
        "newFailures": [c["id"] or c["method"] for c in new_failures],
    }
    with open(os.path.join(out_dir, "summary.json"), "w", encoding="utf-8") as f:
        json.dump(summary, f, indent=2)

    icon = {"green": "🟢", "green-with-known-bugs": "🟡", "red": "🔴", "no-results": "⚪"}[result]
    lines = [
        f"# Test run {icon} {result}",
        "",
        f"Commit `{summary['commit'][:7]}` · {summary['generatedAt']}"
        + (f" · [workflow run]({summary['runUrl']})" if summary["runUrl"] else ""),
        "",
        f"**{totals['passed']} passed · {totals['failed']} failed · {totals['skipped']} skipped** "
        f"({len(new_failures)} new, {len(failed) - len(new_failures)} known product bugs)",
        "",
        "| Class | Passed | Failed | Skipped |",
        "|---|---|---|---|",
    ]
    for cls, k in sorted(per_class.items()):
        lines.append(f"| {cls} | {k['passed']} | {k['failed']} | {k['skipped']} |")
    if failed:
        lines += ["", "## Failures", "", "| TC id | Test | Known bug | Message |", "|---|---|---|---|"]
        for c in failed:
            msg = c["message"].replace("|", "\\|")[:160]
            bug = c["bug"] or ("—" if c["known"] else "**NEW**")
            lines.append(f"| {c['id']} | {c['class']}.{c['method']} | {bug} | {msg} |")
    md = "\n".join(lines) + "\n"
    with open(os.path.join(out_dir, "summary.md"), "w", encoding="utf-8") as f:
        f.write(md)
    sys.stdout.reconfigure(encoding="utf-8")
    print(md)
    return 1 if result in ("red", "no-results") else 0


if __name__ == "__main__":
    sys.exit(main())
