package com.encrypto;

import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisOperations;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import javax.annotation.PostConstruct;

import com.encrypto.model.FileStamp;

import java.util.Date;
import java.util.UUID;

// @Component
public class DataLoader {

	private final ReactiveRedisConnectionFactory factory;
	private final ReactiveRedisOperations<String, FileStamp> opps;

	public DataLoader(ReactiveRedisConnectionFactory factory, ReactiveRedisOperations<String, FileStamp> opps) {
		this.factory = factory;
		this.opps = opps;
	}

	@PostConstruct
	public void loadData() {
		factory.getReactiveConnection().serverCommands().flushAll()
				.thenMany(Flux.just("A", "B", "C")
						.map(name -> new FileStamp(UUID.randomUUID().toString(), name, name + "_stamp", name + "_iv",
								new Date()))
						.flatMap(a -> opps.opsForValue().set(a.getId(), a)))
				.thenMany(opps.keys("*").flatMap(opps.opsForValue()::get)).subscribe(System.out::println);
	}
}
