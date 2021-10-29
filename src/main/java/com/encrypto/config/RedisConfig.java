package com.encrypto.config;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.ReactiveKeyCommands;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.connection.ReactiveStringCommands;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettucePoolingClientConfiguration;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import redis.embedded.RedisServer;
import java.time.Duration;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import com.encrypto.model.FileStamp;

@Configuration
public class RedisConfig {

    @Autowired
    private RedisProps redisProps;

    private RedisServer redisServer;

    @PostConstruct
    private void startEmbeded() {
        if (redisProps.getIsEmbeded()) {
            this.redisServer = RedisServer.builder().port(redisProps.getPort())
                    .setting("maxmemory " + redisProps.getMaxMemoryMB() + "M").build();
            this.redisServer.start();
        }
    }

    @PreDestroy
    private void stopEmbeded() {
        if (redisProps.getIsEmbeded()) {
            this.redisServer.stop();
        }
    }

    private RedisStandaloneConfiguration redisConfig() {
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration();
        config.setHostName(redisProps.getHostName());
        config.setPort(redisProps.getPort());
        config.setDatabase(redisProps.getDatabase());
        config.setPassword(RedisPassword.of(redisProps.getPassword()));
        return config;
    }

    private GenericObjectPoolConfig<LettuceClientConfiguration> redisPoolConfig() {
        GenericObjectPoolConfig<LettuceClientConfiguration> config = new GenericObjectPoolConfig<LettuceClientConfiguration>();
        RedisPool pool = redisProps.getPool();
        config.setMaxTotal(pool.getMaxTotal());
        config.setMaxIdle(pool.getMaxIdle());
        config.setMinIdle(pool.getMinIdle());
        config.setMaxWaitMillis(pool.getMaxWaitMillis());
        return config;
    }

    private LettucePoolingClientConfiguration letuceConfig() {
        return LettucePoolingClientConfiguration.builder().commandTimeout(Duration.ofSeconds(10))
                .shutdownTimeout(Duration.ZERO).poolConfig(redisPoolConfig()).build();
    }

    @Bean
    @Primary
    public ReactiveRedisConnectionFactory reactiveRedisConnectionFactory() {
        LettuceConnectionFactory lettuceConnectionFactory = new LettuceConnectionFactory(redisConfig(), letuceConfig());
        lettuceConnectionFactory.setShareNativeConnection(false);

        return lettuceConnectionFactory;
    }

    @Bean
    public ReactiveRedisTemplate<String, FileStamp> reactiveRedisTemplate(
            ReactiveRedisConnectionFactory reactiveRedisConnectionFactory) {
        Jackson2JsonRedisSerializer<FileStamp> serializer = new Jackson2JsonRedisSerializer<>(FileStamp.class);
        RedisSerializationContext.RedisSerializationContextBuilder<String, FileStamp> builder = RedisSerializationContext
                .newSerializationContext(new StringRedisSerializer());
        RedisSerializationContext<String, FileStamp> context = builder.value(serializer).build();
        return new ReactiveRedisTemplate<>(reactiveRedisConnectionFactory, context);
    }

    @Bean
    public ReactiveKeyCommands keyCommands(final ReactiveRedisConnectionFactory reactiveRedisConnectionFactory) {
        return reactiveRedisConnectionFactory.getReactiveConnection().keyCommands();
    }

    @Bean
    public ReactiveStringCommands stringCommands(final ReactiveRedisConnectionFactory reactiveRedisConnectionFactory) {
        return reactiveRedisConnectionFactory.getReactiveConnection().stringCommands();
    }
}
