package com.m2z.activity.tracker.config.security;

import lombok.Getter;
import lombok.Setter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

@Getter
@Setter
public class UsernamePasswordAuthenticationWithToken extends UsernamePasswordAuthenticationToken {
    private String token;

    public UsernamePasswordAuthenticationWithToken(Object principal, Object credentials, String token) {
        super(principal, credentials);
        this.token = token;
    }

    public UsernamePasswordAuthenticationWithToken(Object principal, Object credentials, Collection<? extends GrantedAuthority> authorities, String token) {
        super(principal, credentials, authorities);
        this.token = token;
    }
}
