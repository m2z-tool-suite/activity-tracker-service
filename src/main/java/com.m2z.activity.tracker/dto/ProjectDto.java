package com.m2z.activity.tracker.dto;

import com.m2z.activity.tracker.entity.Project;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProjectDto {
    private String id;
    private String name;
    private String description;
    private String projectTypeKey = "software";
    private String leadAccountId = "63729ee5f6c85b343c06a2c8";
    private String assigneeType = "PROJECT_LEAD";

    public ProjectDto(Project sastojak) {
        this.name = sastojak.getName();
    }

    public ProjectDto(Map<String, String> mapResponse) {
        id = mapResponse.get("key");
        name = mapResponse.get("name");
        description = mapResponse.get("description");
        projectTypeKey = mapResponse.get("projectTypeKey");
    }

    public ProjectDto(String id, String name) {
        this.id = id;
        this.name = name;
    }
}
