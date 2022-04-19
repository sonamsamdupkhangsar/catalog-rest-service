package me.sonam.catalog.repo;


import me.sonam.catalog.repo.entity.ApplicationStatus;
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
public class ApplicationStatusRepositoryIntegTest {

    private static final Logger LOG = LoggerFactory.getLogger(ApplicationStatusRepositoryIntegTest.class);

    @Autowired
    private WebTestClient client;

    @Autowired
    private ApplicationStatusRepository applicationStatusRepository;

    @Test
    public void save() {
        UUID applicationId = UUID.randomUUID();
        String applicationName = "Journal app";
        String devStatus = "running";
        UUID devEnvironmentId = UUID.randomUUID();
        UUID platformId = UUID.randomUUID();
        String platform = "dev";
        String stageStatus = "running";
        UUID stageEnvironmentId = UUID.randomUUID();
        String prodStatus = "running";
        UUID prodEnvironmentId = UUID.randomUUID();

        ApplicationStatus applicationStatus = new ApplicationStatus();
        applicationStatus.setApplicationId(applicationId);
        applicationStatus.setApplicationName(applicationName);
        applicationStatus.setDevStatus(devStatus);
        applicationStatus.setDevEnvironmentId(devEnvironmentId);
        applicationStatus.setPlatformId(platformId);
        applicationStatus.setPlatform(platform);
        applicationStatus.setStageStatus(stageStatus);
        applicationStatus.setStageEnvironmentId(stageEnvironmentId);
        applicationStatus.setProdStatus(prodStatus);
        applicationStatus.setProdEnvironmentId(prodEnvironmentId);


        Mono<ApplicationStatus> applicationStatusMono = applicationStatusRepository.save(applicationStatus);
        applicationStatusMono.as(StepVerifier::create)
                .assertNext(actual -> {
                    assertThat(actual.getApplicationId()).isEqualTo(applicationId);
                    assertThat(actual.getApplicationName()).isEqualTo(applicationName);
                    assertThat(actual.getDevStatus()).isEqualTo(devStatus);
                    assertThat(actual.getDevEnvironmentId()).isEqualTo(devEnvironmentId);
                    assertThat(actual.getPlatformId()).isEqualTo(platformId);
                    assertThat(actual.getPlatform()).isEqualTo(platform);
                    assertThat(actual.getStageStatus()).isEqualTo(stageStatus);
                    assertThat(actual.getStageEnvironmentId()).isEqualTo(stageEnvironmentId);
                    assertThat(actual.getProdStatus()).isEqualTo(prodStatus);
                    assertThat(actual.getProdEnvironmentId()).isEqualTo(prodEnvironmentId);
                    LOG.info("verify complete");
                })
                .verifyComplete();

        applicationStatusMono = applicationStatusRepository.findById(applicationStatus.getId());
        applicationStatusMono.subscribe(applicationStatus1 -> {
            UUID appId = UUID.randomUUID();
            applicationStatus1.setIsNew(false);
            applicationStatus1.setApplicationId(appId);
            applicationStatusRepository.save(applicationStatus1).subscribe(applicationStatus2 -> {
                assertThat(applicationStatus2.getApplicationId()).isEqualTo(appId);
                LOG.info("asserted updated to application");
            });

        });

        applicationStatusMono = applicationStatusRepository.findById(applicationStatus.getId());
        applicationStatusMono.as(StepVerifier::create)
                .assertNext(actual ->
                {
                    applicationStatusRepository.delete(actual).subscribe(app1 -> assertThat(app1).isNull());

                    LOG.info("deleted applicationStatus");
                    assertThat(applicationStatusRepository.findById(actual.getId()).subscribe(applicationStatus1
                            -> assertThat(applicationStatus1).isNull()));
                    LOG.info("should return null after deleted");
                })
                .verifyComplete();
    }
}
