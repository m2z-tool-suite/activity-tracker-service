package com.m2z.activity.tracker.service.impl;

import com.m2z.activity.tracker.dto.ExternalTrackerDto;
import com.m2z.activity.tracker.dto.ProjectDto;
import com.m2z.activity.tracker.entity.Project;
import com.m2z.activity.tracker.repository.impl.ExternalTrackerRepository;
import com.m2z.activity.tracker.repository.impl.ProjectRepository;
import com.m2z.activity.tracker.service.definition.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.net.URI;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import static com.m2z.activity.tracker.service.impl.UtilsMethods.getBasicAuth;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Service
public class ProjectService extends BaseService<Project, ProjectDto, String> {

    @Autowired
    private ExternalTrackerService externalTrackerService;

    @Autowired
    private ExternalTrackerRepository externalTrackerRepository;
    private final String BASE_FORMAT_PATH = "%s/rest/api/2/project";
    private final String DELETE_PATH = "%s/rest/api/2/project/%s";
    private final String UPDATE_PATH = "%s/rest/api/2/project/%s";
    private final WebClient webClient = WebClient.builder().build();

    private final ProjectRepository repository;

    public ProjectService(ProjectRepository repository) {
        super(repository);
        this.repository = repository;
    }

    @Override
    public ProjectDto convertToDTO(Project element) {
        return new ProjectDto(element);
    }


    public Object getAllProjects(Long extServiceId) {

        ExternalTrackerDto externalTracker = externalTrackerService.findOne(extServiceId);

        String url = String.format(BASE_FORMAT_PATH, externalTracker.getTeamUrl());

        List<ProjectDto> collect = (List<ProjectDto>) webClient.get()
                .uri(URI.create(url))
                .header(AUTHORIZATION, "Basic " + getBasicAuth(externalTracker))
                .retrieve().bodyToMono(List.class).block()
                .stream().map(project -> new ProjectDto((Map) project)).collect(Collectors.toList());

        collect.forEach(project -> project.setDescription(repository.getById(project.getId()).getDescription()));

        return collect;
    }

    public Object updateProject(Long extServiceId, Project project) {

        ExternalTrackerDto externalTracker = externalTrackerService.findOne(extServiceId);

        String url = String.format(UPDATE_PATH, externalTracker.getTeamUrl(), project.getId());

        project.setDescription(project.getDescription() != null ? project.getDescription() : "");

        Object block = webClient.put()
                    .uri(URI.create(url))
                    .header(AUTHORIZATION, "Basic " + getBasicAuth(externalTracker))
                    .bodyValue(Map.of(
                            "description", project.getDescription()
                    ))
                    .retrieve().bodyToMono(Object.class).block();

        repository.save(project);

        return block;
    }

    public Object saveProject(Long extServiceId, Project project) {

        ExternalTrackerDto externalTracker = externalTrackerService.findOne(extServiceId);

        String url = String.format(BASE_FORMAT_PATH, externalTracker.getTeamUrl());

        project.setExternalTracker(externalTrackerRepository.getById(extServiceId));
        project.setId(project.getName().toUpperCase(Locale.ROOT));

        Object block = webClient.post()
                    .uri(URI.create(url))
                    .header(AUTHORIZATION, "Basic " + getBasicAuth(externalTracker))
                    .bodyValue(Map.of(
                            "name", project.getName(),
                            "key", project.getName().toUpperCase(Locale.ROOT),
                            "projectTypeKey", "software",
                            "assigneeType", "PROJECT_LEAD",
                            "description", project.getDescription() != null ? project.getDescription() : "",
                            "leadAccountId", externalTracker.getExtProjectAdminId()
                    ))
                    .retrieve().bodyToMono(Object.class).block();

        repository.save(project);

        return block;
    }

    public Object deleteProject(Long extServiceId, String projectId) {

        ExternalTrackerDto externalTracker = externalTrackerService.findOne(extServiceId);

        String url = String.format(DELETE_PATH, externalTracker.getTeamUrl(), projectId);

        repository.deleteById(projectId);

        return webClient.delete()
                .uri(URI.create(url))
                .header(AUTHORIZATION, "Basic " + getBasicAuth(externalTracker))
                .retrieve().bodyToMono(Object.class).block();
    }
}
