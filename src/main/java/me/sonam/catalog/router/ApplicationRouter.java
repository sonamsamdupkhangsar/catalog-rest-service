package me.sonam.catalog.router;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import me.sonam.catalog.application.ApplicationHandler;
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

@Configuration("applicationRouter")
@OpenAPIDefinition(info = @Info(title = "Application endpoints", version = "1.0", description = "Documentation APIs v1.0"))
public class ApplicationRouter {
    private static final Logger LOG = LoggerFactory.getLogger(ApplicationRouter.class);

    @Bean
    @RouterOperations(
            {
                    @RouterOperation(path = "/applications"
                    , produces = {
                        MediaType.APPLICATION_JSON_VALUE}, method= RequestMethod.GET,
                         operation = @Operation(operationId="applications", responses = {
                            @ApiResponse(responseCode = "200", description = "successful operation"),
                                 @ApiResponse(responseCode = "400", description = "invalid user id")}
                    ))
            }
    )
    public RouterFunction<ServerResponse> appRoute(ApplicationHandler handler) {
        LOG.info("building application router function");
        return RouterFunctions.route(GET("/applications").and(accept(MediaType.APPLICATION_JSON)),
                handler::getApplications)
                .andRoute(GET("/applications/platforms")
                .and(accept(MediaType.APPLICATION_JSON)), handler::getPlatforms)
                .andRoute(GET("/applications/platform/{platformId}")
                        .and(accept(MediaType.APPLICATION_JSON)), handler::getApplicationsByPlatform)
                .andRoute(GET("/applications/services/{applicationId}")
                .and(accept(MediaType.APPLICATION_JSON)), handler::getServicesByApplicationId)
                .andRoute(POST("/applications")
                        .and(accept(MediaType.APPLICATION_JSON)), handler::updateApplication)
                .andRoute(GET("/applications/{applicationId}/environments")
                        .and(accept(MediaType.APPLICATION_JSON)), handler::getEnvironmentAssociationForApplicationId)
                .andRoute(GET("/applications/{applicationId}/clusters")
                        .and(accept(MediaType.APPLICATION_JSON)), handler::getUnassociatedClustersForApplicationId)
                .andRoute(GET("/applications/{applicationId}/cluster")
                        .and(accept(MediaType.APPLICATION_JSON)), handler::getClusterWithAssociatedEnvironmentsForApplicationId)
                .andRoute(POST("/applications/{applicationId}/environments/update")
                        .and(accept(MediaType.APPLICATION_JSON)), handler::associateEnvironment)
                .andRoute(POST("/applications/{applicationId}/update/{clusterId}")
                        .and(accept(MediaType.APPLICATION_JSON)), handler::associateCluster)
                .andRoute(GET("/applications/{applicationId}/connection/component")
                        .and(accept(MediaType.APPLICATION_JSON)), handler::getConnectedComponents)
                .andRoute(GET("/applications/{applicationId}/connection/app")
                        .and(accept(MediaType.APPLICATION_JSON)), handler::getConnectedApps)
                .andRoute(POST("/applications/connection")
                        .and(accept(MediaType.APPLICATION_JSON)), handler::connect);
    }


}
