package me.sonam.catalog.service.endpoint;

import me.sonam.catalog.repo.entity.Environment;
import me.sonam.catalog.repo.entity.Service;
import me.sonam.catalog.repo.entity.ServiceEndpoint;
import me.sonam.catalog.util.Util;
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
public class ServiceEndpointHandler {
    private static final Logger LOG = LoggerFactory.getLogger(ServiceEndpointHandler.class);

    @Autowired
    private ServiceEndpointBehavior serviceEndpointBehavior;

    public Mono<ServerResponse> getByServiceId(ServerRequest serverRequest) {
        LOG.info("get by serviceId");
        UUID serviceId = UUID.fromString(serverRequest.pathVariable("serviceId"));

        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
                .body(serviceEndpointBehavior.getServiceEndpoints(serviceId), ServiceEndpoint.class)
                .onErrorResume(e -> ServerResponse.badRequest().body(BodyInserters.fromValue(e.getMessage())));
    }

    public Mono<ServerResponse> update(ServerRequest serverRequest) {
        LOG.info("update serviceEndpoint");

        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
                .body(serverRequest.bodyToMono(ServiceEndpoint.class).doOnNext(serviceEndpoint ->
                                serviceEndpointBehavior.update(serviceEndpoint)),
                        ServiceEndpoint.class)
                .onErrorResume(e -> ServerResponse.badRequest().body(BodyInserters
                        .fromValue(e.getMessage())));
    }

    public Mono<ServerResponse> delete(ServerRequest serverRequest) {
        LOG.info("delete serviceEndpoint");
        UUID serviceEndpointId = UUID.fromString(serverRequest.pathVariable("serviceEndpointId"));

        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
                .body(serviceEndpointBehavior.delete(serviceEndpointId), String.class)
                .onErrorResume(e -> ServerResponse.badRequest().body(BodyInserters
                        .fromValue(e.getMessage())));
    }

}
