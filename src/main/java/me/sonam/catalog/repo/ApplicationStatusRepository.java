package me.sonam.catalog.repo;

import me.sonam.catalog.repo.entity.ApplicationStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

import java.util.UUID;

public interface ApplicationStatusRepository extends ReactiveCrudRepository<ApplicationStatus, UUID> {
    Flux<ApplicationStatus> findAllBy(Pageable pageable);
}
