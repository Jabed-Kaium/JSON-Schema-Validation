package com.example.jsonschemavalidation.repositories;

import com.example.jsonschemavalidation.models.ApiDetails;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.NoSuchElementException;


import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Slf4j
@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class ApiRepositoryTest {

    @Autowired
    private ApiRepository apiRepository;

    @BeforeEach
    public void setUp() {

        String jsonSchema = """
                    {
                      "$schema": "https://json-schema.org/draft-07/schema#",
                      "title": "Add person event",
                      "description": "add person event for jsonschema",
                      "type": "object",
                      "properties": {
                        "name": {
                          "type": "string"
                        },
                        "age": {
                          "type": "integer",
                          "minimum": 18
                        }
                      }
                    }
                """;

        log.info("jsonSchema: {}", jsonSchema);

        JsonNode jsonNode = createJsonNodeFromString(jsonSchema);

        log.info("jsonNode: {}", jsonNode);

        ApiDetails apiDetails = new ApiDetails();
        apiDetails.setId(1L);
        apiDetails.setApiIdentifier(101L);
        apiDetails.setApiSchema(jsonNode);

        apiRepository.save(apiDetails);
    }

    @Test
    public void testFindByApiIdentifierSuccess() {
        ApiDetails apiDetails = apiRepository.findByApiIdentifier(101L).get();

        assertThat(apiDetails).isNotNull();
        assertThat(apiDetails.getApiIdentifier()).isEqualTo(101L);
        assertThat(apiDetails.getApiSchema()).isNotNull();
    }

    @Test
    public void testFindByApiIdentifierError() {
        assertThatThrownBy(() ->
            apiRepository.findByApiIdentifier(100L).orElseThrow(() -> new NoSuchElementException())
        );
    }

    public JsonNode createJsonNodeFromString(String jsonSchema) {
        try {
            return new ObjectMapper().readTree(jsonSchema);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }
}
