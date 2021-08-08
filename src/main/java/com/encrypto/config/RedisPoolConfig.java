package com.encrypto.config;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RedisPoolConfig {

    private Integer maxIdle;
    private Integer minIdle;
    private Integer maxWaitMillis;
    private Integer maxTotal;
}
