package com.example.jsonschemavalidation.controllers;

import com.example.jsonschemavalidation.models.ApiDetails;
import com.example.jsonschemavalidation.dto.ApiRequest;
import com.example.jsonschemavalidation.services.ApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalTime;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
public class ApiController {

    @Autowired
    private ApiService apiService;

    @PostMapping("/api")
    public ResponseEntity<Map<String, Object>> saveApi(@RequestBody ApiDetails apiDetails) {
        ApiDetails api = apiService.saveApi(apiDetails);

        LocalTime now = LocalTime.now();
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("timestamp", now.toString());
        response.put("status", "201");
        response.put("message", "Successfully saved.");
        response.put("data", api);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/validate")
    public ResponseEntity<Map<String, Object>> validateApi(@RequestBody ApiRequest apiRequest) {
        String result = apiService.validateApi(apiRequest.getApiIdentifier(), apiRequest.getData());

        Map<String, Object> response = new LinkedHashMap<>();
        LocalTime now = LocalTime.now();
        response.put("timestamp", now.toString());

        if(result == null) {
            response.put("status", "200");
            response.put("message", "Request validation successful.");
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } else {
            response.put("status", "400");
            response.put("message", "Request validation failed.");
            response.put("errors", result);

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
}
