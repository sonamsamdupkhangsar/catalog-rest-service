package me.sonam.catalog.repo;

import me.sonam.catalog.repo.entity.Cluster;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

import java.util.UUID;

public interface ClusterRepository extends ReactiveCrudRepository<Cluster, UUID> {
    Flux<Cluster> findAllBy(Pageable pageable);
    Flux<Cluster> findByIdNot(UUID clusterId);
}
