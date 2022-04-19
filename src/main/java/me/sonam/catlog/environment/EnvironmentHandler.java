package me.sonam.catlog.environment;

import me.sonam.catlog.util.Util;
import me.sonam.catlog.repo.entity.Environment;
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
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
public class EnvironmentHandler {
    private static final Logger LOG = LoggerFactory.getLogger(EnvironmentHandler.class);

    @Autowired
    private EnvironmentBehavior environmentBehavior;

    public Mono<ServerResponse> getPage(ServerRequest serverRequest) {
        LOG.info("get environment page");
        Pageable pageable = Util.getPageable(serverRequest);

        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
                .body(environmentBehavior.getPage(pageable), Page.class)
                .onErrorResume(e -> ServerResponse.badRequest().body(BodyInserters
                        .fromValue(e.getMessage())));
    }

    public Mono<ServerResponse> getByCluster(ServerRequest serverRequest) {
        LOG.info("getByCluster");
        UUID clusterId = UUID.fromString(serverRequest.pathVariable("clusterId"));

        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
                .body(environmentBehavior.getByCluster(clusterId), Environment.class)
                .onErrorResume(e -> ServerResponse.badRequest().body(BodyInserters
                        .fromValue(e.getMessage())));
    }

    public Mono<ServerResponse> update(ServerRequest serverRequest) {
        LOG.info("update environment");

        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
                .body(serverRequest.bodyToMono(Environment.class).doOnNext(environment -> environmentBehavior.update(environment)),
                        Environment.class)
                .onErrorResume(e -> ServerResponse.badRequest().body(BodyInserters
                        .fromValue(e.getMessage())));
    }

    public Mono<ServerResponse> getEnvironmentTypes(ServerRequest serverRequest) {
        LOG.info("getEnvironmentTypes");

        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
                .body(environmentBehavior.getEnvironmentTypes(), String.class)
                .onErrorResume(e -> ServerResponse.badRequest().body(BodyInserters
                        .fromValue(e.getMessage())));
    }

    public Mono<ServerResponse> delete(ServerRequest serverRequest) {
        LOG.info("delete environment");
        UUID environmentId = UUID.fromString(serverRequest.pathVariable("environmentId"));

        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
                .body(environmentBehavior.delete(environmentId), String.class)
                .onErrorResume(e -> ServerResponse.badRequest().body(BodyInserters
                        .fromValue(e.getMessage())));
    }

}
