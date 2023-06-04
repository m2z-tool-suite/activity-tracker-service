package com.m2z.activity.tracker.service.impl;

import com.m2z.activity.tracker.dto.ExternalTrackerDto;
import com.m2z.activity.tracker.dto.TicketDto;
import com.m2z.activity.tracker.entity.Ticket;
import com.m2z.activity.tracker.repository.impl.EmployeeRepository;
import com.m2z.activity.tracker.repository.impl.ProjectRepository;
import com.m2z.activity.tracker.repository.impl.TicketRepository;
import com.m2z.activity.tracker.repository.impl.TicketTypeRepository;
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
public class TicketService extends BaseService<Ticket, TicketDto, String> {
    private final WebClient webClient = WebClient.builder().build();

    @Autowired
    private ExternalTrackerService externalTrackerService;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private TicketTypeRepository ticketTypeRepository;

    @Autowired
    private TicketRepository ticketRepository;

    private final String BASE_FORMAT_PATH = "%s/rest/api/2/search?jql=project=%s";
    private final String DELETE_PATH = "%s/rest/api/2/issue/%s";
    private final String CREATE_PATH = "%s/rest/api/2/issue";
    private final String UPDATE_PATH = "%s/rest/api/2/issue/%s";

    private final TicketRepository repository;

    public TicketService(TicketRepository repository) {
        super(repository);
        this.repository = repository;
    }

    @Override
    public TicketDto convertToDTO(Ticket element) {
        return new TicketDto(element);
    }


    public List<TicketDto> getAllTickets(Long extServiceId, String projectId) {

        ExternalTrackerDto externalTracker = externalTrackerService.findOne(extServiceId);

        String url = String.format(BASE_FORMAT_PATH, externalTracker.getTeamUrl(), projectId);

        Map mapResponse = webClient.get()
                .uri(URI.create(url))
                .header(AUTHORIZATION, "Basic " + getBasicAuth(externalTracker))
                .retrieve().bodyToMono(Map.class).block();
        List issues = (List) mapResponse.get("issues");

        List<TicketDto> collect = (List<TicketDto>) issues.stream().map(issue -> new TicketDto((Map) issue)).collect(Collectors.toList());

        collect.forEach(ticketDto -> {
            Ticket ticket = ticketRepository.getById(ticketDto.getId());
            ticketDto.setEstimatedStartDate(ticket.getEstimatedStartDate());
            ticketDto.setEstimatedEndDate(ticket.getEstimatedEndDate());
        });

        return collect;
    }

    public Object saveTicket(Long extServiceId, TicketDto ticket, String projectId) {

        ExternalTrackerDto externalTracker = externalTrackerService.findOne(extServiceId);

        String url = String.format(CREATE_PATH, externalTracker.getTeamUrl());

        Map block = webClient.post()
                .uri(URI.create(url))
                .header(AUTHORIZATION, "Basic " + getBasicAuth(externalTracker))
                .bodyValue(Map.of(
                        "fields", Map.of(
                                "project", Map.of("key", projectId.toUpperCase(Locale.ROOT)),
                                "summary", ticket.getSummary(),
                                "description", ticket.getDescription(),
                                "issuetype", Map.of("name", ticket.getTypeName())
                        )
                ))
                .retrieve().bodyToMono(Map.class).block();

        ticket.setId((String) block.get("key"));

        ticket.setDescription(ticket.getDescription() != null ? ticket.getDescription() : "");

        updateTask(ticket.getId(), ticket.getSelectedAssigneeId(), externalTracker);

        Ticket ticketEntity = new Ticket();

        ticketEntity.setCreatedBy(ticket.getLoggedUserEmail());
        ticketEntity.setId(ticket.getId());
        ticketEntity.setSummary(ticket.getSummary());
        ticketEntity.setDescription(ticket.getDescription());
        ticketEntity.setProject(projectRepository.getById(projectId));
        ticketEntity.setAssignee(employeeRepository.getById(ticket.getSelectedAssigneeId()));
        ticketEntity.setEstimatedStartDate(ticket.getEstimatedStartDate().split("T")[0]);
        ticketEntity.setEstimatedEndDate(ticket.getEstimatedEndDate().split("T")[0]);

        repository.save(ticketEntity);

        return ticket;
    }


    public Object updateTicket(Long extServiceId, TicketDto ticket, String projectId, String ticketId) {

        ExternalTrackerDto externalTracker = externalTrackerService.findOne(extServiceId);

        String url = String.format(UPDATE_PATH, externalTracker.getTeamUrl(), ticketId);

        ticket.setDescription(ticket.getDescription() != null ? ticket.getDescription() : "");

        Map block = webClient.put()
                .uri(URI.create(url))
                .header(AUTHORIZATION, "Basic " + getBasicAuth(externalTracker))
                .bodyValue(Map.of(
                        "fields", Map.of(
                                "summary", ticket.getSummary(),
                                "description", ticket.getDescription(),
                                "issuetype", Map.of("name", ticket.getTypeName())
                        )
                ))
                .retrieve().bodyToMono(Map.class).block();


        Ticket ticketEntity = repository.getById(ticketId);
        ticketEntity.setCreatedBy(ticket.getLoggedUserEmail());
        ticketEntity.setSummary(ticket.getSummary());
        ticketEntity.setDescription(ticket.getDescription());
        ticketEntity.setType(ticketTypeRepository.getByName(ticket.getTypeName()));

        repository.save(ticketEntity);

        updateTask(ticketId, ticket.getSelectedAssigneeId(), externalTracker);

        return block;
    }

    public Object deleteTicket(Long extServiceId, String ticketId) {

        ExternalTrackerDto externalTracker = externalTrackerService.findOne(extServiceId);

        String url = String.format(DELETE_PATH, externalTracker.getTeamUrl(), ticketId);

        repository.deleteById(ticketId);

        return webClient.delete()
                .uri(URI.create(url))
                .header(AUTHORIZATION, "Basic " + getBasicAuth(externalTracker))
                .retrieve().bodyToMono(Object.class).block();
    }

    public void updateTask(String ticketId, String selectedAssigneeId, ExternalTrackerDto externalTracker) {
        if(ticketId != null && selectedAssigneeId != null) {
            webClient.put()
                    .uri(URI.create(String.format("%s/rest/api/3/issue/%s/assignee", externalTracker.getTeamUrl(), ticketId)))
                    .header(AUTHORIZATION, "Basic " + getBasicAuth(externalTracker))
                    .bodyValue(Map.of(
                            "accountId", selectedAssigneeId
                    ))
                    .retrieve().toBodilessEntity().block();
        }
    }
}
