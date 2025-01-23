package com.reliaquest.api.service;

import static com.reliaquest.api.utils.StringUtils.containsString;

import com.fasterxml.jackson.core.type.TypeReference;
import com.reliaquest.api.model.Employee;
import java.util.Comparator;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class EmployeeService {
    private final EmployeeServerAPIClient employeeServerApiClient;
    private final TypeReference<List<Employee>> typeReference = new TypeReference<>() {};

    public EmployeeService(EmployeeServerAPIClient employeeServerApiClient) {
        this.employeeServerApiClient = employeeServerApiClient;
    }

    public List<Employee> getAllEmployees() {
        return employeeServerApiClient.get("/api/v1/employee", typeReference).join();
    }

    public List<Employee> getEmployeesByNameSearch(String searchString) {
        List<Employee> employees = getAllEmployees();
        log.debug("Searching for input string: {} in {} employees", searchString, employees.size());

        return employees.stream()
                .filter(e -> containsString(e.getName(), searchString))
                .toList();
    }

    public Integer getHighestSalaryOfEmployees() {
        List<Employee> allEmployees = getAllEmployees();
        log.debug("Get highest salary of employee out of {} employees", allEmployees.size());

        return allEmployees.stream().mapToInt(Employee::getSalary).max().orElse(0);
    }

    public Employee getEmployeeById(String id) {
        log.debug("Getting employee for ID: {}", id);

        return employeeServerApiClient
                .get("/api/v1/employee/" + id, new TypeReference<Employee>() {})
                .join();
    }

    public List<Employee> getTopEmployeesBySalary(Integer limit) {
        List<Employee> allEmployees = getAllEmployees();

        log.debug("Returning top {} earning employees in {} employees", limit, allEmployees.size());

        return allEmployees.stream()
                .sorted(Comparator.comparing(Employee::getSalary).reversed())
                .limit(limit)
                .toList();
    }
}
