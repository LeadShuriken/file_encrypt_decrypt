package com.encrypto.controller;

import javax.validation.Valid;

import com.encrypto.model.FileStamp;

import org.springframework.data.redis.core.ReactiveRedisOperations;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/${api.version}")
public class EncryptoController {
    private final ReactiveRedisOperations<String, FileStamp> opps;

    EncryptoController(ReactiveRedisOperations<String, FileStamp> opps) {
        this.opps = opps;
    }

    @GetMapping("get")
    public @ResponseBody Flux<FileStamp> all() {
        return opps.keys("*").flatMap(opps.opsForValue()::get);
    }

    @PostMapping("encrypt")
    public @ResponseBody Mono<FileStamp> encrypt(@Valid @RequestBody final FileStamp nameStamp) {
        return Mono.just(nameStamp);
    }
}
