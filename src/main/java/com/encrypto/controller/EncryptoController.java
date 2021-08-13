package com.encrypto.controller;

import javax.validation.Valid;

import com.encrypto.config.RedisProps;
import com.encrypto.model.FileStamp;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.ReactiveRedisOperations;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import ch.qos.logback.core.util.Duration;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Date;
import java.util.UUID;

@RestController
@RequestMapping("/${api.version}")
public class EncryptoController {

    private final RedisProps redisProps;
    private final PasswordEncoder enc;
    private final ReactiveRedisOperations<String, FileStamp> opps;

    EncryptoController(ReactiveRedisOperations<String, FileStamp> opps, PasswordEncoder enc, RedisProps redisProps) {
        this.redisProps = redisProps;
        this.opps = opps;
        this.enc = enc;
    }

    @GetMapping("get")
    public @ResponseBody Flux<FileStamp> all() {
        return opps.keys("*").flatMap(opps.opsForValue()::get);
    }

    @PostMapping("decrypt")
    public @ResponseBody Mono<FileStamp> decrypt(@Valid @RequestBody final FileStamp file) {
        return opps.keys(file.getName()).flatMap(opps.opsForValue()::get)
                .filter(a -> enc.matches(file.getPassword(), a.getPassword()))
                .flatMap(a -> opps.opsForValue().delete(a.getName()).then(Mono.just(a))).next();
    }

    @PostMapping("encrypt")
    public @ResponseBody Mono<Boolean> store(@Valid @RequestBody final FileStamp file) {
        return Mono.just(file)
                .map(i -> new FileStamp(UUID.randomUUID().toString(), i.getName(), enc.encode(i.getPassword()), i.getIv(),
                        i.getExpiration(), new Date()))
                .filter(a -> redisProps.getTtl().compareTo(a.getExpiration()) != -1)
                .flatMap(a -> opps.opsForValue().set(a.getName(), a, a.getExpiration()));
    }
}
