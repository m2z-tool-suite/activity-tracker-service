package com.m2z.activity.tracker.controller;

import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClientFactory;
import com.m2z.activity.tracker.config.BearerHttpAuthenticationHandler;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.net.URI;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MyJiraClient {

    private String username;
    private String password;
    private String jiraUrl;
    private JiraRestClient restClient;

    public MyJiraClient(String username, String password, String jiraUrl) {
        this.username = username;
        this.password = password;
        this.jiraUrl = jiraUrl;
        this.restClient = getJiraRestClient();
    }

    private JiraRestClient getJiraRestClient() {
        return new AsynchronousJiraRestClientFactory()
                .create(URI.create(jiraUrl), new BearerHttpAuthenticationHandler(password));
    }
}
