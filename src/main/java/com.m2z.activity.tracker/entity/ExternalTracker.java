package com.m2z.activity.tracker.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
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

    private String consumerKey;
    private String homeUrl;
    private String teamUrl;
    private String extProjectAdminId;
    @Column(length = 5000)
    private String privateKey;
    private String email;
    private Boolean isActive;

    @ManyToOne
    private ProjectAdmin projectAdmin;

    @OneToMany(mappedBy = "externalTracker")
    private List<Project> projects = new ArrayList<>();

    public void update(ExternalTracker changedT) {

        consumerKey = changedT.getConsumerKey();
        homeUrl = changedT.getHomeUrl();
        teamUrl = changedT.getTeamUrl();
        extProjectAdminId = changedT.getExtProjectAdminId();
        privateKey = changedT.getPrivateKey();
        email = changedT.getEmail();
        isActive = changedT.getIsActive();
    }
}
