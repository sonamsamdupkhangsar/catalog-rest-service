package me.sonam.catalog.connections;

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
}
