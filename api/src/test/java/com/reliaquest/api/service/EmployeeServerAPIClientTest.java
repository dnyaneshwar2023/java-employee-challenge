package com.reliaquest.api.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.reliaquest.api.model.Employee;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class EmployeeServerAPIClientTest {

    private HttpClient httpClient;
    private ObjectMapper objectMapper;
    private EmployeeServerAPIClient apiClient;

    @BeforeEach
    void setup() {
        httpClient = mock(HttpClient.class);
        objectMapper = new ObjectMapper();
        apiClient = new EmployeeServerAPIClient(httpClient, "http://localhost:8080", objectMapper);
    }

    @Test
    void testGet_successfulResponse() throws Exception {
        String jsonResponse = "{\"data\":{\"id\":\"596205c5-e4dc-4b0e-89dc-b2ec6dc758ea\",\"name\":\"John Doe\"}}";
        HttpResponse<String> httpResponse = mock(HttpResponse.class);
        when(httpResponse.statusCode()).thenReturn(200);
        when(httpResponse.body()).thenReturn(jsonResponse);

        when(httpClient.sendAsync(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenReturn(CompletableFuture.completedFuture(httpResponse));

        CompletableFuture<Employee> result =
                apiClient.get("/employees/596205c5-e4dc-4b0e-89dc-b2ec6dc758ea", new TypeReference<>() {});

        Employee employee = result.join();
        assertNotNull(employee);
        assertEquals("596205c5-e4dc-4b0e-89dc-b2ec6dc758ea", employee.getId().toString());
        assertEquals("John Doe", employee.getName());
    }
}
