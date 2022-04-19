package me.sonam.catlog.repo;

import me.sonam.catlog.repo.entity.Dependency;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

import java.util.UUID;

public interface DependencyRepository extends ReactiveCrudRepository<Dependency, UUID> {
    Flux<Dependency> findByConsumerId(UUID consumerId);

    Flux<Dependency> findByProviderId(UUID providerId);
}
