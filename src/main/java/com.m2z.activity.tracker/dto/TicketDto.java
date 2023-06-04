package com.m2z.activity.tracker.dto;

import com.m2z.activity.tracker.entity.Ticket;
import com.m2z.activity.tracker.entity.TicketType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;


@NoArgsConstructor
@Getter
@Setter
public class TicketDto {
    private String id;
    private String summary;
    private String description;
    private String typeName;
    private TicketType type;
    private ProjectDto project;
    private String projectKey;
    private EmployeeDto assignee;
    private String selectedAssigneeId;
    private String estimatedStartDate;
    private String estimatedEndDate;
    private String assigneeId;
    private String loggedUserEmail;

    public TicketDto(Map<String, Object> issueMap) {
        this.id = (String) issueMap.get("key");
        Map<String, Object> fields = (Map<String, Object>) issueMap.get("fields");
        this.summary = (String) fields.get("summary");
        this.description = (String) fields.get("description");

        this.type = new TicketType((Map<String, Object>) fields.get("issuetype"));
        this.typeName = type.getName();
        Map<String, String> project1 = (Map<String, String>)fields.get("project");
        projectKey = project1.get("key");
        Map<String, String> assignee = (Map<String, String>)fields.get("assignee");
        assigneeId = assignee.get("accountId");

    }

    public TicketDto(Ticket ticket) {
        id = ticket.getId();
        summary = ticket.getSummary();
        description = ticket.getDescription();
        type = ticket.getType();
        project = new ProjectDto(ticket.getProject());
        assignee = new EmployeeDto(ticket.getAssignee());
        typeName = type.getName();
    }
}
