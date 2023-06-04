package com.m2z.activity.tracker.dto;

import com.m2z.activity.tracker.entity.Employee;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;


@NoArgsConstructor
@Getter
@Setter
public class EmployeeDto {
    private String id;
    private String firstname;
    private String lastname;
    private String displayName;
    private String email;
    private String role;

    public EmployeeDto(Employee element) {
        id = element.getId();
        firstname = element.getFirstname();
        lastname = element.getLastname();
        displayName = element.getDisplayName();
        email = element.getEmail();
        role = element.getRole();
    }

    public EmployeeDto(Map<String, String> map) {
        id = map.get("accountId");
        displayName = map.get("displayName");
        email = map.get("emailAddress");
    }
}
