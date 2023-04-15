package com.m2z.activity.tracker.dto;

import com.m2z.activity.tracker.entity.ProjectAdmin;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;


@NoArgsConstructor
@AllArgsConstructor
@Data
public class ProjectAdminDto {
    private Long id;

    private String name;
    private String lastName;

    private List<ExternalTrackerDto> externalTrackers;
    private String jiraHome;
    private String privateKey;
    private String email;
    private String password;

    public ProjectAdminDto(ProjectAdmin element) {
        id = element.getId();
        name = element.getFirstname();
        lastName = element.getLastname();
        externalTrackers = element.getExternalTrackers().stream().map(ExternalTrackerDto::new).collect(Collectors.toList());
        email = element.getEmail();
        password = element.getPassword();
    }
}
