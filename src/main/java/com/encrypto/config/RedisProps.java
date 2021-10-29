package com.encrypto.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;
import lombok.Setter;
import java.time.Duration;

@Setter
@Getter
@Configuration
@ConfigurationProperties(prefix = "redis")
public class RedisProps {

    private Integer maxMemoryMB;
    private Boolean isEmbeded;
    private String hostName;
    private String password;
    private Integer database;
    private Integer port;
    private Duration ttlMax;
    private Duration ttlMin;
    private RedisPool pool;
}
