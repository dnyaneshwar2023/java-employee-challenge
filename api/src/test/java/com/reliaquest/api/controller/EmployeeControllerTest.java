package com.reliaquest.api.controller;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import com.reliaquest.api.controller.request.DeleteEmployeeInput;
import com.reliaquest.api.controller.request.EmployeeCreationInput;
import com.reliaquest.api.model.Employee;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class EmployeeControllerTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @RegisterExtension
    public static WireMockExtension employeeServerWireMockRule =
            WireMockExtension.newInstance().options(wireMockConfig().port(8112)).build();

    List<Employee> mockEmployeeList = List.of(
            new Employee(UUID.randomUUID(), "John Doe", 1000, 22, "Software Engineer", "john@gmail.com"),
            new Employee(UUID.randomUUID(), "Jake Luther", 2000, 22, "Security Engineer", "jake@gmail.com"),
            new Employee(UUID.randomUUID(), "Will Jacks", 500, 22, "Software Engineer", "will@gmail.com"));

    EmployeeServerMocks employeeServerMocks = new EmployeeServerMocks(employeeServerWireMockRule);

    @Test
    void shouldReturnAllEmployees() throws Exception {
        employeeServerMocks.mockGetApiCall(
                "/api/v1/employee", 200, getEnclosedResponse(objectMapper.writeValueAsString(mockEmployeeList)));

        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$.[0].name").value("John Doe"))
                .andExpect(jsonPath("$.[1].name").value("Jake Luther"))
                .andExpect(jsonPath("$.[2].name").value("Will Jacks"));
    }

    @Test
    void itShouldReturnEmployeeById() throws Exception {
        Employee mockEmployee = mockEmployeeList.get(0);
        employeeServerMocks.mockGetApiCall(
                "/api/v1/employee/" + mockEmployee.getId(),
                200,
                getEnclosedResponse(objectMapper.writeValueAsString(mockEmployee)));

        mockMvc.perform(get("/" + mockEmployee.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("John Doe"))
                .andExpect(jsonPath("$.age").value(22))
                .andExpect(jsonPath("$.salary").value(1000))
                .andExpect(jsonPath("$.title").value("Software Engineer"));
    }

    @Test
    void shouldReturnErrorWhileGettingEmployeeByIdWhenEmployeeDoesNotExist() throws Exception {
        Employee mockEmployee = mockEmployeeList.get(0);
        employeeServerMocks.mockGetApiCall(
                "/api/v1/employee/" + mockEmployee.getId(), 404, "Employee not found with ID " + mockEmployee.getId());

        mockMvc.perform(get("/" + mockEmployee.getId()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$").value("Employee not found with ID " + mockEmployee.getId()));
    }

    @Test
    void itShouldSearchEmployeeByName() throws Exception {
        employeeServerMocks.mockGetApiCall(
                "/api/v1/employee", 200, getEnclosedResponse(objectMapper.writeValueAsString(mockEmployeeList)));

        mockMvc.perform(get("/search/Ja"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$.[0].name").value("Jake Luther"))
                .andExpect(jsonPath("$.[1].name").value("Will Jacks"));
    }

    @Test
    void shouldReturnHighestSalaryOfEmployee() throws Exception {
        employeeServerMocks.mockGetApiCall(
                "/api/v1/employee", 200, getEnclosedResponse(objectMapper.writeValueAsString(mockEmployeeList)));

        mockMvc.perform(get("/highestSalary"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(2000));
    }

    @Test
    void shouldReturnNamesOfTop10EarningEmployees() throws Exception {
        employeeServerMocks.mockGetApiCall(
                "/api/v1/employee", 200, getEnclosedResponse(objectMapper.writeValueAsString(mockEmployeeList)));

        mockMvc.perform(get("/topTenHighestEarningEmployeeNames"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$.[0]").value("Jake Luther"))
                .andExpect(jsonPath("$.[1]").value("John Doe"))
                .andExpect(jsonPath("$.[2]").value("Will Jacks"));
    }

    @Test
    void shouldCreateEmployee() throws Exception {
        EmployeeCreationInput employeeCreationInput =
                new EmployeeCreationInput("John Clair", 1000, 30, "CTO", "john@rq.com");

        String createEmployeeResponse =
                """
      {
        "data": {
          "id": "4a3a170b-22cd-4ac2-aad1-9bb5b34a1507",
          "employee_name": "John Clair",
          "employee_salary": 1000,
          "employee_age": 30,
          "employee_title": "CTO",
          "employee_email": "john@rq.com"
        },
        "status": "Successfully processed request."
      }
      """;

        employeeServerMocks.mockPostApiCall(
                "/api/v1/employee",
                200,
                objectMapper.writeValueAsString(employeeCreationInput),
                createEmployeeResponse);

        mockMvc.perform(post("/")
                        .content(objectMapper.writeValueAsString(employeeCreationInput))
                        .contentType("application/json"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("4a3a170b-22cd-4ac2-aad1-9bb5b34a1507"))
                .andExpect(jsonPath("$.name").value("John Clair"))
                .andExpect(jsonPath("$.age").value(30))
                .andExpect(jsonPath("$.salary").value(1000))
                .andExpect(jsonPath("$.title").value("CTO"));
    }

    @Test
    void shouldDeleteEmployeeById() throws Exception {
        Employee mockEmployee = mockEmployeeList.get(0);
        employeeServerMocks.mockGetApiCall(
                "/api/v1/employee/" + mockEmployee.getId(),
                200,
                getEnclosedResponse(objectMapper.writeValueAsString(mockEmployee)));

        String deleteReponse = """
      {
        "data": true
      }
      """;

        String deleteEmployeeInput = objectMapper.writeValueAsString(new DeleteEmployeeInput(mockEmployee.getName()));
        employeeServerMocks.mockDeleteApiCall("/api/v1/employee", 200, deleteEmployeeInput, deleteReponse);

        mockMvc.perform(delete("/" + mockEmployee.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(mockEmployee.getName()));
    }

    @Test
    void itShouldReturnBadRequestIfEmployeeNotFoundWhileDeleting() throws Exception {
        Employee mockEmployee = mockEmployeeList.get(0);
        employeeServerMocks.mockGetApiCall(
                "/api/v1/employee/" + mockEmployee.getId(), 200, getEnclosedResponse("null"));

        mockMvc.perform(delete("/" + mockEmployee.getId()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").value("Employee not found with ID " + mockEmployee.getId()));
    }

    String getEnclosedResponse(String response) {
        return """
      {
        "data" : %s
      }
      """.formatted(response);
    }
}
