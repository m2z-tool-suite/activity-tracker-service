package com.m2z.activity.tracker.controller;

import com.m2z.activity.tracker.dto.ProjectDto;
import com.m2z.activity.tracker.entity.Project;
import com.m2z.activity.tracker.service.impl.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping(value = "/api/project")
public class ProjectController {

    @Autowired
    private ProjectService projectService;

    @GetMapping("ext-service/{extServiceId}")
    @ResponseStatus(HttpStatus.OK)
    public Object getAllProjects(@PathVariable("extServiceId") Long extServiceId) {

        return projectService.getAllProjects(extServiceId);
    }

    @PostMapping("ext-service/{extServiceId}")
    @ResponseStatus(HttpStatus.OK)
    public Object create(@RequestBody Project object, @PathVariable("extServiceId") Long extServiceId) {

        return projectService.saveProject(extServiceId, object);
    }

    @DeleteMapping("ext-service/{extServiceId}/project/{projectId}")
    @ResponseStatus(HttpStatus.OK)
    public void delete(@PathVariable("extServiceId") Long extServiceId, @PathVariable("projectId") String projectId) {

        projectService.deleteProject(extServiceId, projectId);
    }

    @PutMapping("ext-service/{extServiceId}/project/{projectId}")
    @ResponseStatus(HttpStatus.OK)
    public ProjectDto update(@PathVariable("extServiceId") Long extServiceId, @PathVariable("projectId") String projectId, @RequestBody Project object) {


        object.setId(projectId);
        return (ProjectDto) projectService.updateProject(extServiceId, object);
    }
}
