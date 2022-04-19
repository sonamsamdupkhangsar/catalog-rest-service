package me.sonam.catalog.repo;


import me.sonam.catalog.repo.entity.ApplicationServiceStatus;
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

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@EnableAutoConfiguration
@ExtendWith(SpringExtension.class)
@SpringBootTest( webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ApplicationServiceStatusRepositoryIntegTest {

    private static final Logger LOG = LoggerFactory.getLogger(ApplicationServiceStatusRepositoryIntegTest.class);

    @Autowired
    private WebTestClient client;

    @Autowired
    private ApplicationServiceStatusRepository applicationServiceStatusRepository;

    @Test
    public void save() {
        UUID applicationId = UUID.randomUUID();
        UUID serviceId = UUID.randomUUID();
        UUID environmentId = UUID.randomUUID();
        int httpStatusValue = 200;
        LocalDateTime localDateTime = LocalDateTime.now();
        String serviceEndpoint = "http://www.sonam.cloud/service/endpoint";


        ApplicationServiceStatus applicationServiceStatus = new ApplicationServiceStatus(applicationId, serviceId,
                environmentId, httpStatusValue, localDateTime, serviceEndpoint);

        Mono<ApplicationServiceStatus> applicationServiceStatusMono = applicationServiceStatusRepository.save(applicationServiceStatus);
        applicationServiceStatusMono.as(StepVerifier::create)
                .assertNext(actual -> {
                    assertThat(actual.getApplicationId()).isEqualTo(applicationId);
                    assertThat(actual.getServiceId()).isEqualTo(serviceId);
                    assertThat(actual.getEnvironmentId()).isEqualTo(environmentId);
                    assertThat(actual.getHttpStatusValue()).isEqualTo(httpStatusValue);
                    assertThat(actual.getLocalDateTime()).isEqualTo(localDateTime);
                    assertThat(actual.getServiceEndpoint()).isEqualTo(serviceEndpoint);
                    assertThat(actual.getServiceEndpoint()).isNotEqualTo("http://dummy.dum.com/nonexisting");
                    LOG.info("verify complete");
                })
                .verifyComplete();

        applicationServiceStatusMono = applicationServiceStatusRepository.findById(applicationServiceStatus.getId());
        applicationServiceStatusMono.subscribe(applicationServiceStatus1 -> {
            UUID appId = UUID.randomUUID();
            applicationServiceStatus1.setApplicationId(appId);
            applicationServiceStatusRepository.save(applicationServiceStatus1).subscribe(applicationServiceStatus2 -> {
                assertThat(applicationServiceStatus2.getApplicationId()).isEqualTo(appId);
                LOG.info("asserted updated to application");
            });

        });

        applicationServiceStatusMono = applicationServiceStatusRepository.findById(applicationServiceStatus.getId());
        applicationServiceStatusMono.as(StepVerifier::create)
                .assertNext(actual ->
                {
                    applicationServiceStatusRepository.delete(actual).subscribe(app1 -> assertThat(app1).isNull());

                    LOG.info("deleted applicationServiceStatus");
                    assertThat(applicationServiceStatusRepository.findById(actual.getId()).subscribe(applicationServiceStatus1
                            -> assertThat(applicationServiceStatus1).isNull()));
                    LOG.info("should return null after deleted");
                })
                .verifyComplete();
    }
}
