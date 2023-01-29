package com.m2z.activity.tracker.controller.impl;

import com.google.api.client.auth.oauth.OAuthParameters;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpHeaders;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.http.json.JsonHttpContent;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.repackaged.org.apache.commons.codec.binary.Base64;
import com.google.common.collect.ImmutableMap;
import org.codehaus.jettison.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Scanner;
import java.util.function.Function;

import static com.m2z.activity.tracker.controller.impl.PropertiesClient.ACCESS_TOKEN;
import static com.m2z.activity.tracker.controller.impl.PropertiesClient.CONSUMER_KEY;
import static com.m2z.activity.tracker.controller.impl.PropertiesClient.PRIVATE_KEY;
import static com.m2z.activity.tracker.controller.impl.PropertiesClient.REQUEST_TOKEN;
import static com.m2z.activity.tracker.controller.impl.PropertiesClient.SECRET;

public class OAuthClient {

    private final Map<Command, Function<List<String>, Optional<Exception>>> actionHandlers;

    private final PropertiesClient propertiesClient;
    private final JiraOAuthClient jiraOAuthClient;

    private String emailPassword;

    public OAuthClient(PropertiesClient propertiesClient, JiraOAuthClient jiraOAuthClient) {
        this.propertiesClient = propertiesClient;
        this.jiraOAuthClient = jiraOAuthClient;
        emailPassword = propertiesClient.getPropertiesOrDefaults().get("emailPassword");

        actionHandlers = ImmutableMap.<Command, Function<List<String>, Optional<Exception>>>builder()
                .put(Command.REQUEST_TOKEN, this::handleGetRequestTokenAction)
                .put(Command.ACCESS_TOKEN, this::handleGetAccessToken)
                .put(Command.REQUEST, this::handleGetRequest)
                .build();
    }

    /**
     * Executes action (if found) with  given lists of arguments
     *
     * @param action
     * @param arguments
     */
    public void execute(Command action, List<String> arguments) {

        String actionName = action.getName();

        switch (actionName) {
            case "request" -> actionHandlers.get(Command.REQUEST).apply(arguments)
                    .ifPresent(Throwable::printStackTrace);

            case "requestToken" -> actionHandlers.get(Command.REQUEST_TOKEN).apply(arguments)
                    .ifPresent(Throwable::printStackTrace);

            case "accessToken" -> actionHandlers.get(Command.ACCESS_TOKEN).apply(arguments)
                    .ifPresent(Throwable::printStackTrace);
        }

    }

    private Optional<Exception> handleUnknownCommand(List<String> arguments) {
        System.out.println("Command not supported. Only " + Command.names() + " are supported.");
        return Optional.empty();
    }

    /**
     * Gets request token and saves it to properties file
     *
     * @param arguments list of arguments: no arguments are needed here
     * @return
     */
    private Optional<Exception> handleGetRequestTokenAction(List<String> arguments) {
        Map<String, String> properties = propertiesClient.getPropertiesOrDefaults();
        try {
            String requestToken = jiraOAuthClient.getAndAuthorizeTemporaryToken(properties.get(CONSUMER_KEY), properties.get(PRIVATE_KEY));
            properties.put(REQUEST_TOKEN, requestToken);
            propertiesClient.savePropertiesToFile(properties);
            return Optional.empty();
        } catch (Exception e) {
            return Optional.of(e);
        }
    }

    /**
     * Gets access token and saves it to properties file
     *
     * @param arguments list of arguments: first argument should be secert (verification code provided by JIRA after request token authorization)
     * @return
     */
    private Optional<Exception> handleGetAccessToken(List<String> arguments) {
        Map<String, String> properties = propertiesClient.getPropertiesOrDefaults();
        String tmpToken = properties.get(REQUEST_TOKEN);
        String secret = arguments.get(0);

        try {
            String accessToken = jiraOAuthClient.getAccessToken(tmpToken, secret, properties.get(CONSUMER_KEY), properties.get(PRIVATE_KEY));
            properties.put(ACCESS_TOKEN, accessToken);
            properties.put(SECRET, secret);
            propertiesClient.savePropertiesToFile(properties);
            return Optional.empty();
        } catch (Exception e) {
            return Optional.of(e);
        }
    }

    /**
     * Makes request to JIRA to provided url and prints response contect
     *
     * @param arguments list of arguments: first argument should be request url
     * @return
     */
    private Optional<Exception> handleGetRequest(List<String> arguments) {
//        WebClient webClient = WebClient.builder().build();
//        String basicAuth = Base64.encodeBase64String(emailPassword.getBytes(StandardCharsets.UTF_8));
//
//        return webClient.get()
//                .uri(URI.create(url))
//                .header("Authorization", "Basic " + basicAuth)
//                .retrieve().bodyToMono(Object.class).block();
//        Map<String, String> properties = propertiesClient.getPropertiesOrDefaults();
//        String tmpToken = properties.get(ACCESS_TOKEN);
//        String secret = properties.get(SECRET);
//        String url = arguments.get(0);
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
        return null;
    }

    public Object handleGetRequestWithResponse(String url) {
        WebClient webClient = WebClient.builder().build();
        String emailPassword = propertiesClient.getPropertiesOrDefaults().get("emailPassword");
        String basicAuth = Base64.encodeBase64String(emailPassword.getBytes(StandardCharsets.UTF_8));

        return webClient.get()
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
//            HttpResponse response = getResponseFromUrl(parameters, new GenericUrl(url));
//
//            return parseResponseReturnObject(response);
//        } catch (Exception e) {
//            System.out.println(e);
//            return null;
//        }
    }

    public Object handlePostRequestWithResponse(String url, Object object) {
        WebClient webClient = WebClient.builder().build();
        String basicAuth = Base64.encodeBase64String(emailPassword.getBytes(StandardCharsets.UTF_8));

        return webClient.post()
                .uri(URI.create(url))
                .header("Authorization", "Basic " + basicAuth)
                .bodyValue(object)
                .retrieve().bodyToMono(Object.class).block();

//        Map<String, String> properties = propertiesClient.getPropertiesOrDefaults();
//        String tmpToken = properties.get(ACCESS_TOKEN);
//        String secret = properties.get(SECRET);
//
//        propertiesClient.savePropertiesToFile(properties);
//
//        try {
//            OAuthParameters parameters = jiraOAuthClient.getParameters(tmpToken, secret, properties.get(CONSUMER_KEY), properties.get(PRIVATE_KEY));
//            HttpResponse response = postRequestFromUrl(parameters, new GenericUrl(url), object);
//
//            return parseResponseReturnObject(response);
//        } catch (Exception e) {
//            System.out.println(e);
//            return null;
//        }
    }

    public Object handleDeleteRequest(String url) {

        WebClient webClient = WebClient.builder().build();
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

    /**
     * Prints response content
     * if response content is valid JSON it prints it in 'pretty' format
     *
     * @param response
     * @throws IOException
     */
    private void parseResponse(HttpResponse response) throws IOException {
        Scanner s = new Scanner(response.getContent()).useDelimiter("\\A");
        String result = s.hasNext() ? s.next() : "";

        try {
            JSONObject jsonObj = new JSONObject(result);
            System.out.println(jsonObj.toString(2));
        } catch (Exception e) {
            System.out.println(result);
        }
    }

    private Object parseResponseReturnObject(HttpResponse response) throws IOException {
        Scanner s = new Scanner(response.getContent()).useDelimiter("\\A");
        String result = s.hasNext() ? s.next() : "";

        try {
            String jsonObj = result;
            return jsonObj;
        } catch (Exception e) {
            System.out.println(result);
        }
        return null;
    }

    /**
     * Authanticates to JIRA with given OAuthParameters and makes request to url
     *
     * @param parameters
     * @param jiraUrl
     * @return
     * @throws IOException
     */
    private static HttpResponse getResponseFromUrl(OAuthParameters parameters, GenericUrl jiraUrl) throws IOException {
        HttpRequestFactory requestFactory = new NetHttpTransport().createRequestFactory(parameters);
        HttpRequest request = requestFactory.buildGetRequest(jiraUrl);
        return request.execute();
    }

    private static HttpResponse postRequestFromUrl(OAuthParameters parameters, GenericUrl jiraUrl, Object object) throws IOException {
        HttpRequestFactory requestFactory = new NetHttpTransport().createRequestFactory(parameters);
        JacksonFactory jsonFactory = new JacksonFactory();
        HttpRequest request = requestFactory.buildPostRequest(jiraUrl, new JsonHttpContent(jsonFactory, object));
        return request.execute();
    }

    private static HttpResponse deleteRequestFromUrl(OAuthParameters parameters, GenericUrl jiraUrl) throws IOException {
        HttpRequestFactory requestFactory = new NetHttpTransport().createRequestFactory(parameters);
        HttpRequest request = requestFactory.buildDeleteRequest(jiraUrl);
        return request.execute();
    }
}
