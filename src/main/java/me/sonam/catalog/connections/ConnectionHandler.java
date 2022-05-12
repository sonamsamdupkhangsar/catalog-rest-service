package me.sonam.catalog.connections;

import me.sonam.catalog.repo.entity.Application;
import me.sonam.catalog.repo.entity.Component;
import me.sonam.catalog.repo.entity.ConnectionForm;
import me.sonam.catalog.util.Util;
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
public class ConnectionHandler {
    private static final Logger LOG = LoggerFactory.getLogger(ConnectionHandler.class);

    @Autowired
    private ConnectionBehavior connectionBehavior;

    public Mono<ServerResponse> getPage(ServerRequest serverRequest) {
        LOG.info("get connection page");
        Pageable pageable = Util.getPageable(serverRequest);

        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
                .body(connectionBehavior.getPage(pageable), Page.class)
                .onErrorResume(e -> ServerResponse.badRequest().body(BodyInserters
                        .fromValue(e.getMessage())));
    }


    public Mono<ServerResponse> getConnectedComponents(ServerRequest serverRequest) {
        LOG.info("getConnectedComponents");
        UUID applicationId = UUID.fromString(serverRequest.pathVariable("serviceEndpointId"));

        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).
                body(connectionBehavior.getConnectedComponents(applicationId), Component.class)
                .onErrorResume(e -> ServerResponse.badRequest().body(BodyInserters
                        .fromValue(e.getMessage())));
    }

    public Mono<ServerResponse> connect(ServerRequest serverRequest) {//ConnectionForm connectionForm) {
        LOG.info("connect");
        Mono<ConnectionForm> mono = serverRequest.bodyToMono(ConnectionForm.class);

        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).
                body(mono.flatMap(connectionForm ->
                        connectionBehavior.connect(connectionForm)), String.class)
                .onErrorResume(e -> ServerResponse.badRequest().body(BodyInserters
                        .fromValue(e.getMessage())));
    }

    public Mono<ServerResponse> getConnectedApps(ServerRequest serverRequest) {
        LOG.info("getConnectedApps");
        UUID applicationId = UUID.fromString(serverRequest.pathVariable("serviceEndpointId"));

        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).
                body(connectionBehavior.getConnectedApps(applicationId), Application.class)
                .onErrorResume(e -> ServerResponse.badRequest().body(BodyInserters
                        .fromValue(e.getMessage())));
    }

    public Mono<ServerResponse> deleteByServiceEndpoint(ServerRequest serverRequest) {
        LOG.info("delete by serviceEndpointId");
        UUID serviceEndpointId = UUID.fromString(serverRequest.pathVariable("serviceEndpointId"));

        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
                .body(connectionBehavior.deleteByServiceEndpointId(serviceEndpointId), String.class)
                .onErrorResume(e -> ServerResponse.badRequest().body(BodyInserters
                        .fromValue(e.getMessage())));
    }

    public Mono<ServerResponse> deleteByServiceId(ServerRequest serverRequest) {
        LOG.info("delete by serviceId");
        UUID serviceId = UUID.fromString(serverRequest.pathVariable("serviceId"));

        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
                .body(connectionBehavior.deleteByServiceId(serviceId), String.class)
                .onErrorResume(e -> ServerResponse.badRequest().body(BodyInserters
                        .fromValue(e.getMessage())));
    }
}
