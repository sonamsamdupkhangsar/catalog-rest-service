package me.sonam.catalog.router;

import me.sonam.catalog.repo.ClusterRepository;
import me.sonam.catalog.repo.EnvironmentRepository;
import me.sonam.catalog.repo.entity.Cluster;
import me.sonam.catalog.repo.entity.Environment;
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
import reactor.test.StepVerifier;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@EnableAutoConfiguration
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class EnvironmentRestServiceIntegTest {
    private static final Logger LOG = LoggerFactory.getLogger(EnvironmentRestServiceIntegTest.class);

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private EnvironmentRepository environmentRepository;

    @Autowired
    private ClusterRepository clusterRepository;

    @Test
    public void getPage() {
        Cluster cluster = new Cluster(UUID.randomUUID(), "My platform cluster");
        clusterRepository.save(cluster).subscribe(cluster1 -> LOG.info("saved cluster getPage"));

        for (int i = 0; i < 10; i++) {
            Environment environment = new Environment();
            environment.setSortOrder(2);
            environment.setEnvironmentType("dev");
            environment.setName("dev");
            environment.setDomain("domain");
            environment.setDeploymentLink("some deployment link");
            environment.setIsNew(true);
            environment.setClusterId(cluster.getId());
            environmentRepository.save(environment).subscribe(env2 -> LOG.info("save env2"));
        }

        LOG.info("get Page of environments");
        EntityExchangeResult<String> result = webTestClient.get().uri("/environments")
                .exchange().expectStatus().isOk().expectBody(String.class).returnResult();

        LOG.info("found page of environments: {}", result.getResponseBody());
    }

    @Test
    public void getByCluster() {
        Cluster cluster = new Cluster(UUID.randomUUID(), "My platform cluster");
        clusterRepository.save(cluster).subscribe(cluster1 -> LOG.info("saved cluster getByCluster"));

        for (int i = 0; i < 10; i++) {
            Environment environment = new Environment();
            environment.setSortOrder(2);
            environment.setEnvironmentType("dev");
            environment.setName("dev");
            environment.setDomain("domain");
            environment.setDeploymentLink("some deployment link");
            environment.setIsNew(true);
            environment.setClusterId(cluster.getId());
            environmentRepository.save(environment).subscribe(env2 -> LOG.info("save env2"));
        }
        LOG.info("get environment");
        EntityExchangeResult<Environment[]> result = webTestClient.get().uri("/environments/cluster/"+cluster.getId())
                .exchange().expectStatus().isOk().expectBody(Environment[].class).returnResult();

        LOG.info("found environment: {}", result.getResponseBody());
        assertThat(result.getResponseBody().length).isGreaterThan(9);
    }

    @Test
    public void update() {
        Cluster cluster = new Cluster(UUID.randomUUID(), "My platform cluster");
        clusterRepository.save(cluster).subscribe(cluster1 -> LOG.info("saved cluster update"));

        Environment environment = new Environment();
        environment.setSortOrder(2);
        environment.setEnvironmentType("dev");
        environment.setName("dev");
        environment.setDomain("domain");
        environment.setDeploymentLink("some deployment link");
        environment.setIsNew(true);
        environment.setClusterId(cluster.getId());
        //environmentRepository.save(environment).subscribe(env2 -> LOG.info("save env2"));

        environment.setName("new dev environment");

        EntityExchangeResult<Environment> result = webTestClient.post().uri("/environments").bodyValue(environment)
                .exchange().expectStatus().isOk().expectBody(Environment.class).returnResult();

        LOG.info("environment: {}", result.getResponseBody());
        assertThat(result.getResponseBody()).isNotNull();
    }

    @Test
    public void delete() {
        LOG.info("delete environment");
        Cluster cluster = new Cluster(UUID.randomUUID(), "My platform cluster");
        clusterRepository.save(cluster).subscribe(cluster1 -> LOG.info("saved cluster"));

        Environment environment = new Environment();
        environment.setSortOrder(2);
        environment.setEnvironmentType("dev");
        environment.setName("dev");
        environment.setDomain("domain");
        environment.setDeploymentLink("some deployment link");
        environment.setIsNew(true);
        environment.setClusterId(cluster.getId());
        environmentRepository.save(environment).subscribe(env2 -> LOG.info("save env2"));

        EntityExchangeResult<Environment> result = webTestClient.delete().uri("/environments/"+environment.getId())
                .exchange().expectStatus().isOk().expectBody(Environment.class).returnResult();

        LOG.info("found environment: {}", result.getResponseBody());
        assertThat(result.getResponseBody()).isNull();

        environmentRepository.existsById(environment.getId()).as(StepVerifier::create)
                .assertNext(aBoolean -> assertThat(aBoolean).isFalse())
                .verifyComplete();
    }

    @Test
    public void getEnvironmentTypes() {
        LOG.info("get environmentTypes");
        EntityExchangeResult<String> result = webTestClient.get().uri("/environments/environmentTypes")
                .exchange().expectStatus().isOk().expectBody(String.class).returnResult();

        LOG.info("found environmentTypes {}", result.getResponseBody());
        assertThat(result.getResponseBody().split("\n").length).isEqualTo(4);
    }
}
