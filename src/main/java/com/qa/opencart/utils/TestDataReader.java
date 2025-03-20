package com.qa.opencart.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.HashMap;

public class TestDataReader {
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final String TEST_DATA_DIR = "./src/test/resources/testdata/";

    public static Map<String, Object> getTestData(String fileName) {
        try {
            return objectMapper.readValue(
                new File(TEST_DATA_DIR + fileName + ".json"),
                new TypeReference<HashMap<String, Object>>() {}
            );
        } catch (IOException e) {
            throw new RuntimeException("Failed to read test data file: " + fileName, e);
        }
    }

    public static <T> T getTestData(String fileName, Class<T> valueType) {
        try {
            return objectMapper.readValue(new File(TEST_DATA_DIR + fileName + ".json"), valueType);
        } catch (IOException e) {
            throw new RuntimeException("Failed to read test data file: " + fileName, e);
        }
    }
} 