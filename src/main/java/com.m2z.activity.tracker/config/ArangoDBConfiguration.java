package com.m2z.activity.tracker.config;

import com.arangodb.ArangoDB;
import com.arangodb.springframework.annotation.EnableArangoRepositories;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableArangoRepositories
public class ArangoDBConfiguration {

    @Value("${arangodb.hosts}")
    private String hosts;

    @Value("${arangodb.port}")
    private int port;

    @Value("${arangodb.user}")
    private String user;

    @Value("${arangodb.password}")
    private String password;

    @Bean
    public ArangoDB arangoDB() {
        return new ArangoDB.Builder()
                .host(hosts, port)
                .user(user)
                .password(password)
                .build();
    }
}
