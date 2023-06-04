package com.m2z.activity.tracker.dto;

import com.m2z.activity.tracker.entity.TicketType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@NoArgsConstructor
@Getter
@Setter
public class TicketDtoInType {
    private String dbId;
    private String title;
    private String description;
    private TicketType type;
    private ProjectDto project;
    private EmployeeDto assignee;
}
