package com.morningcompass.user.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class JsonResponse {
    private final ObjectMapper objectMapper = new ObjectMapper();

    public ResponseEntity<String> Generate(String key, Object message, HttpStatus status) throws JsonProcessingException {
        return new ResponseEntity<>(String.format("{\"%s\": \"%s\"}", key, objectMapper.writeValueAsString(message)), status);
    }
}
