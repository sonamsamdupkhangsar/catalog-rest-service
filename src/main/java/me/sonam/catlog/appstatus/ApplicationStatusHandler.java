package me.sonam.catlog.appstatus;

import me.sonam.catlog.util.Util;
import me.sonam.catlog.repo.entity.ApplicationServiceStatus;
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
public class ApplicationStatusHandler {
    private static final Logger LOG = LoggerFactory.getLogger(ApplicationStatusHandler.class);

    @Autowired
    private ApplicationStatusBehavior applicationStatusBehavior;

    public Mono<ServerResponse> getPage(ServerRequest serverRequest) {
        LOG.info("getPage");
        Pageable pageable = Util.getPageable(serverRequest);

        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
                .body(applicationStatusBehavior.getApplications(pageable), Page.class)
                .onErrorResume(e -> ServerResponse.badRequest().body(BodyInserters
                        .fromValue(e.getMessage())));
    }

    public Mono<ServerResponse> getAppByEnv(ServerRequest serverRequest) {
        LOG.info("getAppByEnv");
        UUID applicationId = UUID.fromString(serverRequest.pathVariable("applicationId"));
        UUID environmentId = UUID.fromString(serverRequest.pathVariable("environmentId"));

        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).
                body(applicationStatusBehavior.getAppByEnv(applicationId, environmentId), ApplicationServiceStatus.class)
                .onErrorResume(e -> ServerResponse.badRequest().body(BodyInserters
                        .fromValue(e.getMessage())));
    }




}
