package com.m2z.activity.tracker.controller;

import com.m2z.activity.tracker.dto.EmployeeDto;
import com.m2z.activity.tracker.entity.Employee;
import com.m2z.activity.tracker.repository.impl.EmployeeRepository;
import com.m2z.activity.tracker.service.impl.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

import static com.m2z.activity.tracker.entity.Role.CLIENT;


@RestController
@RequestMapping(value = "/api/employee")
public class EmployeeController {

    private final EmployeeService service;

    @Autowired
    private EmployeeRepository employeeRepository;

    public EmployeeController(EmployeeService service) {
        this.service = service;
    }

    @GetMapping("external-tracker/{externalTrackerId}")
    @ResponseStatus(HttpStatus.OK)
    public List<EmployeeDto> getAll(@PathVariable("externalTrackerId") Long externalTrackerId)
    {
        return service.findAll(externalTrackerId);
    }

    @GetMapping("{email}")
    @ResponseStatus(HttpStatus.OK)
    public EmployeeDto findById(@PathVariable("email") String email)
    {
        Optional<Employee> assignee = service.getRepository().findByEmail(email);
        if(assignee.isEmpty()) {
            EmployeeDto employeeDto = new EmployeeDto();
            employeeDto.setEmail(email);
            employeeDto.setRole(CLIENT);
            return employeeDto;
        }
        return new EmployeeDto(assignee.get());
    }

    @PostMapping("external-tracker/{externalTrackerId}")
    @ResponseStatus(HttpStatus.CREATED)
    public EmployeeDto create(@RequestBody Employee element, @PathVariable("externalTrackerId") Long externalTrackerId){
        return service.createEmployee(externalTrackerId, element);
    }

    @DeleteMapping("external-tracker/{externalTrackerId}/account/{accountId}")
    @ResponseStatus(HttpStatus.CREATED)
    public void delete(@PathVariable("externalTrackerId") Long externalTrackerId, @PathVariable("accountId") String accountId){
        service.delete(externalTrackerId, accountId);
    }
}
