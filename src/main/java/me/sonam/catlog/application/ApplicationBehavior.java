package me.sonam.catlog.application;

import me.sonam.catlog.repo.entity.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

public interface ApplicationBehavior {
    Mono<Page<Application>> getApplications(Pageable pageable);
    Flux<Cluster> getPlatforms();
    Mono<Page<Application>> getApplicationsByPlatform(UUID platformId, Pageable pageable);
    Mono<Page<Service>> getServicesByApplicationId(UUID application, Pageable pageable);
    Mono<Application> updateApplication(Application application);
    Flux<EnvironmentAssociation> getEnvironmentAssociationForApplicationId(UUID applicationId);
    Flux<Cluster> getUnassociatedClustersForApplicationId(UUID applicationId);
    Mono<Cluster> getClusterWithAssociatedEnvironmentsForApplicationId(UUID applicationId);
    Flux<EnvironmentAssociation> associateEnvironment(UUID applicationId, List<EnvironmentAssociation> environmentAssociations);
    Flux<EnvironmentAssociation> associateCluster(UUID applicationId, UUID clusterId);
    Flux<Component> getConnectedComponents(UUID applicationId);
    Mono<String> connect(ConnectionForm connectionForm);
    Flux<Application> getConnectedApps(UUID applicationId);
}
