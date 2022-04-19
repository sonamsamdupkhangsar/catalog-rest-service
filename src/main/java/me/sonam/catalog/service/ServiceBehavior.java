package me.sonam.catalog.service;

import me.sonam.catalog.repo.entity.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import reactor.core.publisher.Mono;

import java.util.UUID;

public interface ServiceBehavior {
    Mono<Page<Service>> getPage(Pageable pageable);
    Mono<Service> update(Service service);
    Mono<String> delete(UUID serviceId);
}
