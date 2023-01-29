package com.m2z.activity.tracker.dto;

import com.m2z.activity.tracker.entity.Project;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProjekatDto {
    private String key;
    private String name;
    private String projectTypeKey = "software";
    private String leadAccountId = "63729ee5f6c85b343c06a2c8";
    private String assigneeType = "PROJECT_LEAD";

    public ProjekatDto(Project sastojak) {
        this.name = sastojak.getName();
    }

    public ProjekatDto(String key, String name) {
        this.key = key;
        this.name = name;
    }
}
