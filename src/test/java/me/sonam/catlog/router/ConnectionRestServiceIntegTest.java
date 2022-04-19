package me.sonam.catlog.router;

import me.sonam.catlog.repo.ConnectionRepository;
import me.sonam.catlog.repo.entity.Connection;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.EntityExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.UUID;

@EnableAutoConfiguration
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ConnectionRestServiceIntegTest {
    private static final Logger LOG = LoggerFactory.getLogger(ConnectionRestServiceIntegTest.class);

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private ConnectionRepository connectionRepository;

    @Test
    public void getPage() {
        for (int i = 0; i < 10; i++) {
            Connection connection = new Connection(Connection.ConnectionType.READ,
                    Connection.CONNECTING.COMPONENT.name(), UUID.randomUUID(), UUID.randomUUID());

            connectionRepository.save(connection).subscribe(connection1 -> LOG.info("saved connection"));
        }

        LOG.info("get Page of clusters");
        EntityExchangeResult<String> result = webTestClient.get().uri("/connections")
                .exchange().expectStatus().isOk().expectBody(String.class).returnResult();

        LOG.info("found page of connections: {}", result.getResponseBody());
    }

}
