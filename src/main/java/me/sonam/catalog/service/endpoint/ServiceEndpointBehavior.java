package me.sonam.catalog.service.endpoint;

import me.sonam.catalog.repo.entity.Service;
import me.sonam.catalog.repo.entity.ServiceEndpoint;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

/**
 * this defines the serviceEndpoint behaviors supported
 */
public interface ServiceEndpointBehavior {
    // get serviceEndpoints by serviceId
    Flux<ServiceEndpoint> getServiceEndpoints(UUID serviceId);
    Mono<ServiceEndpoint> update(ServiceEndpoint serviceEndpoint);
    Mono<String> delete(UUID serviceId);
}
