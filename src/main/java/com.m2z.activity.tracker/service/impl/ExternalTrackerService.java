package com.m2z.activity.tracker.service.impl;

import com.m2z.activity.tracker.config.CryptUtils;
import com.m2z.activity.tracker.dto.ExternalTrackerDto;
import com.m2z.activity.tracker.entity.ExternalTracker;
import com.m2z.activity.tracker.repository.impl.ExternalTrackerRepository;
import com.m2z.activity.tracker.service.definition.BaseService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ExternalTrackerService extends BaseService<ExternalTracker, ExternalTrackerDto, Long> {

    private ExternalTrackerRepository repository;

    public ExternalTrackerService(ExternalTrackerRepository repository) {
        super(repository);
        this.repository = repository;
    }

    @Override
    public ExternalTrackerDto convertToDTO(ExternalTracker element) {
        return new ExternalTrackerDto(element);
    }

    @Override
    public ExternalTrackerDto save(ExternalTracker externalTracker) {

        externalTracker.setPassword(CryptUtils.encrypt(externalTracker.getPassword()));
        externalTracker.setPrivateKey(CryptUtils.encrypt(externalTracker.getPrivateKey()));

        if(externalTracker.getIsActive()){
            List<ExternalTracker> allByEmail = repository.findAllByEmail(externalTracker.getEmail());
            allByEmail.removeIf(o -> !o.getHomeUrl().equalsIgnoreCase(externalTracker.getHomeUrl()) || !o.getTeamUrl().equalsIgnoreCase(externalTracker.getTeamUrl()));
            allByEmail.forEach(o -> o.setIsActive(false));
            repository.saveAll(allByEmail);
        }

        return super.save(externalTracker);
    }

    public ExternalTrackerDto update(ExternalTracker externalTracker, ExternalTracker updateObject) {

        externalTracker.update(updateObject);
        if(updateObject.getIsActive()){
            List<ExternalTracker> allByEmail = repository.findAllByEmail(updateObject.getEmail());
            allByEmail.removeIf(o -> !o.getHomeUrl().equalsIgnoreCase(updateObject.getHomeUrl()) || !o.getTeamUrl().equalsIgnoreCase(updateObject.getTeamUrl()));
            allByEmail.forEach(o -> o.setIsActive(false));
            repository.saveAll(allByEmail);
        }

        return super.save(externalTracker);
    }
}
