package me.sonam.catalog.router;

import me.sonam.catalog.repo.ApplicationRepository;
import me.sonam.catalog.repo.ComponentRepository;
import me.sonam.catalog.repo.ConnectionRepository;
import me.sonam.catalog.repo.entity.Application;
import me.sonam.catalog.repo.entity.Component;
import me.sonam.catalog.repo.entity.Connection;
import me.sonam.catalog.repo.entity.ConnectionForm;
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
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@EnableAutoConfiguration
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ConnectionRestServiceIntegTest {
    private static final Logger LOG = LoggerFactory.getLogger(ConnectionRestServiceIntegTest.class);

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private ConnectionRepository connectionRepository;

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private ComponentRepository componentRepository;

    @Test
    public void getPage() {
        UUID appId = UUID.randomUUID();
        UUID targetId = UUID.randomUUID();
        UUID serviceEndpointId = UUID.randomUUID();
        UUID serviceId = UUID.randomUUID();

        for (int i = 0; i < 10; i++) {
            Connection connection = new Connection(Connection.ConnectionType.READ, Connection.CONNECTING.COMPONENT.name(),
                    serviceEndpointId, targetId, serviceId, appId);

            connectionRepository.save(connection).subscribe(connection1 -> LOG.info("saved connection"));
        }

        LOG.info("get Page of clusters");
        EntityExchangeResult<String> result = webTestClient.get().uri("/connections")
                .exchange().expectStatus().isOk().expectBody(String.class).returnResult();

        LOG.info("found page of connections: {}", result.getResponseBody());
    }

    @Test
    public void connect() {
        Application application = new Application();
        application.setName("Closed Circuit Application");
        application.setDeprecated(false);
        application.setDescription("this is a closed circuit tv app");
        application.setDocumentationUrl("http://www.circuitdummy.co.ohl.com");
        application.setGitRepo("http://github.com/org/project");
        application.setIsNew(true);
        application.setId(UUID.randomUUID());

        Mono<Application> applicationMono = applicationRepository.save(application);
        Component component = new Component("mykafka-queue", null);
        component.setIsNew(true);
        component.setId(UUID.randomUUID());
        Mono<Component> componentMono = componentRepository.save(component);

        Component dbComponent = new Component("myCatalogDatabase", null);
        dbComponent.setIsNew(true);
        dbComponent.setId(UUID.randomUUID());
        Mono<Component> dbComponentMono = componentRepository.save(dbComponent);

        applicationMono.zipWith(componentMono).zipWith(dbComponentMono).subscribe(objects -> {
            LOG.info("saved application, component and dbComponent");
        });

        LOG.info("connect app {} with component {}", application.getId(), component.getId());

        UUID serviceEndpointId = UUID.randomUUID();

        ConnectionForm connectionForm = new ConnectionForm();
        connectionForm.setAppId(application.getId());
        connectionForm.setServiceEndpointId(serviceEndpointId);
        connectionForm.setConnecting(Connection.CONNECTING.COMPONENT.name());
        LOG.info("set connection from app to queue and db components");
        connectionForm.setTargetIdList(Arrays.asList(component.getId(), dbComponent.getId()));

        webTestClient.post().uri("/connections").bodyValue(connectionForm)
                .exchange().expectStatus().isOk().expectBody(String.class).isEqualTo("connection updated");

        LOG.info("checking if connection is found");
        connectionRepository.findByServiceEndpointIdAndConnecting(serviceEndpointId, Connection.CONNECTING.COMPONENT.name()).subscribe(
                connection -> LOG.info("after saving found connection: {}", connection));


        connectionRepository.findAll().subscribe(connection -> LOG.info("found connection in all: {}", connection));

        EntityExchangeResult<Component[]> result = webTestClient.get().uri("/connections/serviceEndpointId/"+serviceEndpointId+"/component")
                .exchange().expectStatus().isOk().expectBody(Component[].class).returnResult();

        LOG.info("got app connected components: {}", result.getResponseBody());
        Component[] components = result.getResponseBody();
        LOG.info("components found: {}", components.length);
        for(Component component1: components) {
            LOG.info("component: {}", component1);
        }
        assertThat(components.length).isEqualTo(2);
    }
}
