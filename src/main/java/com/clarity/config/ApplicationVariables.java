package com.clarity.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "variables")
@Getter
@Setter
public class ApplicationVariables {

    private String deleteAllNodeQuery;
    private String updateCellTriiger;
}