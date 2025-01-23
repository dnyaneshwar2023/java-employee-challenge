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
    private final EmployeeService employeeService = new EmployeeService(employeeServerApiClient);
    List<Employee> mockEmployeeList = List.of(
            new Employee(UUID.randomUUID(), "John Doe", 1000, 22, "Software Engineer", "john@gmail.com"),
            new Employee(UUID.randomUUID(), "Jake Luther", 2000, 22, "Security Engineer", "jake@gmail.com"),
            new Employee(UUID.randomUUID(), "Will Jacks", 500, 22, "Software Engineer", "will@gmail.com"));

    @Test
    void itShouldGetAllEmployeesFromServer() {
        when(employeeServerApiClient.get(any(), any())).thenReturn(CompletableFuture.completedFuture(mockEmployeeList));

        List<Employee> receivedEmployees = employeeService.getAllEmployees();

        assertEquals(3, receivedEmployees.size());
    }

    @Test
    void itShouldReturnEmployeesMatchingToGivenInput() {
        String searchString = "Ja";
        when(employeeServerApiClient.get(any(), any())).thenReturn(CompletableFuture.completedFuture(mockEmployeeList));

        List<Employee> receivedEmployees = employeeService.getEmployeesByNameSearch(searchString);
        assertEquals(2, receivedEmployees.size());
    }

    @Test
    void itShouldReturnHighestSalaryOfEmployee() {
        when(employeeServerApiClient.get(any(), any())).thenReturn(CompletableFuture.completedFuture(mockEmployeeList));

        Integer highestSalary = employeeService.getHighestSalaryOfEmployees();
        assertEquals(2000, highestSalary);
    }

    @Test
    void itShouldReturnZeroAsHighestSalaryWhenNoEmployeesArePresent() {
        when(employeeServerApiClient.get(any(), any())).thenReturn(CompletableFuture.completedFuture(List.of()));
        Integer highestSalary = employeeService.getHighestSalaryOfEmployees();
        assertEquals(0, highestSalary);
    }
}
