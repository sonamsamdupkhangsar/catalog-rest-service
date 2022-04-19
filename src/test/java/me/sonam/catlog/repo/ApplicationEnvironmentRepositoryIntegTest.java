package me.sonam.catlog.repo;

import me.sonam.catlog.repo.entity.ApplicationEnvironment;
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

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@EnableAutoConfiguration
@ExtendWith(SpringExtension.class)
@SpringBootTest( webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ApplicationEnvironmentRepositoryIntegTest {

    private static final Logger LOG = LoggerFactory.getLogger(ApplicationEnvironmentRepositoryIntegTest.class);

    @Autowired
    private WebTestClient client;

    @Autowired
    private ApplicationEnvironmentRepository applicationEnvironmentRepository;

    @Test
    public void save() {
        UUID applicationId = UUID.randomUUID();
        UUID envrionmentId = UUID.randomUUID();

       ApplicationEnvironment applicationEnvironment = new ApplicationEnvironment(applicationId, envrionmentId);


        Mono<ApplicationEnvironment> applicationEnvironmentMono = applicationEnvironmentRepository.save(applicationEnvironment);
        applicationEnvironmentMono.as(StepVerifier::create)
                .assertNext(actual -> {
                    assertThat(actual.getApplicationId()).isEqualTo(applicationId);
                    assertThat(actual.getEnvironmentId()).isEqualTo(envrionmentId);
                    LOG.info("verify complete");
                })
                .verifyComplete();

        applicationEnvironmentMono = applicationEnvironmentRepository.findByApplicationIdAndEnvironmentId(applicationId, envrionmentId);

        applicationEnvironmentMono.as(StepVerifier::create)
                .assertNext(actual ->
                {
                    applicationEnvironmentRepository.delete(actual).subscribe(applicationEnvironment1 ->
                            assertThat(applicationEnvironment1).isNull());

                    LOG.info("deleted application");
                    assertThat(applicationEnvironmentRepository.findByApplicationIdAndEnvironmentId(applicationId,
                            envrionmentId).subscribe(applicationEnvironment1 ->
                            assertThat(applicationEnvironment1).isNull()));
                    LOG.info("should return null after deleted");
                })
                .verifyComplete();
    }
}
