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

    private final PropertiesClient propertiesClient;
    private final JiraOAuthClient jiraOAuthClient;
    private final WebClient webClient = WebClient.builder().build();

    public OAuthClient(PropertiesClient propertiesClient, JiraOAuthClient jiraOAuthClient) {
        this.propertiesClient = propertiesClient;
        this.jiraOAuthClient = jiraOAuthClient;
    }

//    public Optional<Exception> handleGetRequestTokenAction() {
//        Map<String, String> properties = propertiesClient.getPropertiesOrDefaults();
//        try {
//            String requestToken = jiraOAuthClient.getAndAuthorizeTemporaryToken(properties.get(CONSUMER_KEY), properties.get(PRIVATE_KEY));
//            properties.put(REQUEST_TOKEN, requestToken);
//            propertiesClient.savePropertiesToFile(properties);
//            return Optional.empty();
//        } catch (Exception e) {
//            return Optional.of(e);
//        }
//    }
//
//    /**
//     * Gets access token and saves it to properties file
//     *
//     * @param arguments list of arguments: first argument should be secert (verification code provided by JIRA after request token authorization)
//     * @return
//     */
//    public Optional<Exception> handleGetAccessToken(List<String> arguments) {
//        Map<String, String> properties = propertiesClient.getPropertiesOrDefaults();
//        String tmpToken = properties.get(REQUEST_TOKEN);
//        String secret = arguments.get(0);
//
//        try {
//            String accessToken = jiraOAuthClient.getAccessToken(tmpToken, secret, properties.get(CONSUMER_KEY), properties.get(PRIVATE_KEY));
//            properties.put(ACCESS_TOKEN, accessToken);
//            properties.put(SECRET, secret);
//            propertiesClient.savePropertiesToFile(properties);
//            return Optional.empty();
//        } catch (Exception e) {
//            return Optional.of(e);
//        }
//    }
//
//
//    public Optional<Exception> handleGetRequest(ExternalTrackerDto externalTracker, String url) {
//
//        Map<String, String> properties = propertiesClient.getPropertiesOrDefaults();
//        String tmpToken = properties.get(ACCESS_TOKEN);
//        String secret = properties.get(SECRET);
//        propertiesClient.savePropertiesToFile(properties);
//
//        try {
//            OAuthParameters parameters = jiraOAuthClient.getParameters(tmpToken, secret, properties.get(CONSUMER_KEY), properties.get(PRIVATE_KEY));
//            HttpResponse response = getResponseFromUrl(parameters, new GenericUrl(url));
//            parseResponse(response);
//            return Optional.empty();
//        } catch (Exception e) {
//            return Optional.of(e);
//        }
//    }

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

//        Map<String, String> properties = propertiesClient.getPropertiesOrDefaults();
//        String tmpToken = properties.get(ACCESS_TOKEN);
//        String secret = properties.get(SECRET);
//
//        propertiesClient.savePropertiesToFile(properties);
//
//        try {
//            OAuthParameters parameters = jiraOAuthClient.getParameters(tmpToken, secret, properties.get(CONSUMER_KEY), properties.get(PRIVATE_KEY));
//            HttpResponse response = deleteRequestFromUrl(parameters, new GenericUrl(url));
//
//            return parseResponseReturnObject(response);
//        } catch (Exception e) {
//            System.out.println(e);
//            return null;
//        }
    }

//    /**
//     * Prints response content
//     * if response content is valid JSON it prints it in 'pretty' format
//     *
//     * @param response
//     * @throws IOException
//     */
//    private void parseResponse(HttpResponse response) throws IOException {
//        Scanner s = new Scanner(response.getContent()).useDelimiter("\\A");
//        String result = s.hasNext() ? s.next() : "";
//
//        try {
//            JSONObject jsonObj = new JSONObject(result);
//            System.out.println(jsonObj.toString(2));
//        } catch (Exception e) {
//            System.out.println(result);
//        }
//    }
//
//    private Object parseResponseReturnObject(HttpResponse response) throws IOException {
//        Scanner s = new Scanner(response.getContent()).useDelimiter("\\A");
//        String result = s.hasNext() ? s.next() : "";
//
//        try {
//            String jsonObj = result;
//            return jsonObj;
//        } catch (Exception e) {
//            System.out.println(result);
//        }
//        return null;
//    }

//    /**
//     * Authanticates to JIRA with given OAuthParameters and makes request to url
//     *
//     * @param parameters
//     * @param jiraUrl
//     * @return
//     * @throws IOException
//     */
//    private static HttpResponse getResponseFromUrl(OAuthParameters parameters, GenericUrl jiraUrl) throws IOException {
//        HttpRequestFactory requestFactory = new NetHttpTransport().createRequestFactory(parameters);
//        HttpRequest request = requestFactory.buildGetRequest(jiraUrl);
//        return request.execute();
//    }
//
//    private static HttpResponse postRequestFromUrl(OAuthParameters parameters, GenericUrl jiraUrl, Object object) throws IOException {
//        HttpRequestFactory requestFactory = new NetHttpTransport().createRequestFactory(parameters);
//        JacksonFactory jsonFactory = new JacksonFactory();
//        HttpRequest request = requestFactory.buildPostRequest(jiraUrl, new JsonHttpContent(jsonFactory, object));
//        return request.execute();
//    }
//
//    private static HttpResponse deleteRequestFromUrl(OAuthParameters parameters, GenericUrl jiraUrl) throws IOException {
//        HttpRequestFactory requestFactory = new NetHttpTransport().createRequestFactory(parameters);
//        HttpRequest request = requestFactory.buildDeleteRequest(jiraUrl);
//        return request.execute();
//    }
}
