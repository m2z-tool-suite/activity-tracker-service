package com.m2z.activity.tracker.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;


@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ExternalTracker {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String consumerKey;
    private String homeUrl;
    private String teamUrl;
    private String extProjectAdminId;
    @Column(length = 5000)
    private String privateKey;
    private String email;
    private Boolean isActive;

    @OneToMany
    private List<Employee> employees;

    @OneToMany(mappedBy = "externalTracker")
    private List<Project> projects = new ArrayList<>();

    public void update(ExternalTracker changedT) {

        name = changedT.getName();
        consumerKey = changedT.getConsumerKey();
        homeUrl = changedT.getHomeUrl();
        teamUrl = changedT.getTeamUrl();
        extProjectAdminId = changedT.getExtProjectAdminId();
        privateKey = changedT.getPrivateKey();
        email = changedT.getEmail();
        isActive = changedT.getIsActive();
    }
}
