package com.m2z.activity.tracker.dto;

import com.m2z.activity.tracker.entity.Employee;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@NoArgsConstructor
@AllArgsConstructor
@Data
public class ProjectAdminLogin {
    private String email;

    public ProjectAdminLogin(Employee adminDto) {
        email = adminDto.getEmail();
    }
}
