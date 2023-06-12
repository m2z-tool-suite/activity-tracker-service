package com.m2z.activity.tracker.controller;

import com.arangodb.ArangoCollection;
import com.arangodb.ArangoDB;
import com.arangodb.ArangoDatabase;
import com.arangodb.entity.*;
import com.arangodb.model.CollectionCreateOptions;
import com.m2z.activity.tracker.dto.RequestReport;
import com.m2z.activity.tracker.entity.DocumentReport;
import com.m2z.activity.tracker.entity.ReportMeta;
import com.m2z.activity.tracker.repository.impl.ReportDocumentRepository;
import com.m2z.activity.tracker.repository.impl.ReportMetaRepository;
import de.huxhorn.sulky.ulid.ULID;
import lombok.SneakyThrows;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@RestController
@RequestMapping(value = "/api/report")
public class ReportController {

    @Autowired
    private ReportDocumentRepository reportDocumentRepository;

    @Autowired
    private ReportMetaRepository reportMetaRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private ArangoDB arangoDB;
    private final ULID ulid = new ULID();

    @PostMapping("document")
    @ResponseStatus(HttpStatus.OK)
    public void createDocument(@RequestBody RequestReport requestReport) {

        createColumnTableAndAddData();

        Optional<DocumentReport> documentById = reportDocumentRepository.findById("meta-schema-document1");
        if (documentById.isEmpty()) {
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

        String id = ulid.nextULID();
        List<Map<String, Object>> report = new ArrayList<>();

        projects.forEach(project -> {

            if (projectDocument.containsKey("employees")) {
                List<Map<String, Object>> listEmployees = (List<Map<String, Object>>) projectDocument.get("employees");
                if (listEmployees.size() > 0) {
                    addEmployeeData(listEmployees.get(0), project, requestReport);
                }
            }

            if (projectDocument.containsKey("tickets")) {
                List<Map<String, Object>> listTickets = (List<Map<String, Object>>) projectDocument.get("tickets");
                if (listTickets.size() > 0) {
                    addTicketsData(listTickets.get(0), project, requestReport);
                }
            }

            report.add(project);
        });

        report.add(Map.of("timestamp", LocalDateTime.now().toString()));
        reportDocumentRepository.save(new DocumentReport(id, stringObjectMap, report));
    }


    @PostMapping("graph")
    @ResponseStatus(HttpStatus.OK)
    @SneakyThrows
    public void createGraph() {

        createColumnTableAndAddData();

        List<String> months = List.of("January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December");

        Optional<ReportMeta> reportMetaOptional = reportMetaRepository.findById("meta-graph");
        if (reportMetaOptional.isEmpty()) {
            return;
        }
        ReportMeta reportMeta = reportMetaOptional.get();

        Map<String, List<Map<String, Object>>> mainReport = new HashMap<>();
        for (int i = 0; i < months.size(); i++) {
            mainReport.put(months.get(i), new ArrayList<>());

            createProjectForGraph(mainReport, months.get(i), i + 1, reportMeta);
        }

        ArangoDatabase db = arangoDB.db("report");
        Map<String, String> generatedIds = new HashMap<>();
        Map<String, ArangoCollection> arangoCollections = new HashMap<>();

        reportMeta.getVertex_collections().forEach(vertexCollection -> {
            String keyPrefix = vertexCollection.getKey_prefix();
            String id = keyPrefix + createRandomId();
            vertexCollection.setGenerateId(id);
            CollectionEntity collection = createCollection(db, false, id);
            vertexCollection.setId(collection.getId());
            generatedIds.put(keyPrefix, id);
            arangoCollections.put(keyPrefix, db.collection(collection.getId()));
        });

        Map<String, EdgeDefinition> edgeDefinitions = new HashMap<>();
        reportMeta.getEdge_definitions().forEach(item -> {
            String fromNodeId = generatedIds.get(item.getFrom_vertex_collections());
            String toNodeId = generatedIds.get(item.getTo_vertex_collections());

            EdgeDefinition edgeDefinition = createEdgeDefinition(fromNodeId, toNodeId, createRandomId());
            edgeDefinitions.put(getKeyFromTo(item.getFrom_vertex_collections(), item.getTo_vertex_collections()), edgeDefinition);
        });

        String graphName = "graph_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy_MM_dd_HH_mm_ss"));

        GraphEntity graph = db.createGraph(graphName, edgeDefinitions.values());

        ArangoCollection monthsCollection = arangoCollections.get("month");
        ArangoCollection projectsCollection = arangoCollections.get("project");
        ArangoCollection employeesCollection = arangoCollections.get("employee");
        ArangoCollection ticketsCollection = arangoCollections.get("ticket");

        DocumentCreateEntity<Void> documentMonths = monthsCollection.insertDocument(Map.of("_key", "Months"));
        ArangoCollection monthProjectEdgeCollection = db.collection(edgeDefinitions.get("month__project").getCollection());
        ArangoCollection projectEmployeeEdgeCollection = db.collection(edgeDefinitions.get("project__employee").getCollection());
        ArangoCollection projectTicketEdgeCollection = db.collection(edgeDefinitions.get("project__ticket").getCollection());
        ArangoCollection ticketEmployeeEdgeCollection = db.collection(edgeDefinitions.get("ticket__employee").getCollection());

        mainReport.forEach((month, listProjects) -> {
            DocumentCreateEntity<Void> documentMonth = monthsCollection.insertDocument(Map.of("_key", month));
            createEdge(monthProjectEdgeCollection, documentMonths, documentMonth);

            listProjects.forEach(project -> {
                DocumentCreateEntity<Void> documentProject;
                if (projectsCollection.documentExists((String) project.get("project_id"))) {
                    documentProject = projectsCollection.getDocument((String) project.get("project_id"), DocumentCreateEntity.class);
                } else {
                    Map<String, Object> map = new HashMap<>(project);
                    map.put("_key", project.get("project_id"));
                    map.remove("tickets");
                    map.remove("employees");

                    documentProject = projectsCollection.insertDocument(map);
                }

                createDocument(monthProjectEdgeCollection, documentMonth.getId(), documentProject);

                List<Map<String, String>> employees = (List<Map<String, String>>) project.get("employees");
                employees.forEach(employee -> {

                    String employeeName = employee.get("employee_display_name").replace(" ", "_");
                    DocumentCreateEntity<Void> documentEmployee = createDocumentIfNotExist(employeesCollection, employee, null, employeeName);

                    createDocument(projectEmployeeEdgeCollection, documentProject.getId(), documentEmployee);
                });

                List<Map<String, String>> tickets = (List<Map<String, String>>) project.get("tickets");
                tickets.forEach(ticket -> {

                    DocumentCreateEntity<Void> documentTicket = createDocumentIfNotExist(ticketsCollection, ticket, "ticket_id", ticket.get("ticket_id"));

                    createDocument(projectTicketEdgeCollection, documentProject.getId(), documentTicket);

                    String ticketAssignee = ticket.get("employee_display_name").replace(" ", "_");
                    if (employeesCollection.documentExists(ticketAssignee)) {
                        DocumentCreateEntity<Void> documentEmployee = employeesCollection.getDocument(ticketAssignee, DocumentCreateEntity.class);

                        createDocument(ticketEmployeeEdgeCollection, documentTicket.getId(), documentEmployee);
                    }
                });

            });
        });
    }

    private DocumentCreateEntity createDocumentIfNotExist(ArangoCollection collection, Map<String, String> map, String key, String arangoKey) {

        if (collection.documentExists(arangoKey)) {
            return collection.getDocument(arangoKey, DocumentCreateEntity.class);
        }
        Map<String, Object> mapObj = new HashMap<>(map);
        mapObj.put("_key", key != null ? map.get(key) : arangoKey);

        return collection.insertDocument(mapObj);
    }

    private void createEdge(ArangoCollection collection, DocumentCreateEntity<Void> fromNode, DocumentCreateEntity<Void> toNode) {
        collection.insertDocument(Map.of(
                "_from", fromNode.getId(),
                "_to", toNode.getId()
        ));
    }

    private void createDocument(ArangoCollection collection, String fromNodeId, DocumentCreateEntity<Void> toNode) {
        String keyFromTo = getKeyFromTo(fromNodeId, toNode.getId());
        keyFromTo = keyFromTo.replace("/", "_");
        if (!collection.documentExists(keyFromTo)) {
            collection.insertDocument(Map.of(
                    "_key", keyFromTo,
                    "_from", fromNodeId,
                    "_to", toNode.getId()
            ));
        }
    }

    private String getKeyFromTo(String from, String to) {
        return from + "__" + to;
    }

    private CollectionEntity createCollection(ArangoDatabase db, boolean isEdge, String nodeId) {

        if (isEdge) {
            return db.createCollection(nodeId, new CollectionCreateOptions().type(CollectionType.EDGES));
        }

        return db.createCollection(nodeId);
    }

    private EdgeDefinition createEdgeDefinition(String fromNode, String toNode, String nodeCollection) {

        EdgeDefinition edgeDefinition = new EdgeDefinition();
        edgeDefinition.from(fromNode);
        edgeDefinition.to(toNode);
        edgeDefinition.collection(nodeCollection);

        return edgeDefinition;
    }

    private String createRandomId() {
        return RandomStringUtils.randomAlphabetic(30);
    }

    private String getKeysOfMap(Map<String, Object> projectDocument) {
        List<String> result = new ArrayList<>();

        projectDocument.forEach((key, value) -> {
            if (!(value instanceof Map || value instanceof List) && !key.endsWith("s")) {
                result.add(key);
            }
        });

        return String.join(", ", result);
    }

    private void addTicketsData(Map<String, Object> schema, Map<String, Object> project, RequestReport requestReport) {
        String sql1 = String.format("CALL selectTicketsForProjectAndDateRange('%s', '%s', '%s', '%s')",
                getKeysOfMap(schema), project.get("project_id"), requestReport.getEstimatedStartDate(), requestReport.getEstimatedEndDate());

        List<Map<String, Object>> tickets = jdbcTemplate.queryForList(sql1);

        if (schema.containsKey("assignee")) {
            tickets.forEach(ticket -> {
                Map<String, Object> assignee = (Map<String, Object>) schema.get("assignee");

                String sql2 = String.format("CALL selectEmployeeForProjectDateRangeAndId('%s', '%s', '%s', '%s', '%s')"
                        , getKeysOfMap(assignee), project.get("project_id"), requestReport.getEstimatedStartDate(),
                        requestReport.getEstimatedEndDate(), ticket.get("ticket_id"));

                List<Map<String, Object>> employee = jdbcTemplate.queryForList(sql2);
                if (employee.size() > 0) {
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
                , getKeysOfMap(schema), project.get("project_id"), requestReport.getEstimatedStartDate(), requestReport.getEstimatedEndDate());

        List<Map<String, Object>> employees = jdbcTemplate.queryForList(sql1);

        project.put("employees", employees);
    }

    private String getListOfStrings(ReportMeta reportMeta, String collectionsKey) {
        return String.join(",", reportMeta.getVertex_collections().stream()
                .filter(i -> i.getKey_prefix().equalsIgnoreCase(collectionsKey)).findFirst()
                .get().getProps());
    }

    private void createProjectForGraph(Map<String, List<Map<String, Object>>> mainReport, String month, int monthIndex, ReportMeta reportMeta) {

        String propsProject = getListOfStrings(reportMeta, "project");
        String sql = String.format("call `activity-tracker`.selectProjectsForMonth('%s', %s)", propsProject, monthIndex);

        List<Map<String, Object>> projects = jdbcTemplate.queryForList(sql);

        projects.forEach(project -> {

            String propsEmployee = getListOfStrings(reportMeta, "employee");
            String sql1 = String.format("CALL selectEmployeesForProjectAndMonth('%s', '%s', %s)"
                    , propsEmployee, project.get("project_id"), monthIndex);

            List<Map<String, Object>> employees = jdbcTemplate.queryForList(sql1);

            project.put("employees", employees);

            String propsTicket = getListOfStrings(reportMeta, "ticket");
            sql1 = String.format("CALL selectTicketsForProjectAndMonth('%s', '%s', %s)", propsTicket, project.get("project_id"), monthIndex);

            List<Map<String, Object>> tickets = jdbcTemplate.queryForList(sql1);

            tickets.forEach(ticket -> {

                String sql2 = String.format("CALL selectEmployeeForProjectAndMonth('%s', '%s', '%s', %s)"
                        , propsEmployee, project.get("project_id"), ticket.get("ticket_assignee_id"), monthIndex);

                List<Map<String, Object>> employee = jdbcTemplate.queryForList(sql2);
                ticket.put("assignee", employee);
                ticket.remove("ticket_assignee_id");
                ticket.remove("ticket_assignee_display_name");
            });

            project.put("tickets", tickets);

            if (!(employees.isEmpty() && tickets.isEmpty())) {
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
        } catch (Exception e) {
        }
    }
}
