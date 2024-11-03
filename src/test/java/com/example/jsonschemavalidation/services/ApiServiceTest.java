package com.example.jsonschemavalidation.services;

import com.example.jsonschemavalidation.models.ApiDetails;
import com.example.jsonschemavalidation.repositories.ApiRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;

import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class ApiServiceTest {

    @Mock
    private ApiRepository apiRepository;

    @InjectMocks
    private ApiService apiService;

    String validData;
    String invalidData;

    ApiDetails apiDetails = new ApiDetails();

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        String jsonSchema = """
                    {"$schema":"https://json-schema.org/draft-07/schema#","title":"Add person event","description":"add person event for jsonschema","type":"object","properties":{"name":{"type":"string"},"age":{"type":"integer","minimum":18}}}
                """;

        validData = """
                    {"name":"John","age":18}
                """;
        invalidData = """
                    {"name":"John","age":10}
                """;

        apiDetails.setId(1L);
        apiDetails.setApiIdentifier(101L);
        apiDetails.setApiSchema(createJsonNodeFromString(jsonSchema));
    }

    @Test
    public void saveApiSuccess() {
        when(apiRepository.save(apiDetails)).thenReturn(apiDetails);

        ApiDetails newApiDetails = apiService.saveApi(apiDetails);

        assertThat(newApiDetails).isNotNull();
        assertThat(newApiDetails.getApiIdentifier()).isEqualTo(101L);
        assertThat(newApiDetails.getApiSchema()).isEqualTo(apiDetails.getApiSchema());
    }

    @Test
    public void saveApiError() {
        when(apiRepository.save(apiDetails)).thenThrow(new DataIntegrityViolationException("Unique constraint violated"));

        assertThrows(DataIntegrityViolationException.class, () -> apiService.saveApi(apiDetails));
    }

    @Test
    void saveApi_WhenApiDetailsIsNull_ShouldThrowNullPointerException() {
        // Act & Assert
        assertThrows(
                NullPointerException.class,
                () -> apiService.saveApi(null)
        );
    }

    @Test
    public void validateApiSuccess() {
        when(apiRepository.findByApiIdentifier(apiDetails.getApiIdentifier())).thenReturn(Optional.of(apiDetails));

        String validationResult = apiService.validateApi(apiDetails.getApiIdentifier(), createJsonNodeFromString(validData));

        assertThat(validationResult).isNull();
    }

    @Test
    public void validateApiError() {
        when(apiRepository.findByApiIdentifier(apiDetails.getApiIdentifier())).thenReturn(Optional.of(apiDetails));

        String validationResult = apiService.validateApi(apiDetails.getApiIdentifier(), createJsonNodeFromString(invalidData));

        assertThat(validationResult).isNotNull();
        assertThat(validationResult).contains("[$.age: must have a minimum value of 18]");
    }

    @Test
    public void validateApiInvalidApiIdentifier() {
        when(apiRepository.findByApiIdentifier(102L)).thenReturn(Optional.empty());

        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () -> apiService.validateApi(102L, createJsonNodeFromString(validData)));

        assertThat(exception.getMessage()).isEqualTo("API identifier 102 not found.");
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
