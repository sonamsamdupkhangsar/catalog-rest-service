package me.sonam.catlog.repo;


import me.sonam.catlog.repo.entity.ApplicationEnvironment;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface ApplicationEnvironmentRepository extends ReactiveCrudRepository<ApplicationEnvironment, UUID> {
    Mono<ApplicationEnvironment> findByApplicationIdAndEnvironmentId(UUID applicationId, UUID environmentId);
    Mono<Boolean> existsByApplicationIdAndEnvironmentId(UUID applicationId, UUID environmentId);
    Flux<ApplicationEnvironment> findByEnvironmentId(UUID environmentId);
    Flux<ApplicationEnvironment> findByApplicationId(UUID applicationId);
    Mono<Void> deleteByApplicationIdAndEnvironmentId(UUID applicationId, UUID environmentId);


    void deleteByApplicationId(UUID applicationId);
}
