package com.web.demo.reader;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.List;

@Component
public class JsonFileReader {

    private final ObjectMapper objectMapper;

    public JsonFileReader(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public <T> List<T> readListFromFile(String fileName, TypeReference<List<T>> typeReference) {
        try (InputStream inputStream = new ClassPathResource(fileName).getInputStream()) {
            return objectMapper.readValue(inputStream, typeReference);
        } catch (Exception e) {
            throw new RuntimeException("Failed to read JSON file: " + fileName, e);
        }
    }
}

