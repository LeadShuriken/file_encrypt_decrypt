package com.encrypto.controller;

import com.encrypto.model.FileStamp;

import org.springframework.data.redis.core.ReactiveRedisOperations;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
public class EncryptoController {
    private final ReactiveRedisOperations<String, FileStamp> opps;

    EncryptoController(ReactiveRedisOperations<String, FileStamp> opps) {
        this.opps = opps;
    }

    @GetMapping("/get")
    public Flux<FileStamp> all() {
        return opps.keys("*").flatMap(opps.opsForValue()::get);
    }
}
