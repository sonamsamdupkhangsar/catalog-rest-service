package me.sonam.catalog.application;

import me.sonam.catalog.util.Util;
import me.sonam.catalog.repo.entity.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
public class ApplicationHandler {
    private static final Logger LOG = LoggerFactory.getLogger(ApplicationHandler.class);

    @Autowired
    private ApplicationBehavior applicationBehavior;

    public Mono<ServerResponse> getApplications(ServerRequest serverRequest) {
        LOG.info("getApplications");
        Pageable pageable = Util.getPageable(serverRequest);

        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).
                body(applicationBehavior.getApplications(pageable), Page.class)
                .onErrorResume(e -> ServerResponse.badRequest().body(BodyInserters
                        .fromValue(e.getMessage())));
    }

    public Mono<ServerResponse> getPlatforms(ServerRequest serverRequest) {
        LOG.info("get cluster/platforms");
        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).
                body(applicationBehavior.getPlatforms(), Cluster.class)
                .onErrorResume(e -> ServerResponse.badRequest().body(BodyInserters
                        .fromValue(e.getMessage())));
    }

    public Mono<ServerResponse> getApplicationsByPlatform(ServerRequest serverRequest) {//UUID platformId, Pageable pageable){
        LOG.info("getApplicationsByPlatform");
        UUID platformId = UUID.fromString(serverRequest.pathVariable("platformId"));
        LOG.info("platformId: {}", platformId);
        Pageable pageable = Util.getPageable(serverRequest);

        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).
                body(applicationBehavior.getApplicationsByPlatform(platformId, pageable), Page.class)
                .onErrorResume(e -> ServerResponse.badRequest().body(BodyInserters
                        .fromValue(e.getMessage())));
    }

    public Mono<ServerResponse> getServicesByApplicationId(ServerRequest serverRequest) {//UUID application, Pageable pageable);
        LOG.info("getServicesByApplicationId");
        UUID applicationId = UUID.fromString(serverRequest.pathVariable("applicationId"));
        Pageable pageable = Util.getPageable(serverRequest);

        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).
                body(applicationBehavior.getServicesByApplicationId(applicationId, pageable), Page.class)
                .onErrorResume(e -> ServerResponse.badRequest().body(BodyInserters
                        .fromValue(e.getMessage())));
    }

    public Mono<ServerResponse> updateApplication(ServerRequest serverRequest) {//Application application) {
        LOG.info("updateApplication");

        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).
                body(serverRequest.bodyToMono(Application.class).doOnNext(application ->
                                applicationBehavior.updateApplication(application)), Application.class)
                .onErrorResume(e -> ServerResponse.badRequest().body(BodyInserters
                        .fromValue(e.getMessage())));
    }


    public Mono<ServerResponse> getEnvironmentAssociationForApplicationId(ServerRequest serverRequest) {
        LOG.info("getEnvironmentAssociationForApplicationId");
        UUID applicationId = UUID.fromString(serverRequest.pathVariable("applicationId"));
        LOG.info("applicationId: {}", applicationId);

        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).
                body(applicationBehavior.getEnvironmentAssociationForApplicationId(applicationId), EnvironmentAssociation.class)
                .onErrorResume(e -> ServerResponse.badRequest().body(BodyInserters
                        .fromValue(e.getMessage())));
    }

    public Mono<ServerResponse> getUnassociatedClustersForApplicationId(ServerRequest serverRequest) {
        LOG.info("getUnassociatedClustersForApplicationId");
        UUID applicationId = UUID.fromString(serverRequest.pathVariable("applicationId"));

        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).
                body(applicationBehavior.getUnassociatedClustersForApplicationId(applicationId), Cluster.class)
                .onErrorResume(e -> ServerResponse.badRequest().body(BodyInserters
                        .fromValue(e.getMessage())));
    }

    public Mono<ServerResponse> getClusterWithAssociatedEnvironmentsForApplicationId(ServerRequest serverRequest) {
        LOG.info("getClusterWithAssociatedEnvironmentsForApplicationId");
        UUID applicationId = UUID.fromString(serverRequest.pathVariable("applicationId"));

        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).
                body(applicationBehavior.getClusterWithAssociatedEnvironmentsForApplicationId(applicationId), Cluster.class)
                .onErrorResume(e -> ServerResponse.badRequest().body(BodyInserters
                        .fromValue(e.getMessage())));
    }

    public Mono<ServerResponse> associateEnvironment(ServerRequest serverRequest) {//UUID applicationId, EnvironmentAssociation[] environmentAssociations) {
        LOG.info("associateEnvironment");
        UUID applicationId = UUID.fromString(serverRequest.pathVariable("applicationId"));
        Flux<EnvironmentAssociation> flux = serverRequest.bodyToFlux(EnvironmentAssociation.class);

        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).
                body(flux.collectList().flatMapMany(environmentAssociations -> {
                    LOG.info("environmentAssociations from flux: {}", environmentAssociations);
                    return  applicationBehavior.associateEnvironment(applicationId, environmentAssociations);})
                        , EnvironmentAssociation.class)
                .onErrorResume(e -> ServerResponse.badRequest().body(BodyInserters
                        .fromValue(e.getMessage())));
    }

    public Mono<ServerResponse> associateCluster(ServerRequest serverRequest) {
        LOG.info("associateCluster");
        UUID applicationId = UUID.fromString(serverRequest.pathVariable("applicationId"));
        UUID clusterId = UUID.fromString(serverRequest.pathVariable("clusterId"));

        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).
                body(applicationBehavior.associateCluster(applicationId, clusterId), EnvironmentAssociation.class)
                .onErrorResume(e -> ServerResponse.badRequest().body(BodyInserters
                        .fromValue(e.getMessage())));
    }

    public Mono<ServerResponse> getConnectedComponents(ServerRequest serverRequest) {
        LOG.info("getConnectedComponents");
        UUID applicationId = UUID.fromString(serverRequest.pathVariable("applicationId"));

        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).
                body(applicationBehavior.getConnectedComponents(applicationId), Component.class)
                .onErrorResume(e -> ServerResponse.badRequest().body(BodyInserters
                        .fromValue(e.getMessage())));
    }

    public Mono<ServerResponse> getConnectedApps(ServerRequest serverRequest) {
        LOG.info("getConnectedApps");
        UUID applicationId = UUID.fromString(serverRequest.pathVariable("applicationId"));

        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).
                body(applicationBehavior.getConnectedApps(applicationId), Application.class)
                .onErrorResume(e -> ServerResponse.badRequest().body(BodyInserters
                        .fromValue(e.getMessage())));
    }
}
