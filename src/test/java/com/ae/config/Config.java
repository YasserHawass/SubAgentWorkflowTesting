package com.ae.config;

public final class Config {
    private Config() {}

    public static String baseUrl() {
        return System.getProperty("base.url", "https://www.automationexercise.com");
    }

    public static String apiBaseUrl() {
        return baseUrl() + "/api";
    }

    public static boolean headless() {
        return Boolean.parseBoolean(System.getProperty("playwright.headless", "true"));
    }

    public static double slowMo() {
        return Double.parseDouble(System.getProperty("playwright.slowmo", "0"));
    }
}
