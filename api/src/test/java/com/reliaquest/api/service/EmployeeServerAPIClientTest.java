package com.reliaquest.api.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.reliaquest.api.controller.request.DeleteEmployeeInput;
import com.reliaquest.api.controller.request.EmployeeCreationInput;
import com.reliaquest.api.model.Employee;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
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
    void testGet_successfulResponse() {
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

    @Test
    void testPost_successfulResponse() {
        String jsonResponse = "{\"data\":{\"id\":\"596205c5-e4dc-4b0e-89dc-b2ec6dc758ea\",\"name\":\"John Doe\"}}";
        HttpResponse<String> httpResponse = mock(HttpResponse.class);
        when(httpResponse.statusCode()).thenReturn(200);
        when(httpResponse.body()).thenReturn(jsonResponse);

        when(httpClient.sendAsync(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenReturn(CompletableFuture.completedFuture(httpResponse));

        CompletableFuture<Employee> result = apiClient.post(
                "/employees",
                new EmployeeCreationInput("John Doe", 1000, 20, "Engineer", "email"),
                new TypeReference<Employee>() {});

        Employee employee = result.join();
        assertNotNull(employee);
        assertEquals("596205c5-e4dc-4b0e-89dc-b2ec6dc758ea", employee.getId().toString());
        assertEquals("John Doe", employee.getName());
    }

    @Test
    void testDelete_successfulResponse() {
        String jsonResponse = "{\"data\":true}";
        HttpResponse<String> httpResponse = mock(HttpResponse.class);
        when(httpResponse.statusCode()).thenReturn(200);
        when(httpResponse.body()).thenReturn(jsonResponse);

        when(httpClient.sendAsync(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenReturn(CompletableFuture.completedFuture(httpResponse));

        CompletableFuture<Boolean> result = apiClient.delete("/employee", new DeleteEmployeeInput("John Doe"));

        boolean isDeleted = result.join();
        assertTrue(isDeleted);
    }

    @Test
    void testGet_unsuccessfulResponse() {
        HttpResponse<String> httpResponse = mock(HttpResponse.class);
        when(httpResponse.statusCode()).thenReturn(404);
        when(httpResponse.body()).thenReturn("Employee not found");
        when(httpClient.sendAsync(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenReturn(CompletableFuture.completedFuture(httpResponse));

        CompletableFuture<Employee> result =
                apiClient.get("/employees/596205c5-e4dc-4b0e-89dc-b2ec6dc758ea", new TypeReference<>() {});

        assertThrows(CompletionException.class, result::join);
    }

    @Test
    void testPost_unsuccessfulResponse() {
        HttpResponse<String> httpResponse = mock(HttpResponse.class);
        when(httpResponse.statusCode()).thenReturn(400);
        when(httpResponse.body()).thenReturn("Invalid input");
        when(httpClient.sendAsync(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenReturn(CompletableFuture.completedFuture(httpResponse));

        when(httpClient.sendAsync(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenReturn(CompletableFuture.completedFuture(httpResponse));

        CompletableFuture<Employee> result = apiClient.post(
                "/employees",
                new EmployeeCreationInput("John Doe", 1000, 20, "Engineer", "email"),
                new TypeReference<Employee>() {});

        assertThrows(CompletionException.class, result::join);
    }

    @Test
    void testDelete_unsuccessfulResponse() {
        HttpResponse httpResponse = mock(HttpResponse.class);
        when(httpResponse.statusCode()).thenReturn(400);
        when(httpResponse.body()).thenReturn("Invalid input");
        when(httpClient.sendAsync(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenReturn(CompletableFuture.completedFuture(httpResponse));

        CompletableFuture<Boolean> result = apiClient.delete("/employee", new DeleteEmployeeInput("John Doe"));

        assertThrows(CompletionException.class, result::join);
    }
}
