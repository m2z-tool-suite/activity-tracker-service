package com.m2z.activity.tracker.service.impl;

import com.m2z.activity.tracker.dto.EmployeeDto;
import com.m2z.activity.tracker.dto.ExternalTrackerDto;
import com.m2z.activity.tracker.entity.Employee;
import com.m2z.activity.tracker.repository.impl.EmployeeRepository;
import com.m2z.activity.tracker.repository.impl.ExternalTrackerRepository;
import com.m2z.activity.tracker.service.definition.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.m2z.activity.tracker.service.impl.UtilsMethods.getBasicAuth;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Service
public class EmployeeService extends BaseService<Employee, EmployeeDto, String> {

    private EmployeeRepository repository;

    @Autowired
    private ExternalTrackerService externalTrackerService;

    @Autowired
    private ExternalTrackerRepository externalTrackerRepository;
    private final String BASE_FORMAT_PATH = "%s/rest/api/3/users";
    private final String CREATE_FORMAT_PATH = "%s/rest/api/3/user";
    private final String DELETE_PATH = "%s/rest/api/3/user?accountId=%s";
    private final String UPDATE_PATH = "%s/rest/api/3/users/%s";
    private final WebClient webClient = WebClient.builder().build();

    public EmployeeService(EmployeeRepository repository) {
        super(repository);
        this.repository = repository;
    }

    @Override
    public EmployeeDto convertToDTO(Employee element) {
        return new EmployeeDto(element);
    }

    @Override
    public EmployeeRepository getRepository() {
        return repository;
    }

    public List<EmployeeDto> findAll(Long externalTrackerId) {

        ExternalTrackerDto externalTracker = externalTrackerService.findOne(externalTrackerId);

        String url = String.format(BASE_FORMAT_PATH + "/search", externalTracker.getTeamUrl());

        List<EmployeeDto> collect = (List<EmployeeDto>) webClient.get()
                .uri(URI.create(url))
                .header(AUTHORIZATION, "Basic " + getBasicAuth(externalTracker))
                .retrieve().bodyToMono(List.class).block()
                .stream()
                .filter(EmployeeService::isAccountTypeEqualsToAtlassian)
                .map(employee -> new EmployeeDto((Map) employee))
                .collect(Collectors.toList());

        collect.forEach(this::updateEmployeeDto);
        List<EmployeeDto> collect1 = new ArrayList<>();

        collect.forEach(employee -> {
            Optional<Employee> byAccountId = repository.findById(employee.getId());
            if(byAccountId.isPresent()) {
                Employee employee1 = byAccountId.get();
                employee.setEmail(employee1.getEmail());
                employee.setFirstname(employee1.getFirstname());
                employee.setLastname(employee1.getLastname());
                collect1.add(employee);
            }
        });

        return collect1;
    }

    public void delete(Long externalTrackerId, String accountId) {

        ExternalTrackerDto externalTracker = externalTrackerService.findOne(externalTrackerId);

        String url = String.format(DELETE_PATH, externalTracker.getTeamUrl(), accountId);

        Employee employee = repository.getById(accountId);

        webClient.delete()
                .uri(URI.create(url))
                .header(AUTHORIZATION, "Basic " + getBasicAuth(externalTracker))
                .retrieve().toBodilessEntity().block();

        repository.deleteById(employee.getId());
    }

    public EmployeeDto createEmployee(Long externalTrackerId, Employee employee) {

        ExternalTrackerDto externalTracker = externalTrackerService.findOne(externalTrackerId);
        employee.setExternalTracker(externalTrackerRepository.getById(externalTrackerId));

        String url = String.format(CREATE_FORMAT_PATH, externalTracker.getTeamUrl());

        Map responseEmployee = webClient.post()
                .uri(URI.create(url))
                .header(AUTHORIZATION, "Basic " + getBasicAuth(externalTracker))
                .bodyValue(Map.of(
                        "emailAddress", employee.getEmail()
                ))
                .retrieve().bodyToMono(Map.class).block();

        employee.setId((String) responseEmployee.get("accountId"));

        repository.save(employee);

        EmployeeDto employeeDto = new EmployeeDto(responseEmployee);

        return employeeDto;
    }

    public void updateEmployeeDto(EmployeeDto employeeDto) {
        Optional<Employee> employeeOptional = repository.findByEmailAndId(employeeDto.getEmail(), employeeDto.getId());
        if(employeeOptional.isPresent()){
            Employee employee = employeeOptional.get();
            employeeDto.setRole(employee.getRole());
            employeeDto.setFirstname(employee.getFirstname());
            employeeDto.setLastname(employee.getLastname());
            employeeDto.setId(employee.getId());
        }
    }

    public static boolean isAccountTypeEqualsToAtlassian(Object employee) {
        Map<String, Object> employeeMap = (Map) employee;
        return employeeMap.containsKey("accountType") && ((String) employeeMap.get("accountType")).equalsIgnoreCase("atlassian") &&
                employeeMap.containsKey("active") && ((Boolean) employeeMap.get("active"));
    }
}
