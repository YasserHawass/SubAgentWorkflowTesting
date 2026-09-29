package com.ae.fixtures;

import java.util.concurrent.atomic.AtomicInteger;

public final class TestUsers {
    private TestUsers() {}

    private static final AtomicInteger SEQ = new AtomicInteger();

    /** Unique, disposable user. Email is unique per JVM run + sequence. */
    public static TestUser newUser() {
        String tag = System.currentTimeMillis() + "-" + SEQ.incrementAndGet();
        return new TestUser(
                "AE Tester " + tag,
                "ae.test+" + tag + "@example.com",
                "Passw0rd!" + tag,
                "Mr",
                "10", "5", "1990",
                "Auto", "Tester",
                "AE QA",
                "1 Test Street", "Suite 2",
                "United States", "California", "San Jose", "95101",
                "+10000000000"
        );
    }
}
