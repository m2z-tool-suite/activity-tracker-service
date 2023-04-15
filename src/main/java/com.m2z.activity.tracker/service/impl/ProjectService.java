package com.m2z.activity.tracker.service.impl;

import com.m2z.activity.tracker.dto.ProjectDto;
import com.m2z.activity.tracker.entity.Project;
import com.m2z.activity.tracker.repository.impl.ProjectRepository;
import com.m2z.activity.tracker.service.definition.BaseService;
import org.springframework.stereotype.Service;

@Service
public class ProjectService extends BaseService<Project, ProjectDto, Long> {

    public ProjectService(ProjectRepository repository) {
        super(repository);
    }

    @Override
    public ProjectDto convertToDTO(Project element) {
        return new ProjectDto(element);
    }
}
