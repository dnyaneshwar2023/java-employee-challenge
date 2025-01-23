package com.reliaquest.api.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.reliaquest.api.model.Employee;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class EmployeeService {
    private final EmployeeServerAPIClient employeeServerApiClient;
    private final TypeReference<List<Employee>> typeReference = new TypeReference<>() {};

    public EmployeeService(EmployeeServerAPIClient employeeServerApiClient) {
        this.employeeServerApiClient = employeeServerApiClient;
    }

    public List<Employee> getAllEmployees() {
        return employeeServerApiClient.get("/api/v1/employee", typeReference).join();
    }
}
