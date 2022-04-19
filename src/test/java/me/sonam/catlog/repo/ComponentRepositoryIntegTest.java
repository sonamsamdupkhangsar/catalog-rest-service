package me.sonam.catlog.repo;


import me.sonam.catlog.repo.entity.Cluster;
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
public class ComponentRepositoryIntegTest {

    private static final Logger LOG = LoggerFactory.getLogger(ComponentRepositoryIntegTest.class);

    @Autowired
    private WebTestClient client;

    @Autowired
    private ClusterRepository clusterRepository;

    @Test
    public void save() {
        Cluster cluster = new Cluster(UUID.randomUUID(), "production component");

        LOG.info("save component");
        Mono<Cluster> clusterMono = clusterRepository.save(cluster);
        clusterMono.as(StepVerifier::create)
                .assertNext(actual -> {
                    assertThat(actual.getName()).isEqualTo("production component");
                    LOG.info("verify complete");
                })
                .verifyComplete();

        clusterMono = clusterRepository.findById(cluster.getId());
        clusterMono.as(StepVerifier::create)
                .assertNext(actual ->
                {
                    clusterRepository.delete(actual).subscribe(app1 -> assertThat(app1).isNull());

                    LOG.info("deleted component");
                    assertThat(clusterRepository.findById(actual.getId()).subscribe(cluster1
                            -> assertThat(cluster1).isNull()));
                    LOG.info("should return null after deleted");
                })
                .verifyComplete();
    }
}
