package me.sonam.catalog.appstatus;

import me.sonam.catalog.repo.entity.ApplicationServiceStatus;
import me.sonam.catalog.repo.entity.ApplicationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface ApplicationStatusBehavior {
    Mono<Page<ApplicationStatus>> getApplications(Pageable pageable);
    Flux<ApplicationServiceStatus> getAppByEnv(UUID appId, UUID envId);
}
