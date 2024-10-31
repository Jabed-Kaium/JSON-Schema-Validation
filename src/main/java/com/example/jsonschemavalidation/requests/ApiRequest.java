package com.example.jsonschemavalidation.requests;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor @AllArgsConstructor
@Getter @Setter
public class ApiRequest {

    private Long apiIdentifier;
    private JsonNode data;
}
