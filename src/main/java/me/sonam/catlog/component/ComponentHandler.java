package me.sonam.catlog.component;

import me.sonam.catlog.util.Util;
import me.sonam.catlog.repo.entity.Component;
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
public class ComponentHandler {
    private static final Logger LOG = LoggerFactory.getLogger(ComponentHandler.class);

    @Autowired
    private ComponentBehavior componentBehavior;

    public Mono<ServerResponse> getPage(ServerRequest serverRequest) {
        LOG.info("get component page");
        Pageable pageable = Util.getPageable(serverRequest);

        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
                .body(componentBehavior.getPage(pageable), Page.class)
                .onErrorResume(e -> ServerResponse.badRequest().body(BodyInserters
                        .fromValue(e.getMessage())));
    }

    public Mono<ServerResponse> getComponent(ServerRequest serverRequest) {
        LOG.info("get component");
        UUID componentId = UUID.fromString(serverRequest.pathVariable("componentId"));
        LOG.info("componentId: {}", componentId);
        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
                .body(componentBehavior.getComponent(componentId), Component.class)
                .onErrorResume(e -> ServerResponse.badRequest().body(BodyInserters
                        .fromValue(e.getMessage())));
    }

    public Mono<ServerResponse> update(ServerRequest serverRequest) {
        LOG.info("update component");

        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
                .body(serverRequest.bodyToMono(Component.class).doOnNext(cluster -> componentBehavior.update(cluster)),
                        Component.class)
                .onErrorResume(e -> ServerResponse.badRequest().body(BodyInserters
                        .fromValue(e.getMessage())));
    }

    public Mono<ServerResponse> getParentComponents(ServerRequest serverRequest) {
        LOG.info("get parent components");

        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
                .body(componentBehavior.getParentComponents(), Component.class)
                .onErrorResume(e -> ServerResponse.badRequest().body(BodyInserters
                        .fromValue(e.getMessage())));
    }

    public Mono<ServerResponse> delete(ServerRequest serverRequest) {
        LOG.info("delete component");
        UUID clusterId = UUID.fromString(serverRequest.pathVariable("componentId"));

        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
                .body(componentBehavior.delete(clusterId), String.class)
                .onErrorResume(e -> ServerResponse.badRequest().body(BodyInserters
                        .fromValue(e.getMessage())));
    }

}
