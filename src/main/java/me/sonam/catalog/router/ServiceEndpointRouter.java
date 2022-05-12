package me.sonam.catalog.router;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import me.sonam.catalog.service.ServiceHandler;
import me.sonam.catalog.service.endpoint.ServiceEndpointHandler;
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

@Configuration("serviceEndpointRouter")
@OpenAPIDefinition(info = @Info(title = "Application Service Endpoint Router endpoints",
        version = "1.0", description = "Documentation APIs v1.0"))
public class ServiceEndpointRouter {
    private static final Logger LOG = LoggerFactory.getLogger(ServiceEndpointRouter.class);

    @Bean
    @RouterOperations(
            {
                    @RouterOperation(path = "/serviceendpoint"
                            , produces = {
                            MediaType.APPLICATION_JSON_VALUE}, method= RequestMethod.GET,
                            operation = @Operation(operationId="applications", responses = {
                                    @ApiResponse(responseCode = "200", description = "successful operation"),
                                    @ApiResponse(responseCode = "400", description = "invalid user id")}
                            ))
            }
    )
    public RouterFunction<ServerResponse> serviceEndpointRoute(ServiceEndpointHandler handler) {
        LOG.info("building service router function");
        return RouterFunctions.route(GET("/serviceendpoint/{serviceId}")
                        .and(accept(MediaType.APPLICATION_JSON)), handler::getByServiceId)
                .andRoute(POST("/serviceendpoint")
                    .and(accept(MediaType.APPLICATION_JSON)), handler::update)
                .andRoute(DELETE("/serviceendpoint/{serviceEndpointId}")
                        .and(accept(MediaType.APPLICATION_JSON)), handler::delete)
                .andRoute(DELETE("/serviceendpoint/serviceId/{serviceId}")
                .and(accept(MediaType.APPLICATION_JSON)), handler::deleteByServiceId);

    }
}
