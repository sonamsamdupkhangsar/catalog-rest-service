package me.sonam.catlog.repo;

import me.sonam.catlog.repo.entity.Application;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.reactive.ReactiveSortingRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface ApplicationRepository extends ReactiveSortingRepository<Application, UUID> {
    Flux<Application> findByPlatformId(UUID platformId, Pageable pageable);
    Mono<Integer> countByPlatformId(UUID platformId);
    Flux<Application> findAllBy(Pageable pageable);
}
