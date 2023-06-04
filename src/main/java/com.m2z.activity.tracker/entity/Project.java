package com.m2z.activity.tracker.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;


@Entity
@NoArgsConstructor
@Getter
@Setter
public class Project {
    @Id
    private String id;
    private String name;
    private String description;

    @ManyToOne
    private ExternalTracker externalTracker;

    @ManyToMany
    private List<Employee> employees = new ArrayList<>();

    @OneToMany(mappedBy = "project")
    private List<Ticket> tickets = new ArrayList<>();

    public void prePre(){

    }
}
