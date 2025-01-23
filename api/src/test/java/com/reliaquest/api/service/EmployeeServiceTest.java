package com.reliaquest.api.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.reliaquest.api.controller.request.EmployeeCreationInput;
import com.reliaquest.api.exception.APIException;
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

    @Test
    void itShouldReturnEmployeeByGivenId() {
        String id = "abcbc123-4567-890a-bcde-fghij123456789";
        Employee mockEmployee =
                new Employee(UUID.randomUUID(), "John Doe", 1000, 22, "Software Engineer", "john@gmail.com");
        when(employeeServerApiClient.get(any(), any())).thenReturn(CompletableFuture.completedFuture(mockEmployee));

        Employee receivedEmployee = employeeService.getEmployeeById(id);

        assertEquals(mockEmployee, receivedEmployee);
    }

    @Test
    void itShouldThrowExceptionIfEmployeeNotFound() {
        String id = "abcbc123-4567-890a-bcde-fghij123456789";
        when(employeeServerApiClient.get(any(), any())).thenThrow(new APIException(404, "Employee not found"));

        assertThrows(APIException.class, () -> employeeService.getEmployeeById(id));
    }

    @Test
    void itShouldReturnTopKEmployeesBySalary() {
        Integer k = 2;
        when(employeeServerApiClient.get(any(), any())).thenReturn(CompletableFuture.completedFuture(mockEmployeeList));

        List<Employee> receivedEmployees = employeeService.getTopEmployeesBySalary(k);

        assertEquals(2, receivedEmployees.size());
        assertEquals(2000, receivedEmployees.get(0).getSalary());
        assertEquals(1000, receivedEmployees.get(1).getSalary());
        assertEquals("Jake Luther", receivedEmployees.get(0).getName());
        assertEquals("John Doe", receivedEmployees.get(1).getName());
    }

    @Test
    void itShouldCreatedEmployeeBasedOnGivenInput() {
        EmployeeCreationInput input = new EmployeeCreationInput("John", 9878374, 25, "Tech lead", "john@rq.com");
        Employee mockCreatedEmployee = Employee.newEmployee(input);
        when(employeeServerApiClient.post(any(), any(), any()))
                .thenReturn(CompletableFuture.completedFuture(mockCreatedEmployee));

        Employee createdEmployee = employeeService.createEmployee(input);

        verify(employeeServerApiClient, times(1)).post(any(), any(), any());
        assertEquals(mockCreatedEmployee.getName(), createdEmployee.getName());
        assertEquals(mockCreatedEmployee.getAge(), createdEmployee.getAge());
        assertEquals(mockCreatedEmployee.getEmail(), createdEmployee.getEmail());
        assertEquals(mockCreatedEmployee.getTitle(), createdEmployee.getTitle());
    }
}
