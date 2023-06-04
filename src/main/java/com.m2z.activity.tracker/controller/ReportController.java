package com.m2z.activity.tracker.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.m2z.activity.tracker.dto.RequestReport;
import com.m2z.activity.tracker.entity.DocumentReport;
import com.m2z.activity.tracker.repository.impl.ReportDocumentRepository;
import com.m2z.activity.tracker.repository.impl.ReportGraphRepository;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDateTime;
import java.util.*;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@RestController
@RequestMapping(value = "/api/report")
public class ReportController {

    @Autowired
    private ReportDocumentRepository reportDocumentRepository;

    @Autowired
    private ReportGraphRepository reportGraphRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final WebClient webClient = WebClient.builder().build();

    @PostMapping("document")
    @ResponseStatus(HttpStatus.OK)
    public void createDocument(@RequestBody RequestReport requestReport) {

        createColumnTableAndAddData();

        Optional<DocumentReport> documentById = reportDocumentRepository.findById("meta-schema-document1");
        if(documentById.isEmpty()) {
            System.out.println("Document schema is not present");
            return;
        }
        DocumentReport documentReport = documentById.get();
        List<Map<String, Object>> reportList = (List<Map<String, Object>>) documentReport.getReport();
        Map<String, Object> stringObjectMap = reportList.get(0);
        String title = (String) stringObjectMap.get("naslov");
        title = title.replace("<<estimated_start_date>>", requestReport.getEstimatedStartDate());
        title = title.replace("<<estimated_end_date>>", requestReport.getEstimatedEndDate());

        stringObjectMap.put("naslov", title);
        Map<String, Object> projectDocument = reportList.get(1);

        String sql = String.format("CALL selectDistinctProjects('%s')", getKeysOfMap(projectDocument));
        List<Map<String, Object>> projects = jdbcTemplate.queryForList(sql);

        String id = UUID.randomUUID().toString();
        List<Map<String, Object>> report = new ArrayList<>();

        projects.forEach( project -> {

            if(projectDocument.containsKey("employees")) {
                List<Map<String, Object>> listEmployees = (List<Map<String, Object>>) projectDocument.get("employees");
                if(listEmployees.size() > 0) {
                    addEmployeeData(listEmployees.get(0), project, requestReport);
                }
            }

            if(projectDocument.containsKey("tickets")) {
                List<Map<String, Object>> listTickets = (List<Map<String, Object>>) projectDocument.get("tickets");
                if(listTickets.size() > 0) {
                    addTicketsData(listTickets.get(0), project, requestReport);
                }
            }

            report.add(project);
        });


        report.add(Map.of("timestamp", LocalDateTime.now().toString()));
        reportDocumentRepository.save(new DocumentReport(id, stringObjectMap, report));
    }

    private String getKeysOfMap(Map<String, Object> projectDocument) {
        List<String> result = new ArrayList<>();

        projectDocument.forEach((key, value) -> {
            if(!(value instanceof Map || value instanceof List) && !key.endsWith("s")) {
                result.add(key);
            }
        });

        return String.join(", ", result);
    }

    private void addTicketsData(Map<String, Object> schema, Map<String, Object> project, RequestReport requestReport) {
        String sql1 = String.format("CALL selectTicketsForProjectAndDateRange('%s', '%s', '%s', '%s')",
               getKeysOfMap(schema), project.get("project_id"), requestReport.getEstimatedStartDate(), requestReport.getEstimatedEndDate());

        List<Map<String, Object>> tickets = jdbcTemplate.queryForList(sql1);

        if(schema.containsKey("assignee")) {
            tickets.forEach(ticket -> {
                Map<String, Object> assignee = (Map<String, Object>) schema.get("assignee");

                String sql2 = String.format("CALL selectEmployeeForProjectDateRangeAndId('%s', '%s', '%s', '%s', '%s')"
                        , getKeysOfMap(assignee), project.get("project_id"), requestReport.getEstimatedStartDate(),
                        requestReport.getEstimatedEndDate(), ticket.get("ticket_id"));

                List<Map<String, Object>> employee = jdbcTemplate.queryForList(sql2);
                if(employee.size() > 0) {
                    ticket.put("assignee", employee.get(0));
                    ticket.remove("ticket_assignee_id");
                    ticket.remove("ticket_assignee_display_name");
                }
            });
        }

        project.put("tickets", tickets);
    }

    private void addEmployeeData(Map<String, Object> schema, Map<String, Object> project, RequestReport requestReport) {
        String sql1 = String.format("CALL selectEmployeeForProjectAndDateRange('%s', '%s', '%s', '%s')"
                , getKeysOfMap(schema),  project.get("project_id"), requestReport.getEstimatedStartDate(), requestReport.getEstimatedEndDate());

        List<Map<String, Object>> employees = jdbcTemplate.queryForList(sql1);

        project.put("employees", employees);
    }

    @PostMapping("graph")
    @ResponseStatus(HttpStatus.OK)
    @SneakyThrows
    public void createGraph() {

        createColumnTableAndAddData();

        String id = UUID.randomUUID().toString();

        List<String> months = List.of("January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December");

        Map<String, List<Map<String, Object>>> mainReport = new HashMap<>();
        for (int i = 0; i < months.size(); i++) {
            mainReport.put(months.get(i), new ArrayList<>());

            createProjectForGraph(mainReport, months.get(i), i + 1);
        }

        Map<String, Object> bodyMap = new HashMap<>();
        bodyMap.put("name", id);
        bodyMap.put("type", 2);

        webClient.post().uri("http://localhost:8529/_api/collection")
                .header(
                        AUTHORIZATION, "Basic " + Base64.getEncoder().encodeToString("root:root".getBytes())
                )
                .bodyValue(bodyMap)
                .retrieve().toBodilessEntity().block();

        webClient.post().uri(String.format("http://localhost:8529/_api/document/%s", id))
                .header(
                        AUTHORIZATION, "Basic " + Base64.getEncoder().encodeToString("root:root".getBytes())
                )
                .bodyValue(new ObjectMapper().writer().withDefaultPrettyPrinter().writeValueAsString(mainReport))
                .retrieve().toBodilessEntity().block();

    }

    private void createProjectForGraph(Map<String, List<Map<String, Object>>> mainReport, String month, int monthIndex) {

        String sql = String.format("call `activity-tracker`.selectProjectsForMonth(%s)", monthIndex);

        List<Map<String, Object>> projects = jdbcTemplate.queryForList(sql);

        projects.forEach( project -> {

            String sql1 = String.format("CALL selectEmployeesForProjectAndMonth('%s', %s)"
                    ,project.get("project_id"), monthIndex);

            List<Map<String, Object>> employees = jdbcTemplate.queryForList(sql1);

            project.put("employees", employees);

            sql1 = String.format("CALL selectTicketsForProjectAndMonth('%s', %s)",
                    project.get("project_id"), monthIndex);

            List<Map<String, Object>> tickets = jdbcTemplate.queryForList(sql1);

            tickets.forEach(ticket -> {

                String sql2 = String.format("CALL selectEmployeeForProjectAndMonth('%s', '%s', %s)"
                        , project.get("project_id"), ticket.get("ticket_assignee_id"), monthIndex);

                List<Map<String, Object>> employee = jdbcTemplate.queryForList(sql2);
                ticket.put("assignee", employee);
                ticket.remove("ticket_assignee_id");
                ticket.remove("ticket_assignee_display_name");
            });

            project.put("tickets", tickets);

            if(!(employees.isEmpty() && tickets.isEmpty())) {
                mainReport.get(month).add(project);
            }
        });
    }

    private void createColumnTableAndAddData() {

        String sql = "call `activity-tracker`.step1_createColumnTable()";
        executeSql(sql);

        sql = "call `activity-tracker`.step2_storeTicketDataInColumnTable()";
        executeSql(sql);

        sql = "call `activity-tracker`.step3_storeEmployeeDataInColumnTable()";
        executeSql(sql);

        sql = "call `activity-tracker`.step4_storeProjectDataInColumnTable()";
        executeSql(sql);
    }

    private void executeSql(String sql) {
        try {
            jdbcTemplate.execute(sql);
        } catch (Exception e) {}
    }
}
