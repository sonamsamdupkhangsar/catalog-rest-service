package me.sonam.catalog.repo;

import me.sonam.catalog.repo.entity.ServiceEndpoint;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface ServiceEndpointRepository extends ReactiveCrudRepository<ServiceEndpoint, UUID> {
    Flux<ServiceEndpoint> findByServiceId(UUID sericeId);
    Flux<ServiceEndpoint> findAllBy(Pageable pageable);
    Mono<Long> deleteByServiceId(UUID serviceId);
}
