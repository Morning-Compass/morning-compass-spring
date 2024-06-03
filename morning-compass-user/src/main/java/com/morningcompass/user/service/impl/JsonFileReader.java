package com.morningcompass.user.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

@Service
public class JsonFileReader {
    public JsonNode read(String path) throws IOException, URISyntaxException {
        ObjectMapper objectMapper = new ObjectMapper();
        URL resource = getClass().getClassLoader().getResource(path);
        if (resource == null) {
            throw new IllegalArgumentException("file not found!");
        } else {
            return objectMapper.readTree(new File(resource.toURI()));
        }
    }
}
