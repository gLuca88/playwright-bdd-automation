package com.gianluca.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class JsonDataReaderTest {

    private record TestData(String nome, int eta) {
    }

    @Test
    void read_shouldDeserializeJsonIntoRecord() {
        TestData actual = JsonDataReader.read("testdata/json-reader-test.json", TestData.class);

        assertEquals("Mario", actual.nome());
        assertEquals(30, actual.eta());
    }

    @Test
    void read_shouldThrowExceptionWhenJsonFileDoesNotExist() {

        assertThrows(
                IllegalArgumentException.class,
                () -> JsonDataReader.read(
                        "testdata/file-inesistente.json",
                        TestData.class
                )
        );
    }
}

