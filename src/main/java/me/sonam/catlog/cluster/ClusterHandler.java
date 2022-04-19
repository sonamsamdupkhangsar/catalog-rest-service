package me.sonam.catlog.cluster;

import me.sonam.catlog.util.Util;
import me.sonam.catlog.repo.entity.Cluster;
import me.sonam.catlog.repo.entity.Environment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Component
public class ClusterHandler {
    private static final Logger LOG = LoggerFactory.getLogger(ClusterHandler.class);

    @Autowired
    private ClusterBehavior clusterBehavior;

    public Mono<ServerResponse> getPage(ServerRequest serverRequest) {
        LOG.info("get cluster page");
        Pageable pageable = Util.getPageable(serverRequest);

        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
                .body(clusterBehavior.getClusters(pageable), Page.class)
                .onErrorResume(e -> ServerResponse.badRequest().body(BodyInserters
                        .fromValue(e.getMessage())));
    }

    public Mono<ServerResponse> getCluster(ServerRequest serverRequest) {
        LOG.info("get cluster");
        UUID clusterId = UUID.fromString(serverRequest.pathVariable("clusterId"));

        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
                .body(clusterBehavior.getCluster(clusterId), Cluster.class)
                .onErrorResume(e -> ServerResponse.badRequest().body(BodyInserters
                        .fromValue(e.getMessage())));
    }

    public Mono<ServerResponse> getClustersNotAssociatedWith(ServerRequest serverRequest) {
        LOG.info("get clusters");
        UUID environmentId = UUID.fromString(serverRequest.pathVariable("environmentId"));

        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
                .body(clusterBehavior.getClustersNotAssociatedWith(environmentId), Cluster.class)
                .onErrorResume(e -> ServerResponse.badRequest().body(BodyInserters
                        .fromValue(e.getMessage())));
    }

    public Mono<ServerResponse> update(ServerRequest serverRequest) {
        LOG.info("update cluster");

        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
                .body(serverRequest.bodyToMono(Cluster.class).doOnNext(cluster -> clusterBehavior.update(cluster)),
                        Cluster.class)
                .onErrorResume(e -> ServerResponse.badRequest().body(BodyInserters
                        .fromValue(e.getMessage())));
    }

    public Mono<ServerResponse> getEnvironments(ServerRequest serverRequest) {
        LOG.info("get environments");
        UUID clusterId = UUID.fromString(serverRequest.pathVariable("clusterId"));

        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
                .body(clusterBehavior.getEnvrionments(clusterId), Environment.class)
                .onErrorResume(e -> ServerResponse.badRequest().body(BodyInserters
                        .fromValue(e.getMessage())));
    }

    public Mono<ServerResponse> delete(ServerRequest serverRequest) {
        LOG.info("delete cluster");
        UUID clusterId = UUID.fromString(serverRequest.pathVariable("clusterId"));

        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
                .body(clusterBehavior.delete(clusterId), String.class)
                .onErrorResume(e -> ServerResponse.badRequest().body(BodyInserters
                        .fromValue(e.getMessage())));
    }


}
