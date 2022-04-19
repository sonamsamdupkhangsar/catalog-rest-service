package me.sonam.catlog.repo;


import me.sonam.catlog.repo.entity.Environment;
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
public class EnvironmentRepositoryIntegTest {

    private static final Logger LOG = LoggerFactory.getLogger(EnvironmentRepositoryIntegTest.class);

    @Autowired
    private WebTestClient client;

    @Autowired
    private EnvironmentRepository environmentRepository;

    @Test
    public void save() {
        UUID providerId = UUID.randomUUID();
        UUID consumerId = UUID.randomUUID();

       Environment environment = new Environment();
       environment.setSortOrder(1);
       environment.setEnvironmentType("test");
       environment.setName("dev");
       environment.setDomain("domain");
       environment.setDeploymentLink("some deployment link");
       UUID clusterId = UUID.randomUUID();
       environment.setClusterId(clusterId);

       assertThat(environment.getId()).isNotNull();

        LOG.info("save environment");
        Mono<Environment> environmentMono = environmentRepository.save(environment);
        environmentMono.as(StepVerifier::create)
                .assertNext(actual -> {
                    assertThat(actual.getSortOrder()).isEqualTo(1);
                    assertThat(actual.getEnvironmentType()).isEqualTo("test");
                    assertThat(actual.getName()).isEqualTo("dev");
                    assertThat(actual.getName()).isNotEqualTo("test");
                    assertThat(actual.getDomain()).isEqualTo("domain");
                    assertThat(actual.getDeploymentLink()).isEqualTo("some deployment link");
                    assertThat(actual.getClusterId()).isEqualTo(clusterId);
                    LOG.info("verify complete");
                })
                .verifyComplete();

        environmentMono = environmentRepository.findById(environment.getId());
        environmentMono.as(StepVerifier::create)
                .assertNext(actual ->
                {
                   environmentRepository.delete(actual).subscribe(environment1 -> assertThat(environment1).isNull());

                    LOG.info("deleted environment");
                    assertThat(environmentRepository.findById(actual.getId()).subscribe(environment1
                            -> assertThat(environment1).isNull()));
                    LOG.info("should return null after deleted");
                })
                .verifyComplete();
    }
}
