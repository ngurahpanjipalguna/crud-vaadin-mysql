package com.example.crud.service;

import com.example.crud.entity.Employee;
import com.example.crud.repository.EmployeeRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmployeeService {

    private final EmployeeRepository employeeRepository;

    public EmployeeService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    public List<Employee> findAll(String stringFilter) {
        if (stringFilter == null || stringFilter.isEmpty()) {
            return employeeRepository.findAll();
        } else {
            return employeeRepository.search(stringFilter);
        }
    }

    public Employee save(Employee employee) {
        return employeeRepository.save(employee);
    }

    public void delete(Employee employee) {
        employeeRepository.delete(employee);
    }
}