package me.sonam.catlog.repo;


import me.sonam.catlog.repo.entity.Application;
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
public class ApplicationRepositoryIntegTest {

    private static final Logger LOG = LoggerFactory.getLogger(ApplicationRepositoryIntegTest.class);

    @Autowired
    private WebTestClient client;

    @Autowired
    private ApplicationRepository applicationRepository;

    @Test
    public void save() {
        Application application = new Application();
        application.setName("Closed Circuit Application");
        application.setDeprecated(false);
        application.setDescription("this is a closed circuit tv app");
        application.setDocumentationUrl("http://www.circuitdummy.co.ohl.com");
        application.setGitRepo("http://github.com/org/project");
        application.setId(UUID.randomUUID());
        application.setIsNew(true);

        Mono<Application> applicationMono = applicationRepository.save(application);
        applicationMono.as(StepVerifier::create)
                .assertNext(actual -> {
                    assertThat(actual.getDeprecated()).isFalse();
                    assertThat(actual.getName()).isEqualTo("Closed Circuit Application");
                    assertThat(actual.getDescription()).isEqualTo("this is a closed circuit tv app");
                    assertThat(actual.getDocumentationUrl()).isEqualTo("http://www.circuitdummy.co.ohl.com");
                    assertThat(actual.getGitRepo()).isEqualTo("http://github.com/org/project");
                    assertThat(actual.isNew()).isTrue();
                    LOG.info("verify complete");
                })
                .verifyComplete();

        applicationMono = applicationRepository.findById(application.getId());
        applicationMono.subscribe(application1 -> {
            application1.setName("New Closed Circuit Application");
            applicationRepository.save(application1).subscribe(application2 -> {
                assertThat(application2.getName()).isEqualTo("New Closed Circuit Application");
                assertThat(application2.getName()).isNotEqualTo("Closed Circuit Application");
                LOG.info("asserted updated to application");
            });

        });

        applicationMono = applicationRepository.findById(application.getId());
        applicationMono.as(StepVerifier::create)
                .assertNext(actual ->
                {
                    applicationRepository.delete(actual).subscribe(app1 -> assertThat(app1).isNull());

                    LOG.info("deleted application");
                    assertThat(applicationRepository.findById(actual.getId()).subscribe(application1 -> assertThat(application1).isNull()));
                    LOG.info("should return null after deleted");
                })
                .verifyComplete();
    }
}
