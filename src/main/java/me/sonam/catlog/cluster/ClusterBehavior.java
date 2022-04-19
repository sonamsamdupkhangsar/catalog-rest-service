package me.sonam.catlog.cluster;

import me.sonam.catlog.repo.entity.Cluster;
import me.sonam.catlog.repo.entity.Environment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface ClusterBehavior {
    Mono<Page<Cluster>> getClusters(Pageable pageable);
    Mono<Cluster> getCluster(UUID clusterId);
    Flux<Cluster> getClustersNotAssociatedWith(UUID environmentId);
    Mono<Cluster> update(Cluster cluster);
    Flux<Environment> getEnvrionments(UUID clusterId);
    Mono<String> delete(UUID clusterId);
}
