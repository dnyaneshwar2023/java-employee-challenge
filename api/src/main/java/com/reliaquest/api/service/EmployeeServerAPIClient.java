package com.reliaquest.api.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;

@Component
class EmployeeServerAPIClient {
  private final HttpClient httpClient;
  private final String baseUrl;
  private final ObjectMapper objectMapper;

  public EmployeeServerAPIClient(HttpClient httpClient,
                                 @Value("${urls.employee_server_base_url}")
                                 String baseUrl,
                                 ObjectMapper objectMapper) {
    this.httpClient = httpClient;
    this.baseUrl = baseUrl;
    this.objectMapper = objectMapper;
  }

  public <T> CompletableFuture<T> get(String uri, TypeReference<T> typeReference) {
    HttpRequest request = HttpRequest.newBuilder(URI.create(baseUrl + uri)).GET().build();
    return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
      .thenApply(response -> {
        String body = response.body();
        try {
          JsonNode responseString = objectMapper.readValue(body, JsonNode.class);
          return objectMapper.readValue(responseString.get("data").toString(), typeReference);
        } catch (JsonProcessingException e) {
          throw new RuntimeException(e);
        }
      });
  }
}
