package me.sonam.catalog.environment;

import me.sonam.catalog.repo.entity.Environment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface EnvironmentBehavior {
    Mono<Page<Environment>> getPage(Pageable pageable);
    Flux<Environment> getByCluster(UUID clusterId);
    Mono<Environment> update(Environment environment);
    Mono<String> delete(UUID environmentId);
    Flux<String> getEnvironmentTypes();
}
