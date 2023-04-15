package com.m2z.activity.tracker.config.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.security.SignatureException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class TokenProvider {
    @Value("${secret}")
    private String secret;

    @Value("${tokenExpirationDay}")
    private Long days;

    private Claims getClaims(String token) throws SignatureException {
        try{
            return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
        }catch (Exception e){
            throw new SignatureException("Invalid claims");
        }
    }

    private boolean isExpired(String token){
        try {
            return getClaims(token).getExpiration().before(new Date(System.currentTimeMillis()));
        }catch (Exception e){
            throw new ExpiredJwtException(null, null, "Token expired");
        }
    }

    @SneakyThrows
    public String getUsername(String token){
        Claims claims = getClaims(token);
        try {
            String username = claims.get("username", String.class);
            return username;
        }catch (Exception e){
            throw new UsernameNotFoundException("Username not found");
        }
    }

    public boolean validateToken(String token, UserDetails userDetails){
        return getUsername(token).equals(userDetails.getUsername()) && !isExpired(token);
    }

    public String generateToken(UserDetails userDetails){
        Map<String, Object> claims = new HashMap<>();
        claims.put("username", userDetails.getUsername());
        claims.put("roles", userDetails.getAuthorities());
        claims.put("created", new Date(System.currentTimeMillis()));

        return Jwts.builder().setClaims(claims).setExpiration(new Date(System.currentTimeMillis() + days * 86400000)) // 1 day == 86,400,000 ms
                .signWith(SignatureAlgorithm.HS512, secret).compact();
    }

}
