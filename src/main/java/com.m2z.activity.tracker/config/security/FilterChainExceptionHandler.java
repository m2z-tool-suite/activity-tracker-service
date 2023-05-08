//package com.m2z.activity.tracker.config.security;
//
//import io.jsonwebtoken.JwtException;
//import jakarta.servlet.FilterChain;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Qualifier;
//import org.springframework.security.core.AuthenticationException;
//import org.springframework.stereotype.Component;
//import org.springframework.web.filter.OncePerRequestFilter;
//import org.springframework.web.servlet.HandlerExceptionResolver;
//
//import java.io.IOException;
//
//@Component
//@Slf4j
//public class FilterChainExceptionHandler extends OncePerRequestFilter {
//    private HandlerExceptionResolver resolver;
//
//    public FilterChainExceptionHandler(@Autowired @Qualifier("handlerExceptionResolver") HandlerExceptionResolver resolver) {
//        this.resolver = resolver;
//    }
//
//    @Override
//    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
//            throws ServletException, IOException {
//
//        try {
//            filterChain.doFilter(request, response);
//        } catch (JwtException | AuthenticationException e) {
//            log.error("Spring Security Filter Chain Exception:", e);
//            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//            resolver.resolveException(request, response, null, e);
//        }
//    }
//}
