package com.m2z.activity.tracker.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;


@Entity
@NoArgsConstructor
@Getter
@Setter
public class Ticket {
    @Id
    private String id;
    private String summary;
    private String description;

    private String lastUpdatedBy;
    private String createdBy;
    private String updatedBy;

    private String dateCreated = Instant.now().toString();
    private String dateUpdated = Instant.now().toString();
    private String estimatedStartDate;
    private String estimatedEndDate;
    private String startDate;
    private String endDate;

    @ManyToOne
    private TicketType type;

    @ManyToOne
    private Project project;

    @ManyToOne
    private Employee assignee;

    @PreUpdate
    public void preUpdate(){
        dateUpdated = Instant.now().toString();
    }
}
