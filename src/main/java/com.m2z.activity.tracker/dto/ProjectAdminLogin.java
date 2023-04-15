package com.m2z.activity.tracker.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@NoArgsConstructor
@AllArgsConstructor
@Data
public class ProjectAdminLogin {
    private String email;
    private String password;

    public ProjectAdminLogin(ProjectAdminDto adminDto) {
        email = adminDto.getEmail();
        password = adminDto.getPassword();
    }
}
