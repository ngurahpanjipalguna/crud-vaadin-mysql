package com.example.crud.repository;

import com.example.crud.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    @Query("select e from Employee e " +
        "where lower(e.name) like lower(concat('%', :searchTerm, '%')) " +
        "or lower(e.email) like lower(concat('%', :searchTerm, '%'))")
    List<Employee> search(@Param("searchTerm") String searchTerm);
}