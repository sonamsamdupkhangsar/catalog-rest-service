package me.sonam.catalog.component;

import me.sonam.catalog.repo.entity.Component;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface ComponentBehavior {
    Mono<Component> update(Component component);
    Flux<Component> getParentComponents();
    Mono<Page<Component>> getPage(Pageable pageable);
    Mono<Component> getComponent(UUID componentId);
    Mono<String> delete(UUID componentId);
}
