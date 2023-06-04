package com.m2z.activity.tracker.controller;

import com.m2z.activity.tracker.dto.ProjectDto;
import com.m2z.activity.tracker.dto.TicketDto;
import com.m2z.activity.tracker.service.impl.TicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping(value = "/api/ticket")
public class TicketController {

    @Autowired
    private TicketService ticketService;

    @GetMapping("ext-service/{extServiceId}/project/{projectId}")
    @ResponseStatus(HttpStatus.OK)
    public List<TicketDto> getAllTickets(@PathVariable("extServiceId") String extServiceId, @PathVariable("projectId") String projectId) {

        return ticketService.getAllTickets(Long.valueOf(extServiceId), projectId);
    }

    @PostMapping("ext-service/{extServiceId}/project/{projectId}")
    @ResponseStatus(HttpStatus.OK)
    public Object create(@RequestBody TicketDto object, @PathVariable("extServiceId") Long extServiceId, @PathVariable("projectId") String projectId) {

        return ticketService.saveTicket(extServiceId, object, projectId);
    }

    @DeleteMapping("ext-service/{extServiceId}/project/{projectId}/ticket/{ticketId}")
    @ResponseStatus(HttpStatus.OK)
    public void delete(@PathVariable("extServiceId") Long extServiceId, @PathVariable("projectId") String projectId,
                       @PathVariable("ticketId") String ticketId) {

        ticketService.deleteTicket(extServiceId, ticketId);
    }

    @PutMapping("ext-service/{extServiceId}/project/{projectId}/ticket/{ticketId}")
    @ResponseStatus(HttpStatus.OK)
    public ProjectDto update(@PathVariable("extServiceId") Long extServiceId, @PathVariable("projectId") String projectId,
                             @PathVariable("ticketId") String ticketId, @RequestBody TicketDto object) {

        return (ProjectDto) ticketService.updateTicket(extServiceId, object, projectId, ticketId);
    }
}
