package com.m2z.activity.tracker.service.impl;

import com.m2z.activity.tracker.dto.ProjekatDto;
import com.m2z.activity.tracker.entity.Project;
import com.m2z.activity.tracker.repository.impl.ProjekatRepository;
import com.m2z.activity.tracker.service.definition.BaseService;
import org.springframework.stereotype.Service;

@Service
public class ProjekatService extends BaseService<Project, ProjekatDto, Long> {

    public ProjekatService(ProjekatRepository repository) {
        super(repository);
    }

    @Override
    public ProjekatDto convertToDTO(Project element) {
        return new ProjekatDto(element);
    }
}
