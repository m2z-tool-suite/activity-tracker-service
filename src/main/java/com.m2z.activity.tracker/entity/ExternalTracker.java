package com.m2z.activity.tracker.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
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
    private String password;
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
        password = changedT.getPassword();
        isActive = changedT.getIsActive();
    }
}
