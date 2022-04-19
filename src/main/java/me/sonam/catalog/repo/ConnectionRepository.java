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
    void deleteByAppIdSource(UUID appId);
    void deleteByAppIdSourceAndConnecting(UUID appId, String appOrComp);
    Mono<Boolean> existsByAppIdSourceAndTargetId(UUID appId, UUID targetId);
    Mono<Boolean> deleteByAppIdSourceAndTargetId(UUID appId, UUID targetId);
}
