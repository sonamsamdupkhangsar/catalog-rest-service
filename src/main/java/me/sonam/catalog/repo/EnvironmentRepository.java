package me.sonam.catalog.repo;

import me.sonam.catalog.repo.entity.Environment;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.UUID;

public interface EnvironmentRepository extends ReactiveCrudRepository<Environment, UUID> {
    Flux<Environment> findByIdNotInAndClusterId(List<UUID> list, UUID clusterId);
    Flux<Environment> findByClusterIdOrderBySortOrderAsc(UUID clusterId);
    Flux<UUID> findIdByClusterIdOrderBySortOrderAsc(UUID clusterId);
    Flux<Environment> findAllBy(Pageable pageable);
}
