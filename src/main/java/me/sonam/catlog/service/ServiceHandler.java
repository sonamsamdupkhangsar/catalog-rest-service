package me.sonam.catlog.service;

import me.sonam.catlog.util.Util;
import me.sonam.catlog.repo.entity.Environment;
import me.sonam.catlog.repo.entity.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.UUID;

@org.springframework.stereotype.Service
public class ServiceHandler {
    private static final Logger LOG = LoggerFactory.getLogger(ServiceHandler.class);

    @Autowired
    private ServiceBehavior serviceBehavior;

    public Mono<ServerResponse> getPage(ServerRequest serverRequest) {
        LOG.info("get service page");
        Pageable pageable = Util.getPageable(serverRequest);

        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
                .body(serviceBehavior.getPage(pageable), Page.class)
                .onErrorResume(e -> ServerResponse.badRequest().body(BodyInserters
                        .fromValue(e.getMessage())));
    }

    public Mono<ServerResponse> update(ServerRequest serverRequest) {
        LOG.info("update service");

        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
                .body(serverRequest.bodyToMono(Service.class).doOnNext(service -> serviceBehavior.update(service)),
                        Environment.class)
                .onErrorResume(e -> ServerResponse.badRequest().body(BodyInserters
                        .fromValue(e.getMessage())));
    }

    public Mono<ServerResponse> delete(ServerRequest serverRequest) {
        LOG.info("delete service");
        UUID environmentId = UUID.fromString(serverRequest.pathVariable("serviceId"));

        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
                .body(serviceBehavior.delete(environmentId), String.class)
                .onErrorResume(e -> ServerResponse.badRequest().body(BodyInserters
                        .fromValue(e.getMessage())));
    }

}
