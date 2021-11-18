package com.encrypto.controllers;

import javax.validation.Valid;

import com.encrypto.models.FileStamp;
import com.encrypto.models.RedisProps;

import org.springframework.data.redis.core.ReactiveRedisOperations;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

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

    @PostMapping("decrypt")
    public @ResponseBody Mono<FileStamp> decrypt(@Valid @RequestBody final FileStamp file) {
        return opps.keys(file.getName()).flatMap(opps.opsForValue()::get)
                .filter(a -> enc.matches(file.getPassword(), a.getPassword()))
                .flatMap(a -> opps.opsForValue().delete(a.getName()).then(Mono.just(a))).next();
    }

    @PostMapping("encrypt")
    public @ResponseBody Mono<Boolean> store(@Valid @RequestBody final FileStamp file) {
        return Mono.just(file)
                .filter(a -> redisProps.getTtlMax().compareTo(a.getExpiration()) != -1
                        && redisProps.getTtlMin().compareTo(a.getExpiration()) != 1)
                .map(i -> new FileStamp(UUID.randomUUID().toString(), i.getName(), enc.encode(i.getPassword()),
                        i.getIv(), i.getSalt(), i.getExpiration(), new Date()))
                .flatMap(a -> opps.opsForValue().set(a.getName(), a, a.getExpiration()));
    }
}
