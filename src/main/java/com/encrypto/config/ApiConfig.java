package com.encrypto.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "api")
public class ApiConfig {

    private String version;
    private Integer maxReqPerSec;
    private String allowedOrigin;
    private String originFilter;
}
