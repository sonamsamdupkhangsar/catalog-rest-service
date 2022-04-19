package me.sonam.catlog.router;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import me.sonam.catlog.component.ComponentHandler;
import me.sonam.catlog.connections.ConnectionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;

@Configuration("connectionRouter")
@OpenAPIDefinition(info = @Info(title = "Application Status Router endpoints", version = "1.0", description = "Documentation APIs v1.0"))
public class ConnectionRouter {
    private static final Logger LOG = LoggerFactory.getLogger(ConnectionRouter.class);

    @Bean
    @RouterOperations(
            {
                    @RouterOperation(path = "/connections"
                            , produces = {
                            MediaType.APPLICATION_JSON_VALUE}, method= RequestMethod.GET,
                            operation = @Operation(operationId="applications", responses = {
                                    @ApiResponse(responseCode = "200", description = "successful operation"),
                                    @ApiResponse(responseCode = "400", description = "invalid user id")}
                            ))
            }
    )
    public RouterFunction<ServerResponse> connectionRoute(ConnectionHandler handler) {
        LOG.info("building application router function");
        return RouterFunctions.route(GET("/connections")
                        .and(accept(MediaType.APPLICATION_JSON)), handler::getPage);
    }
}
