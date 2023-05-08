package com.m2z.tools.security.config;

import com.m2z.tools.security.model.CorsConfigProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


/*
Sets allowed origins for prod and development.
Required for browsers since its easy for bad actors to make a request on the users behalf
Example www.evil.com loads and makes a request to facebook.com
To protect against this we set allowed origins header on the server side and a preflight request is sent from the browser
We do not need csrf since the browser is not automaticlly authentacting us via cookies or basic auth
 */
@Configuration
public class CorsConfigurer {

    @Bean
    public WebMvcConfigurer configureDevelopment(CorsConfigProperties corsConfigProperties) {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                WebMvcConfigurer.super.addCorsMappings(registry);
                CorsRegistration corsRegistration = registry.addMapping("/**");
                corsRegistration
                        .allowedOriginPatterns(corsConfigProperties.origins())
                        .allowedMethods("*")
                        .allowedHeaders("*");
            }
        };
    }
}
