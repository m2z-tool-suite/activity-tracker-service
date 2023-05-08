package com.m2z.tools.security.filters;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.m2z.tools.security.exception.BadTokenException;
import com.m2z.tools.security.exception.InternalTokenProcessingException;
import com.m2z.tools.security.model.PrincipleUser;
import com.m2z.tools.security.service.JwkService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.LocalDateTime;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Slf4j
public class CognitoAuthenticationFilter extends OncePerRequestFilter {

    private final static ObjectMapper mapper = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .configure(JsonGenerator.Feature.IGNORE_UNKNOWN, true)
            .registerModule( new SimpleModule().addSerializer(LocalDateTime.class, new LocalDateTimeSerializer()));
    private final JwkService jwkService;


    public CognitoAuthenticationFilter(JwkService jwkService) {
        this.jwkService = jwkService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {


        // TODO IF NOT COGNITO continue
//        String provider = request.getHeader("");
//        if (!provider.equalsIgnoreCase("cognito")) {
//            filterChain.doFilter(request, response);
//        }

        // IF COGNITO Process

        String idToken = request.getHeader("Authorization");

        if (idToken == null || !idToken.contains("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            idToken = idToken.substring(7);
            System.out.println(idToken);
            // Process the token
            PrincipleUser principleUser = jwkService.process(idToken);

            // we can have a custom implementation
            SecurityContextHolder.getContext().setAuthentication(
                    new UsernamePasswordAuthenticationToken(principleUser, null, principleUser.getAuthorities())
            );

            filterChain.doFilter(request, response);
        } catch (BadTokenException e ) {
            response.setContentType(APPLICATION_JSON_VALUE);
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            log.warn("Failed parsing token either missing info or expired");
            mapper.writeValue(response.getOutputStream(), new GenericResponseDTO("Bad id token", HttpStatus.UNAUTHORIZED.value()));
        } catch (InternalTokenProcessingException e) {
            log.error("Token processing internals are not working as intended", e);
        }
    }
}
