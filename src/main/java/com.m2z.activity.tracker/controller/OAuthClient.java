package com.m2z.activity.tracker.controller;

import com.google.api.client.auth.oauth.OAuthParameters;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.http.json.JsonHttpContent;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.repackaged.org.apache.commons.codec.binary.Base64;
import com.m2z.activity.tracker.config.CryptUtils;
import com.m2z.activity.tracker.dto.ExternalTrackerDto;
import com.m2z.activity.tracker.entity.Project;
import org.codehaus.jettison.json.JSONObject;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Scanner;

import static com.m2z.activity.tracker.controller.PropertiesClient.ACCESS_TOKEN;
import static com.m2z.activity.tracker.controller.PropertiesClient.CONSUMER_KEY;
import static com.m2z.activity.tracker.controller.PropertiesClient.PRIVATE_KEY;
import static com.m2z.activity.tracker.controller.PropertiesClient.REQUEST_TOKEN;
import static com.m2z.activity.tracker.controller.PropertiesClient.SECRET;


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
