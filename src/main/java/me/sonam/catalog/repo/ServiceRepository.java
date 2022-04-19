package me.sonam.catalog.repo;

import me.sonam.catalog.repo.entity.Service;
import org.springframework.data.domain.Pageable;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface ServiceRepository extends ReactiveCrudRepository<Service, UUID> {
    Flux<Service> findByApplicationId(UUID applicationId);
    Flux<Service> findByApplicationId(UUID applicationId, Pageable pageable);
    Mono<Integer> countByApplicationId(UUID applicationId);
    Flux<Service> findAllBy(Pageable pageable);
}
