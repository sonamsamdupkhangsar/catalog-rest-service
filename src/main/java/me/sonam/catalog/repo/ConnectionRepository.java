package me.sonam.catalog.repo;

import me.sonam.catalog.repo.entity.Connection;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface ConnectionRepository extends ReactiveCrudRepository<Connection, UUID> {

    Flux<Connection> findAllBy(Pageable pageable);
    Flux<Connection> findByAppIdSource(UUID appId);
    Flux<Connection> findByAppIdSourceAndConnecting(UUID appId, String connecting);
    Mono<Boolean> existsByAppIdSourceAndTargetIdAndConnecting(UUID appId, UUID targetId, String connecting);
    void deleteByAppIdSource(UUID appId);
    Mono<Long> deleteByAppIdSourceAndConnecting(UUID appId, String appOrComp);
    Mono<Boolean> existsByAppIdSourceAndTargetId(UUID appId, UUID targetId);
    Mono<Boolean> deleteByAppIdSourceAndTargetId(UUID appId, UUID targetId);
}
