package com.gianluca.utils;

import com.google.gson.Gson;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public final class JsonDataReader {

    private static final Gson GSON = new Gson();

    private JsonDataReader() {
    }

    public static <T> T read(String filePath, Class<T> clazz) {
        InputStream inputStream =
                JsonDataReader.class
                        .getClassLoader()
                        .getResourceAsStream(filePath);

        if (inputStream == null) {
            throw new IllegalArgumentException(
                    "File JSON non trovato: " + filePath
            );
        }

        try (InputStreamReader reader =
                     new InputStreamReader(
                             inputStream,
                             StandardCharsets.UTF_8
                     )) {

            return GSON.fromJson(reader, clazz);

        } catch (Exception e) {
            throw new RuntimeException(
                    "Errore durante la lettura del file JSON: " + filePath,
                    e
            );
        }
    }

}
