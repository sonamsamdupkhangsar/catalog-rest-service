package me.sonam.catalog.repo;


import me.sonam.catalog.repo.entity.Connection;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.UUID;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@EnableAutoConfiguration
@ExtendWith(SpringExtension.class)
@SpringBootTest( webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ConnectionRepositoryIntegTest {

    private static final Logger LOG = LoggerFactory.getLogger(ConnectionRepositoryIntegTest.class);

    @Autowired
    private WebTestClient client;

    @Autowired
    private ConnectionRepository connectionRepository;

    @Test
    public void save() {
        UUID appId = UUID.randomUUID();
        UUID targetId = UUID.randomUUID();
        UUID serviceEndpointId = UUID.randomUUID();
        UUID serviceId = UUID.randomUUID();

        Connection connection = new Connection(Connection.ConnectionType.READ, Connection.CONNECTING.APP.name(),
                serviceEndpointId,targetId, serviceId, appId);

        LOG.info("save connection");
        Mono<Connection> connectionMono = connectionRepository.save(connection);
        connectionMono.as(StepVerifier::create)
                .assertNext(actual -> {
                    assertThat(actual.getConnecting()).isEqualTo(Connection.CONNECTING.APP.name());
                    assertThat(actual.getConnection()).isEqualTo(Connection.ConnectionType.READ.name());
                    assertThat(actual.getServiceEndpointId()).isEqualTo(serviceEndpointId);
                    assertThat(actual.getAppId()).isEqualTo(appId);
                    assertThat(actual.getTargetId()).isEqualTo(targetId);
                    LOG.info("verify complete");
                })
                .verifyComplete();

        connectionMono = connectionRepository.findById(connection.getId());
        connectionMono.as(StepVerifier::create)
                .assertNext(actual ->
                {
                    connectionRepository.delete(actual).subscribe(app1 -> assertThat(app1).isNull());

                    LOG.info("deleted connection");
                    assertThat(connectionRepository.findById(actual.getId()).subscribe(connection1
                            -> assertThat(connection1).isNull()));
                    LOG.info("should return null after deleted");
                })
                .verifyComplete();
    }
}
