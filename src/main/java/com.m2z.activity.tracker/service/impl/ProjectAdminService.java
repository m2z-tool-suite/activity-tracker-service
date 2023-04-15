package com.m2z.activity.tracker.service.impl;

import com.m2z.activity.tracker.config.CryptUtils;
import com.m2z.activity.tracker.config.security.TokenProvider;
import com.m2z.activity.tracker.dto.ProjectAdminDto;
import com.m2z.activity.tracker.dto.ProjectAdminLogin;
import com.m2z.activity.tracker.dto.Token;
import com.m2z.activity.tracker.entity.ProjectAdmin;
import com.m2z.activity.tracker.repository.definition.BaseRepository;
import com.m2z.activity.tracker.repository.impl.ProjectAdminRepository;
import com.m2z.activity.tracker.service.definition.BaseService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

@Service
public class ProjectAdminService extends BaseService<ProjectAdmin, ProjectAdminDto, Long> {

    private ProjectAdminRepository repository;
    private AuthenticationManager authenticationManager;
    private TokenProvider tokenProvider;
    private UserDetailsService userDetailsService;

    public ProjectAdminService(BaseRepository<ProjectAdmin, Long> repository, ProjectAdminRepository repository1, AuthenticationManager authenticationManager, TokenProvider tokenProvider, UserDetailsService userDetailsService) {
        super(repository);
        this.repository = repository1;
        this.authenticationManager = authenticationManager;
        this.tokenProvider = tokenProvider;
        this.userDetailsService = userDetailsService;
    }

    @Override
    public ProjectAdminDto convertToDTO(ProjectAdmin element) {
        return new ProjectAdminDto(element);
    }

//    public ProjectAdminDto login(ProjectAdminLogin projectAdminLogin) {
//        Optional<ProjectAdmin> admin = repository.findByEmailAndPassword(projectAdminLogin.getEmail(), projectAdminLogin.getPassword());
//
//        if(admin.isPresent()){
//            return new ProjectAdminDto(admin.get());
//        }
//
//        throw new ProcessingException("User not found");
//    }

    public Token login(@RequestBody ProjectAdminLogin user){
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());
        String jwt = tokenProvider.generateToken(userDetails);

        return new Token(jwt, "Client");
    }

    public Token register(ProjectAdmin projectAdmin) {
        String pass = projectAdmin.getPassword();
        ProjectAdminDto adminDto = save(projectAdmin);
        adminDto.setPassword(pass);

        return login(new ProjectAdminLogin(adminDto));
    }

    @Override
    public ProjectAdminDto save(ProjectAdmin admin) {

        admin.setPassword(CryptUtils.encrypt(admin.getPassword()));
        return super.save(admin);
    }
}
