package com.encrypto.controller;

import javax.validation.Valid;

import com.encrypto.model.FileStamp;

import org.springframework.data.redis.core.ReactiveRedisOperations;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Date;
import java.util.UUID;

@RestController
@RequestMapping("/${api.version}")
public class EncryptoController {

    private final PasswordEncoder enc;
    private final ReactiveRedisOperations<String, FileStamp> opps;

    EncryptoController(ReactiveRedisOperations<String, FileStamp> opps, PasswordEncoder enc) {
        this.opps = opps;
        this.enc = enc;
    }

    @GetMapping("get")
    public @ResponseBody Flux<FileStamp> all() {
        return opps.keys("*").flatMap(opps.opsForValue()::get);
    }

    @PostMapping("decrypt")
    public @ResponseBody Mono<FileStamp> decrypt(@Valid @RequestBody final FileStamp file) {
        return opps.keys(file.getName()).flatMap(opps.opsForValue()::get).next().map(a -> {
            opps.opsForValue().delete(a.getName());
            return a;
        });
    }

    @PostMapping("encrypt")
    public @ResponseBody Mono<FileStamp> encrypt(@Valid @RequestBody final FileStamp nameStamp) {
        return Mono.just(nameStamp.getName())
                .map(i -> new FileStamp(UUID.randomUUID().toString(), i, enc.encode(i), new Date())).map(a -> {
                    opps.opsForValue().set(a.getName(), a);
                    return a;
                });
    }
}
