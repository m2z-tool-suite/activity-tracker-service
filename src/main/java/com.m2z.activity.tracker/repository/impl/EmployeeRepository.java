package com.m2z.activity.tracker.repository.impl;


import com.m2z.activity.tracker.entity.Employee;
import com.m2z.activity.tracker.repository.definition.BaseRepository;

import java.util.List;
import java.util.Optional;

public interface EmployeeRepository extends BaseRepository<Employee, String> {

    Optional<Employee> findByEmail(String email);

    Optional<Employee> findByEmailAndId(String email, String accountId);

    List<Employee> findAll();
}
