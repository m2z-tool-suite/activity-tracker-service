package com.m2z.activity.tracker.controller;

import com.google.api.client.repackaged.org.apache.commons.codec.binary.Base64;
import com.m2z.activity.tracker.config.CryptUtils;
import com.m2z.activity.tracker.dto.ExternalTrackerDto;
import com.m2z.activity.tracker.entity.Project;
import org.springframework.web.reactive.function.client.WebClient;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.Map;



public class OAuthClient {

    private final WebClient webClient = WebClient.builder().build();

    public Object handleGetRequestWithResponse(ExternalTrackerDto externalTracker, String url) {

        String emailPassword = String.format("%s:%s", externalTracker.getEmail(), CryptUtils.decrypt(externalTracker.getPrivateKey()));

        String basicAuth = Base64.encodeBase64String(emailPassword.getBytes(StandardCharsets.UTF_8));

        return webClient.get()
                .uri(URI.create(url))
                .header("Authorization", "Basic " + basicAuth)
                .retrieve().bodyToMono(Object.class).block();
    }

    public Object handlePostRequestWithResponse(ExternalTrackerDto externalTracker, String url, Project project) {

        String emailPassword = String.format("%s:%s", externalTracker.getEmail(), CryptUtils.decrypt(externalTracker.getPrivateKey()));

        String basicAuth = Base64.encodeBase64String(emailPassword.getBytes(StandardCharsets.UTF_8));

        return webClient.post()
                .uri(URI.create(url))
                .header("Authorization", "Basic " + basicAuth)
                .bodyValue(Map.of(
                        "name", project.getName(),
                        "key", project.getName().toUpperCase(Locale.ROOT),
                        "projectTypeKey", "software",
                        "assigneeType", "PROJECT_LEAD",
                        "leadAccountId", externalTracker.getExtProjectAdminId()
                ))
                .retrieve().bodyToMono(Object.class).block();
    }

    public Object handleDeleteRequest(ExternalTrackerDto externalTracker, String url) {

        String emailPassword = String.format("%s:%s", externalTracker.getEmail(), CryptUtils.decrypt(externalTracker.getPrivateKey()));

        String basicAuth = Base64.encodeBase64String(emailPassword.getBytes(StandardCharsets.UTF_8));

        return webClient.delete()
                .uri(URI.create(url))
                .header("Authorization", "Basic " + basicAuth)
                .retrieve().bodyToMono(Object.class).block();
//        }
    }
}
