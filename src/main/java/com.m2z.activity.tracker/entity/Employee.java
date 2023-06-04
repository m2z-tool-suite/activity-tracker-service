package com.m2z.activity.tracker.entity;

import com.m2z.activity.tracker.dto.EmployeeDto;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.checkerframework.common.aliasing.qual.Unique;

import java.util.ArrayList;
import java.util.List;


@Entity
@NoArgsConstructor
@Getter
@Setter
public class Employee {
    @Id
    private String id;
    private String firstname;
    private String lastname;
    private String displayName;
    private String role;

    @Unique
    private String email;

    @ManyToMany
    private List<Project> projects;

    @ManyToOne
    private ExternalTracker externalTracker;

    @OneToMany(mappedBy = "assignee")
    private List<Ticket> assignedTickets = new ArrayList<>();

    public Employee(EmployeeDto employee) {
        id = employee.getId();
        firstname = employee.getFirstname();
        lastname = employee.getLastname();
        displayName = employee.getDisplayName();
        role = employee.getRole();
        email = employee.getEmail();
    }
}
