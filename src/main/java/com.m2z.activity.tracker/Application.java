package com.m2z.activity.tracker;

import com.m2z.tools.security.model.CorsConfigProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableConfigurationProperties({CorsConfigProperties.class})
@ComponentScan(basePackages = "com.m2z.tools.security")
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

}
