//package com.m2z.activity.tracker.controller;
//
//import com.m2z.activity.tracker.dto.ProjectAdminDto;
//import com.m2z.activity.tracker.dto.ProjectAdminLogin;
//import com.m2z.activity.tracker.dto.Token;
//import com.m2z.activity.tracker.entity.ProjectAdmin;
//import com.m2z.activity.tracker.service.impl.ProjectAdminService;
//import lombok.SneakyThrows;
//import org.springframework.http.HttpStatus;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.ResponseStatus;
//import org.springframework.web.bind.annotation.RestController;
//
//import java.util.List;
//
//
//@RestController
//@RequestMapping(value = "/api/project/admin")
//public class ProjectAdminController {
//
//    private ProjectAdminService service;
//
//    public ProjectAdminController(ProjectAdminService service) {
//        this.service = service;
//    }
//
//    @GetMapping
//    @ResponseStatus(HttpStatus.OK)
//    public List<ProjectAdminDto> getAll()
//    {
//        return service.findAll();
//    }
//
//
//    @PostMapping("login")
//    @ResponseStatus(HttpStatus.OK)
//    @SneakyThrows
//    public Token login(@RequestBody ProjectAdminLogin projectAdminLogin) {
//
//        return service.login(projectAdminLogin);
//    }
//
//    @PostMapping("register")
//    @ResponseStatus(HttpStatus.OK)
//    public Token register(@RequestBody ProjectAdmin projectAdminLogin) {
//
//        return service.register(projectAdminLogin);
//    }
//}
