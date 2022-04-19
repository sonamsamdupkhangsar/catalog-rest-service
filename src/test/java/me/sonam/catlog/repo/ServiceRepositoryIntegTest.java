package me.sonam.catlog.repo;


import me.sonam.catlog.repo.entity.Service;
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
public class ServiceRepositoryIntegTest {

    private static final Logger LOG = LoggerFactory.getLogger(ServiceRepositoryIntegTest.class);

    @Autowired
    private WebTestClient client;

    @Autowired
    private ServiceRepository serviceRepository;

    @Test
    public void save() {
        UUID applicationId = UUID.randomUUID();
        Service service = new Service("ping service", applicationId);
        service.setDescription("a pinging service");
        service.setEndpoint("http://localhost/health/liveness");
        service.setHealthEndpoint(false);
        service.setAccessTokenRequired(true);
        service.setPingIt(true);
        service.setRestMethod("mymethod");

       assertThat(service.getId()).isNotNull();

        LOG.info("save service");
        Mono<Service> serviceMono = serviceRepository.save(service);
        serviceMono.as(StepVerifier::create)
                .assertNext(actual -> {
                    assertThat(actual.getName()).isEqualTo("ping service");
                    assertThat(actual.getDescription()).isEqualTo("a pinging service");
                    assertThat(actual.getEndpoint()).isEqualTo("http://localhost/health/liveness");
                    assertThat(actual.getHealthEndpoint()).isEqualTo(false);
                    assertThat(actual.getAccessTokenRequired()).isEqualTo(true);
                    assertThat(actual.getPingIt()).isEqualTo(true);
                    assertThat(actual.getRestMethod()).isEqualTo("mymethod");

                    LOG.info("verify complete");
                })
                .verifyComplete();

        serviceMono = serviceRepository.findById(service.getId());
        serviceMono.as(StepVerifier::create)
                .assertNext(actual ->
                {
                   serviceRepository.delete(actual).subscribe(service1 -> assertThat(service1).isNull());

                    LOG.info("deleted service");
                    assertThat(serviceRepository.findById(actual.getId()).subscribe(service1
                            -> assertThat(service1).isNull()));
                    LOG.info("should return null after deleted");
                })
                .verifyComplete();
    }
}
