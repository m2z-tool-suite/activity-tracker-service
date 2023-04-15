package com.m2z.activity.tracker.controller;

import com.m2z.activity.tracker.dto.ExternalTrackerDto;
import com.m2z.activity.tracker.entity.ExternalTracker;
import com.m2z.activity.tracker.exception.NotFoundException;
import com.m2z.activity.tracker.repository.definition.BaseRepository;
import com.m2z.activity.tracker.service.impl.ExternalTrackerService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;


@RestController
@RequestMapping(value = "/api/external-service")
public class ExternalTrackerController {

    private final ExternalTrackerService service;

    public ExternalTrackerController(ExternalTrackerService service) {
        this.service = service;
    }

    @PutMapping("{id}")
    @ResponseStatus(HttpStatus.OK)
    public ExternalTrackerDto update(@RequestBody ExternalTracker changedT, @PathVariable("id") Long id){
        BaseRepository<ExternalTracker, Long> repository = service.getRepository();
        Optional<ExternalTracker> byId = repository.findById(id);
        if(byId.isEmpty()){
            throw new NotFoundException();
        }

        ExternalTracker externalTracker = byId.get();

        return service.update(externalTracker, changedT);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ExternalTrackerDto> getAll()
    {
        return service.findAll();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ExternalTrackerDto create(@RequestBody ExternalTracker element){
        return service.save(element);
    }
}
