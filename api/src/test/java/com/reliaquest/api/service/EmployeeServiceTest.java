package com.reliaquest.api.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.reliaquest.api.model.Employee;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class EmployeeServiceTest {
    private final EmployeeServerAPIClient employeeServerApiClient = Mockito.mock(EmployeeServerAPIClient.class);
    private EmployeeService employeeService = new EmployeeService(employeeServerApiClient);

    @Test
    void itShouldGetAllEmployeesFromServer() {

        List<Employee> mockEmployeeList =
                List.of(new Employee(UUID.randomUUID(), "John", 1000, 22, "Software Engineer", "john@gmail.com"));

        when(employeeServerApiClient.get(any(), any())).thenReturn(CompletableFuture.completedFuture(mockEmployeeList));

        List<Employee> receivedEmployees = employeeService.getAllEmployees();

        assertEquals(receivedEmployees.size(), 1);
    }
}
