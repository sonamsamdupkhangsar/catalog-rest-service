package me.sonam.catalog.connections;

import me.sonam.catalog.repo.entity.Application;
import me.sonam.catalog.repo.entity.Component;
import me.sonam.catalog.repo.entity.Connection;
import me.sonam.catalog.repo.entity.ConnectionForm;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface ConnectionBehavior {
    Mono<Page<Connection>> getPage(Pageable pageable);
    Flux<Component> getConnectedComponents(UUID applicationId);
    Mono<String> connect(ConnectionForm connectionForm);
    Flux<Application> getConnectedApps(UUID applicationId);
    Mono<String> deleteByServiceEndpointId(UUID serviceEndpointId);
    Mono<String> deleteByServiceId(UUID serviceId);
}
