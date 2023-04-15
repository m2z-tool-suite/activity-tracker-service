package com.m2z.activity.tracker.config.security;

import com.m2z.activity.tracker.entity.ProjectAdmin;
import com.m2z.activity.tracker.repository.impl.ProjectAdminRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    @Autowired
    private WebClient webClient;

    @Autowired
    private ProjectAdminRepository projectAdminRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();

        Optional<ProjectAdmin> userOptional = projectAdminRepository.findByEmail(username);

        if(userOptional.isEmpty()){
            return null;
        }

        ProjectAdmin projectAdmin = userOptional.get();

//        user.getAuthorities().forEach(userAuthority -> authorities.add(
//                new SimpleGrantedAuthority(
//                        userAuthority.getName()))
//        );

        return new org.springframework.security.core.userdetails.User(projectAdmin.getEmail(), projectAdmin.getPassword(), authorities);
    }
}
