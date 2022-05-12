package me.sonam.catalog.repo;

import me.sonam.catalog.repo.entity.Connection;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface ConnectionRepository extends ReactiveCrudRepository<Connection, UUID> {

    Flux<Connection> findAllBy(Pageable pageable);
    Flux<Connection> findByServiceEndpointId(UUID serviceEndpointId);

    Flux<Connection> findByServiceEndpointIdAndConnecting(UUID appId, String connecting);
    Mono<Boolean> existsByServiceEndpointIdAndTargetIdAndConnecting(UUID serviceEndpointId, UUID targetId, String connecting);
    //void deleteByAppIdSource(UUID appId);
    Mono<Long> deleteByServiceEndpointIdAndConnecting(UUID serviceEndpointId, String appOrComp);
   // Mono<Boolean> existsByAppIdSourceAndTargetId(UUID appId, UUID targetId);
   // Mono<Boolean> deleteByAppIdSourceAndTargetId(UUID appId, UUID targetId);
    Flux<Connection> findByAppIdAndConnecting(UUID applicationId, String connecting);
    Flux<Connection> findByAppId(UUID applicationId);
    Mono<Long> deleteByServiceEndpointId(UUID serviceEndpointId);
    Mono<Long> deleteByServiceId(UUID serviceId);
}
