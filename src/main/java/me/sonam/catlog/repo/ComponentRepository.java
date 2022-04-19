package me.sonam.catlog.repo;

import me.sonam.catlog.repo.entity.Component;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface ComponentRepository extends ReactiveCrudRepository<Component, UUID> {
    // get all top level components such as kafka, redis, postgresdb
    Flux<Component> findByParentIdNull();

    Mono<Boolean> existsByParentId(UUID id);
    Flux<Component> findAllBy(Pageable pageable);
}
