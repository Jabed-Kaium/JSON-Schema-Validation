package com.example.jsonschemavalidation.controllers;

import com.example.jsonschemavalidation.models.ApiDetails;
import com.example.jsonschemavalidation.requests.ApiRequest;
import com.example.jsonschemavalidation.services.ApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ApiController {

    @Autowired
    private ApiService apiService;

    @PostMapping("/api")
    public ResponseEntity<?> saveApi(@RequestBody ApiDetails apiDetails) {
        ApiDetails api = apiService.saveApi(apiDetails);
        return ResponseEntity.ok(api);
    }

    @PostMapping("/validate")
    public ResponseEntity<?> validateApi(@RequestBody ApiRequest apiRequest) {
        String result = apiService.validateApi(apiRequest.getApiIdentifier(), apiRequest.getData());

        if(result == null) {
            return ResponseEntity.ok("Request validation successful");
        } else {
            return ResponseEntity.badRequest().body(result);
        }
    }
}
