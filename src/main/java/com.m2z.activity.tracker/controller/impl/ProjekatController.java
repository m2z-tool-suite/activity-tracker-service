package com.m2z.activity.tracker.controller.impl;

import com.m2z.activity.tracker.controller.definition.BaseController;
import com.m2z.activity.tracker.dto.ProjekatDto;
import com.m2z.activity.tracker.entity.Project;
import com.m2z.activity.tracker.service.impl.ProjekatService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequestMapping(value = "/api/projekat")
public class ProjekatController extends BaseController<Project, ProjekatDto, Long> {

    private final PropertiesClient propertiesClient = new PropertiesClient();
    private final JiraOAuthClient jiraOAuthClient = new JiraOAuthClient(propertiesClient);
    private final OAuthClient oAuthClient = new OAuthClient(propertiesClient, jiraOAuthClient);

//    @Value("${jira.token.name}")
//    private String tokenName;
//
//    @Value("${jira.token.value}")
//    private String tokenValue;

    @Value("${jira.team.url}")
    private String jiraTeamUrl;


    public ProjekatController(ProjekatService service) throws Exception {
        super(service);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Object getAllProjects() {
        String format = String.format("%s/rest/api/2/project", jiraTeamUrl);

        return oAuthClient.handleGetRequestWithResponse(format);
    }

    @GetMapping("{projectId}")
    @ResponseStatus(HttpStatus.OK)
    public Object getOneProject(@PathVariable("projectId") String projectId) {
        String format = String.format("%s/rest/api/2/project/%s", jiraTeamUrl, projectId);

        return oAuthClient.handleGetRequestWithResponse(format);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public Object createProject(@RequestBody Object object) {
        String format = String.format("%s/rest/api/2/project", jiraTeamUrl);

        return oAuthClient.handlePostRequestWithResponse(format, object);
    }

    @DeleteMapping("{projectId}")
    @ResponseStatus(HttpStatus.OK)
    public Object deleteProject(@PathVariable("projectId") String projectId) {
        String format = String.format("%s/rest/api/2/project/%s", jiraTeamUrl, projectId);

        return oAuthClient.handleDeleteRequest(format);
    }

    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    public Object updateProject(@RequestBody Object object) {
        String format = String.format("%s/rest/api/2/project", jiraTeamUrl);

        return oAuthClient.handlePostRequestWithResponse(format, object);
    }

//    @GetMapping("requestToken")
//    @ResponseStatus(HttpStatus.OK)
//    public void requestToken() {
//
//        testGetToken("requestToken", new ArrayList<>());
//    }
//
//    @GetMapping("accessToken")
//    @ResponseStatus(HttpStatus.OK)
//    public void accessToken(@RequestParam("code") String code) {
//
//        testGetToken("accessToken", Arrays.stream(code.split(",")).toList());
//    }


    public void testGetToken(String arg, List<String> arguments) {

        oAuthClient.execute(Command.fromString(arg), arguments);
    }
}
