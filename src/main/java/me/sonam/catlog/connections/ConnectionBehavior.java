package me.sonam.catlog.connections;

import me.sonam.catlog.repo.entity.Connection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Mono;

public interface ConnectionBehavior {
    Mono<Page<Connection>> getPage(Pageable pageable);
}
