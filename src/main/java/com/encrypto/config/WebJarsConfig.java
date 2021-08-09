package com.encrypto.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

@Data
@Configuration
@ConfigurationProperties(prefix = "webjars")
public class WebJarsConfig {
    String bootstrap;
    String jquery;
}