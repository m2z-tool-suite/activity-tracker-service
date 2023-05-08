package com.m2z.activity.tracker.dto;

import com.m2z.activity.tracker.entity.ExternalTracker;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@NoArgsConstructor
@AllArgsConstructor
@Data
public class ExternalTrackerDto {
    private Long id;
    private String consumerKey;
    private String homeUrl;
    private String teamUrl;
    private String extProjectAdminId;
    private String privateKey;
    private String email;
    private Boolean isActive;

    public ExternalTrackerDto(ExternalTracker element) {
        id = element.getId();
        consumerKey = element.getConsumerKey();
        homeUrl = element.getHomeUrl();
        teamUrl = element.getTeamUrl();
        extProjectAdminId = element.getExtProjectAdminId();
        privateKey = element.getPrivateKey();
        email = element.getEmail();
        isActive = element.getIsActive();
    }
}
