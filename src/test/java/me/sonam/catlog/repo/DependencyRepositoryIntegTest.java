package me.sonam.catlog.repo;


import me.sonam.catlog.repo.entity.Dependency;
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
public class DependencyRepositoryIntegTest {

    private static final Logger LOG = LoggerFactory.getLogger(DependencyRepositoryIntegTest.class);

    @Autowired
    private WebTestClient client;

    @Autowired
    private DependencyRepository dependencyRepository;

    @Test
    public void save() {
        UUID providerId = UUID.randomUUID();
        UUID consumerId = UUID.randomUUID();

       Dependency dependency = new Dependency(providerId, consumerId);

       assertThat(dependency.getId()).isNotNull();

        LOG.info("save dependency");
        Mono<Dependency> dependencyMono = dependencyRepository.save(dependency);
        dependencyMono.as(StepVerifier::create)
                .assertNext(actual -> {
                    assertThat(actual.getProviderId()).isEqualTo(providerId);
                    assertThat(actual.getConsumerId()).isEqualTo(consumerId);
                    assertThat(actual.getProviderId()).isNotEqualTo(UUID.randomUUID());
                    assertThat(actual.getConsumerId()).isNotEqualTo(UUID.randomUUID());
                    LOG.info("verify complete");
                })
                .verifyComplete();

        dependencyMono =dependencyRepository.findById(dependency.getId());
        dependencyMono.as(StepVerifier::create)
                .assertNext(actual ->
                {
                   dependencyRepository.delete(actual).subscribe(app1 -> assertThat(app1).isNull());

                    LOG.info("deleted dependency");
                    assertThat(dependencyRepository.findById(actual.getId()).subscribe(dependency1
                            -> assertThat(dependency1).isNull()));
                    LOG.info("should return null after deleted");
                })
                .verifyComplete();
    }
}
