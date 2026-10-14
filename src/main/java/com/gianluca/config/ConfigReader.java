package com.gianluca.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.Locale;
import java.net.URI;

public final class ConfigReader {

    private static final Properties PROPERTIES = new Properties();

    static {
        loadProperties();
    }

    private ConfigReader() {
    }

    private static void loadProperties() {
        PROPERTIES.putAll(loadProperties(System.getProperty("env", "qa")));
    }

    static Properties loadProperties(String environment) {
        Properties properties = new Properties();
        String fileName = "config-" + environment + ".properties";

        try (InputStream inputStream =
                     ConfigReader.class
                             .getClassLoader()
                             .getResourceAsStream(fileName)) {

            if (inputStream == null) {
                throw new IllegalStateException(
                        "File di configurazione non trovato: " + fileName
                );
            }

            properties.load(inputStream);
            return properties;

        } catch (IOException e) {
            throw new IllegalStateException(
                    "Errore durante il caricamento del file: " + fileName,
                    e
            );
        }
    }

    public static String get(String key) {

        String value = System.getProperty(key, PROPERTIES.getProperty(key));

        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(
                    "Proprietà non trovata o vuota: " + key
            );
        }

        return value.trim();
    }

    public static String get(String key, String defaultValue) {
        String value = System.getProperty(key, PROPERTIES.getProperty(key));
        return (value == null || value.isBlank()) ? defaultValue : value.trim();
    }

    public static int getInt(String key) {
        try {
            return Integer.parseInt(get(key));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(
                    "La proprietà '" + key + "' non è un numero intero valido",
                    e
            );
        }
    }

    public static boolean getBoolean(String key) {

        String value = get(key);

        if (!value.equalsIgnoreCase("true")
                && !value.equalsIgnoreCase("false")) {

            throw new IllegalArgumentException(
                    "La proprietà '" + key +
                            "' deve essere true oppure false"
            );
        }

        return Boolean.parseBoolean(value);
    }

    public static void validate() {
        String browser = get("browser.name").toLowerCase(Locale.ROOT);
        if (!java.util.Set.of("chromium", "firefox", "webkit").contains(browser)) {
            throw new IllegalArgumentException("Browser non supportato: " + browser);
        }
        if (getInt("browser.timeout") <= 0) {
            throw new IllegalArgumentException("browser.timeout deve essere maggiore di zero");
        }
        getBoolean("browser.headless");
        getBoolean("playwright.trace.enabled");
        getBoolean("playwright.video.enabled");
        getBoolean("playwright.screenshot.enabled");
        URI baseUrl = URI.create(get("base.url"));
        if (baseUrl.getHost() == null || !("https".equalsIgnoreCase(baseUrl.getScheme())
                || "http".equalsIgnoreCase(baseUrl.getScheme()))) {
            throw new IllegalArgumentException("base.url deve essere un URL HTTP/HTTPS assoluto");
        }
    }
}

