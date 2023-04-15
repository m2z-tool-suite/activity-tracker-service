package com.m2z.activity.tracker.controller;

import com.m2z.activity.tracker.dto.ExternalTrackerDto;
import com.m2z.activity.tracker.dto.ProjectDto;
import com.m2z.activity.tracker.entity.Project;
import com.m2z.activity.tracker.repository.impl.ProjectRepository;
import com.m2z.activity.tracker.service.impl.ExternalTrackerService;
import com.m2z.activity.tracker.service.impl.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping(value = "/api/project")
public class ProjectController {

    private final OAuthClient oAuthClient = new OAuthClient();

    @Autowired
    private ProjectService projectService;

    @Autowired
    private ExternalTrackerService externalTrackerService;

    @GetMapping("ext-service/{extServiceId}")
    @ResponseStatus(HttpStatus.OK)
    public Object getAllProjects(@PathVariable("extServiceId") Long extServiceId) {
        ExternalTrackerDto externalTracker = externalTrackerService.findOne(extServiceId);

        String format = String.format("%s/rest/api/2/project", externalTracker.getTeamUrl());

        return oAuthClient.handleGetRequestWithResponse(externalTracker, format);
    }

//    @GetMapping("{projectId}")
//    @ResponseStatus(HttpStatus.OK)
//    public Object getOneProject(@PathVariable("projectId") String projectId) {
//        String format = String.format("%s/rest/api/2/project/%s", jiraTeamUrl, projectId);
//
//        return oAuthClient.handleGetRequestWithResponse(externalTracker, format);
//    }

    @PostMapping("ext-service/{extServiceId}")
    @ResponseStatus(HttpStatus.OK)
    public Object create(@RequestBody Project object, @PathVariable("extServiceId") Long extServiceId) {
        ExternalTrackerDto externalTracker = externalTrackerService.findOne(extServiceId);

        String format = String.format("%s/rest/api/2/project", externalTracker.getTeamUrl());

        return oAuthClient.handlePostRequestWithResponse(externalTracker, format, object);
    }

    @DeleteMapping("ext-service/{extServiceId}/project/{projectId}")
    @ResponseStatus(HttpStatus.OK)
    public void delete(@PathVariable("extServiceId") Long extServiceId, @PathVariable("projectId") String projectId) {

        ExternalTrackerDto externalTracker = externalTrackerService.findOne(extServiceId);

        String format = String.format("%s/rest/api/2/project/%s", externalTracker.getTeamUrl(), projectId);

        oAuthClient.handleDeleteRequest(externalTracker, format);
    }

    @PutMapping("ext-service/{extServiceId}/project/{projectId}")
    @ResponseStatus(HttpStatus.OK)
    public ProjectDto update(@PathVariable("extServiceId") Long extServiceId, @RequestBody Project object) {

        ExternalTrackerDto externalTracker = externalTrackerService.findOne(extServiceId);

        String format = String.format("%s/rest/api/2/project", externalTracker.getTeamUrl());

        return (ProjectDto) oAuthClient.handlePostRequestWithResponse(externalTracker, format, object);
    }
}
