package com.clarity.config;

import org.neo4j.driver.Driver;
import org.neo4j.driver.Session;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

@Configuration
public class Neo4jRepoConfig {

//    @Bean
//    @Scope("singleton")
//    public Session getSession(Driver driver) {
//        Session session = driver.session();
////        return session;
////        try (Session session = driver.session()) {
////            return session;
////        }
//    }
}
