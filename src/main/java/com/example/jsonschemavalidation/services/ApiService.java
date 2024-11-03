package com.example.jsonschemavalidation.services;

import com.example.jsonschemavalidation.models.ApiDetails;
import com.example.jsonschemavalidation.repositories.ApiRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SpecVersion;
import com.networknt.schema.ValidationMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.View;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;

@Service
public class ApiService {

    @Autowired
    private ApiRepository apiRepository;

    public ApiDetails saveApi(ApiDetails apiDetails) {

        Optional<ApiDetails> existingApi = apiRepository.findByApiIdentifier(apiDetails.getApiIdentifier());

        if(!existingApi.isEmpty()) {
            throw new IllegalArgumentException("API identifier "  + apiDetails.getApiIdentifier() + " already exists.");
        }

        return apiRepository.save(apiDetails);
    }

    public String validateApi(Long apiIdentifier, JsonNode data) {
        ApiDetails apiDetails = apiRepository.findByApiIdentifier(apiIdentifier)
                .orElseThrow(() -> new NoSuchElementException("API identifier " + apiIdentifier + " not found."));

        //json schema
        JsonNode apiSchemaJson = apiDetails.getApiSchema();

        // Validate the data against the API schema
        JsonSchema jsonSchema = JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V7).getSchema(apiSchemaJson); //apiSchemaJson may need convert to toString()

        Set<ValidationMessage> errors = jsonSchema.validate(data);

        if(errors.isEmpty()) {
            return null;
        } else {
            return errors.toString();
        }
    }
}
