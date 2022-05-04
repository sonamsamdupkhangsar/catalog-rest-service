package me.sonam.catalog.repo;


import me.sonam.catalog.repo.entity.ServiceEndpoint;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@EnableAutoConfiguration
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ServiceEndpointRepositoryIntegTest {
    private static final Logger LOG = LoggerFactory.getLogger(ServiceEndpointRepositoryIntegTest.class);

    @Autowired
    private ServiceEndpointRepository serviceEndpointRepository;

    @Test
    public void create() {
        ServiceEndpoint serviceEndpoint = new ServiceEndpoint();
        serviceEndpoint.setIsNew(true);
        serviceEndpoint.setId(UUID.randomUUID());
        UUID serviceId = UUID.randomUUID();
        serviceEndpoint.setServiceId(serviceId);
        serviceEndpoint.setEndpoint("sonam.me/service/endpoint");
        serviceEndpoint.setDescription("this is a health service endpoint");
        serviceEndpoint.setRestMethod(ServiceEndpoint.REST_METHOD.GET.name());

        Mono<ServiceEndpoint> serviceEndpointMono = serviceEndpointRepository.save(serviceEndpoint);

        serviceEndpointMono.as(StepVerifier::create)
                .assertNext(actual -> {
                    assertThat(actual.getDescription()).isEqualTo("this is a health service endpoint");
                    assertThat(actual.getEndpoint()).isEqualTo("sonam.me/service/endpoint");
                    LOG.info("verify complete");
                })
                .verifyComplete();

        Flux<ServiceEndpoint> serviceEndpointMono1 = serviceEndpointRepository.findByServiceId(serviceId);

        serviceEndpointMono1.as(StepVerifier::create)
                .assertNext(actual ->
                {
                    serviceEndpointRepository.delete(actual).subscribe(service1 -> assertThat(service1).isNull());

                    LOG.info("deleted serviceEndpoint");
                    assertThat(serviceEndpointRepository.findById(actual.getId()).subscribe(service1
                            -> assertThat(service1).isNull()));
                    LOG.info("should return null after deleted");
                })
                .verifyComplete();
    }
}
