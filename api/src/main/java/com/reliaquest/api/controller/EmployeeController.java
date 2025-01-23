package com.reliaquest.api.controller;

import com.reliaquest.api.service.EmployeeService;
import java.util.List;
import org.springframework.http.ResponseEntity;

public class EmployeeController<Employee, Input>
        implements IEmployeeController<com.reliaquest.api.model.Employee, Input> {
    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @Override
    public ResponseEntity<List<com.reliaquest.api.model.Employee>> getAllEmployees() {
        return ResponseEntity.ok(employeeService.getAllEmployees());
    }

    @Override
    public ResponseEntity<List<com.reliaquest.api.model.Employee>> getEmployeesByNameSearch(String searchString) {
        return null;
    }

    @Override
    public ResponseEntity<com.reliaquest.api.model.Employee> getEmployeeById(String id) {
        return null;
    }

    @Override
    public ResponseEntity<Integer> getHighestSalaryOfEmployees() {
        return null;
    }

    @Override
    public ResponseEntity<List<String>> getTopTenHighestEarningEmployeeNames() {
        return null;
    }

    @Override
    public ResponseEntity<com.reliaquest.api.model.Employee> createEmployee(Input employeeInput) {
        return null;
    }

    @Override
    public ResponseEntity<String> deleteEmployeeById(String id) {
        return null;
    }
}
