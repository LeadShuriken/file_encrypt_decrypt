package com.encrypto.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

import redis.embedded.RedisServer;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import com.encrypto.models.RedisProps;

@Configuration
@Order(Ordered.HIGHEST_PRECEDENCE)
@ConditionalOnProperty(prefix = "redis", name = "isEmbeded", havingValue = "true")
public class RedisEmbededConfig {

    @Autowired
    private RedisProps redisProps;
    private RedisServer redisServer;

    @PostConstruct
    private void startEmbeded() {
        this.redisServer = RedisServer.builder().port(redisProps.getPort())
                .setting("maxmemory " + redisProps.getMaxMemoryMB() + "M").build();
        this.redisServer.start();
    }

    @PreDestroy
    private void stopEmbeded() {
        this.redisServer.stop();
    }
}
