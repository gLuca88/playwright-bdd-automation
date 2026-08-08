package com.gianluca.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public final class ConfigReader {

    private static final Properties PROPERTIES = new Properties();

    static {
        loadProperties();
    }

    private ConfigReader() {
    }

    private static void loadProperties() {

        String environment = System.getProperty("env", "qa");
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

            PROPERTIES.load(inputStream);

        } catch (IOException e) {
            throw new IllegalStateException(
                    "Errore durante il caricamento del file: " + fileName,
                    e
            );
        }
    }

    public static String get(String key) {

        String value = PROPERTIES.getProperty(key);

        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(
                    "Proprietà non trovata o vuota: " + key
            );
        }

        return value.trim();
    }

    public static String get(String key, String defaultValue) {
        String value = PROPERTIES.getProperty(key);
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
}

