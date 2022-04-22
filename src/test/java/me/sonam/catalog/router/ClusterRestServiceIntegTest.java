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

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@EnableAutoConfiguration
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ClusterRestServiceIntegTest {
    private static final Logger LOG = LoggerFactory.getLogger(ClusterRestServiceIntegTest.class);

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private ClusterRepository clusterRepository;

    @Autowired
    private EnvironmentRepository environmentRepository;

    @Test
    public void getPage() {
        for (int i = 0; i < 10; i++) {
            Cluster cluster = new Cluster(UUID.randomUUID(), "My platform cluster");
            clusterRepository.save(cluster).subscribe(cluster1 -> LOG.info("saved cluster"));
        }

        LOG.info("get Page of clusters");
        EntityExchangeResult<String> result = webTestClient.get().uri("/clusters")
                .exchange().expectStatus().isOk().expectBody(String.class).returnResult();

        LOG.info("found page of clusters: {}", result.getResponseBody());
    }

    @Test
    public void getCluster() {
        Cluster cluster = new Cluster(UUID.randomUUID(), "My platform cluster");
        clusterRepository.save(cluster).subscribe(cluster1 -> LOG.info("saved cluster"));

        LOG.info("get cluster");
        EntityExchangeResult<Cluster> result = webTestClient.get().uri("/clusters/"+cluster.getId())
                .exchange().expectStatus().isOk().expectBody(Cluster.class).returnResult();

        LOG.info("found cluster: {}", result.getResponseBody());
        assertThat(result.getResponseBody()).isEqualTo(cluster);
    }

    @Test
    public void getClustersNotAssociatedWith() {
        Environment environment = new Environment();
        environmentRepository.save(environment).subscribe(environment1 -> LOG.info("saved environment"));

        for (int i = 0; i < 10; i++) {
            Cluster cluster = new Cluster(UUID.randomUUID(), "My platform cluster");
            if (i == 4) {
                environment.setIsNew(false);
                environment.setClusterId(cluster.getId());
                environmentRepository.save(environment).subscribe(environment1 -> LOG.info("associated environment with cluster"));
            }

            clusterRepository.save(cluster).subscribe(cluster1 -> LOG.info("saved cluster"));
        }
        LOG.info("get cluster not associated with environment");
        EntityExchangeResult<Cluster[]> result = webTestClient.get().uri("/clusters/environments/"+environment.getId())
                .exchange().expectStatus().isOk().expectBody(Cluster[].class).returnResult();

        LOG.info("found cluster.length: {}, clusters: {}", result.getResponseBody());
        assertThat(result.getResponseBody().length).isGreaterThan(8);
    }

    @Test
    public void update() {
        Cluster cluster = new Cluster(null, "My platform cluster");

        EntityExchangeResult<Cluster> result = webTestClient.post().uri("/clusters").bodyValue(cluster)
                .exchange().expectStatus().isOk().expectBody(Cluster.class).returnResult();

        LOG.info("cluster: {}", result.getResponseBody());
        assertThat(result.getResponseBody().getName()).isEqualTo("My platform cluster");

        LOG.info("get Page of clusters");
        EntityExchangeResult<String> clusterResult = webTestClient.get().uri("/clusters")
                .exchange().expectStatus().isOk().expectBody(String.class).returnResult();

        LOG.info("found page of clusters: {}", clusterResult.getResponseBody());

    }

    @Test
    public void getEnvironments() {
        LOG.info("get all envrionments associated with cluster-id");

        //create 1 cluster and associate 10 environments to it
        Cluster cluster = new Cluster(UUID.randomUUID(), "My platform cluster");
        clusterRepository.save(cluster).subscribe(cluster1 -> LOG.info("saved cluster"));

        for (int i = 0; i < 10; i++) {
            Environment environment = new Environment();
            environment.setClusterId(cluster.getId());
            environmentRepository.save(environment).subscribe(environment1 -> LOG.info("saved environment"));
        }
        EntityExchangeResult<Environment[]> result = webTestClient.get().uri("/clusters/associatedenvironments/"+cluster.getId())
                .exchange().expectStatus().isOk().expectBody(Environment[].class).returnResult();

        LOG.info("environments: {}", result.getResponseBody());
        assertThat(result.getResponseBody().length).isEqualTo(10);
    }

    @Test
    public void delete() {
        LOG.info("get all envrionments associated with cluster-id");

        //create 1 cluster and associate 10 environments to it
        Cluster cluster = new Cluster(UUID.randomUUID(), "My platform cluster");
        clusterRepository.save(cluster).subscribe(cluster1 -> LOG.info("saved cluster"));

        EntityExchangeResult<String> result = webTestClient.delete().uri("/clusters/"+cluster.getId())
                .exchange().expectStatus().isOk().expectBody(String.class).returnResult();

        LOG.info("deleted response: {}", result.getResponseBody());

        LOG.info("verify the rest endpoint returns null for the deleted cluster");

        LOG.info("get cluster");
        EntityExchangeResult<Cluster> result2 = webTestClient.get().uri("/clusters/"+cluster.getId())
                .exchange().expectStatus().isOk().expectBody(Cluster.class).returnResult();

        LOG.info("found cluster: {}", result.getResponseBody());
        assertThat(result.getResponseBody()).isNull();
    }
}
