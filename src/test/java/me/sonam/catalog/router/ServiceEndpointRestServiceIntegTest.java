package me.sonam.catalog.router;

import me.sonam.catalog.repo.*;
import me.sonam.catalog.repo.entity.*;
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
import reactor.core.publisher.Mono;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@EnableAutoConfiguration
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ServiceEndpointRestServiceIntegTest {
    private static final Logger LOG = LoggerFactory.getLogger(ServiceEndpointRestServiceIntegTest.class);

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private ServiceEndpointRepository serviceEndpointRepository;


    @Test
    public void getByServiceId() {
        UUID serviceId = UUID.randomUUID();

        for(int i = 0; i < 10; i++) {
            ServiceEndpoint serviceEndpoint = new ServiceEndpoint();
            serviceEndpoint.setIsNew(true);
            serviceEndpoint.setId(UUID.randomUUID());
            serviceEndpoint.setServiceId(serviceId);
            serviceEndpoint.setEndpoint("sonam.me/service/endpoint");
            serviceEndpoint.setDescription("this is a health service endpoint");
            serviceEndpoint.setRestMethod(ServiceEndpoint.REST_METHOD.GET.name());

           serviceEndpointRepository.save(serviceEndpoint).subscribe(serviceEndpoint1 -> LOG.info("saved serviceEndpoint"));
        }

        LOG.info("get Page of serviceEndoints");
        EntityExchangeResult<ServiceEndpoint[]> result = webTestClient.get().uri("/serviceendpoint/"+ serviceId)
                .exchange().expectStatus().isOk().expectBody(ServiceEndpoint[].class).returnResult();


        ServiceEndpoint[] serviceEndpoints = result.getResponseBody();
        assertThat(serviceEndpoints.length).isEqualTo(10);
        LOG.info("found {} serviceEndpoints: {} by serviceId {}", serviceEndpoints.length, serviceEndpoints);
        for(ServiceEndpoint serviceEndpoint: serviceEndpoints) {
            assertThat(serviceEndpoint.getServiceId()).isEqualTo(serviceId);
            LOG.info("assert serviceId is same for all");
        }
    }

    @Test
    public void update() {
        ServiceEndpoint serviceEndpoint = new ServiceEndpoint();
        serviceEndpoint.setIsNew(true);
        UUID serviceId = UUID.randomUUID();
        serviceEndpoint.setName("old name");
        serviceEndpoint.setServiceId(serviceId);
        serviceEndpoint.setEndpoint("sonam.me/service/endpoint");
        serviceEndpoint.setDescription("this is a health service endpoint");
        serviceEndpoint.setRestMethod(ServiceEndpoint.REST_METHOD.GET.name());

       // serviceEndpointRepository.save(serviceEndpoint).subscribe(serviceEndpoint1 -> LOG.info("saved serviceEndpoint"));
        LOG.info("save a new serviceEndpoint first");
        EntityExchangeResult<ServiceEndpoint> result = webTestClient.post().uri("/serviceendpoint").bodyValue(serviceEndpoint)
                .exchange().expectStatus().isOk().expectBody(ServiceEndpoint.class).returnResult();

        ServiceEndpoint saved = result.getResponseBody();
        assertThat(saved.getName()).isEqualTo("old name");

        LOG.info("change 'old name' to 'new name'");
        serviceEndpoint.setName("new name");
        result = webTestClient.post().uri("/serviceendpoint").bodyValue(serviceEndpoint)
                .exchange().expectStatus().isOk().expectBody(ServiceEndpoint.class).returnResult();

        saved = result.getResponseBody();
        assertThat(saved.getName()).isEqualTo("new name");
    }

    @Test
    public void delete() {
        LOG.info("delete service");
        ServiceEndpoint serviceEndpoint = new ServiceEndpoint();
        serviceEndpoint.setIsNew(true);
        serviceEndpoint.setId(UUID.randomUUID());
        UUID serviceId = UUID.randomUUID();
        serviceEndpoint.setName("old name");
        serviceEndpoint.setServiceId(serviceId);
        serviceEndpoint.setEndpoint("sonam.me/service/endpoint");
        serviceEndpoint.setDescription("this is a health service endpoint");
        serviceEndpoint.setRestMethod(ServiceEndpoint.REST_METHOD.GET.name());
        serviceEndpointRepository.save(serviceEndpoint).subscribe(serviceEndpoint1 -> LOG.info("saved serviceEndpoint"));

        EntityExchangeResult<ServiceEndpoint> result = webTestClient.delete().uri("/serviceendpoint/"+serviceEndpoint.getId())
                .exchange().expectStatus().isOk().expectBody(ServiceEndpoint.class).returnResult();

        LOG.info("response for delete serviceEndpoint: {}", result.getResponseBody());
        assertThat(result.getResponseBody()).isNull();

        LOG.info("get services endpoint by serviceId and verify none returned");
        EntityExchangeResult<ServiceEndpoint[]> serviceEndpointResult = webTestClient.get().uri("/serviceendpoint/"+ serviceId)
                .exchange().expectStatus().isOk().expectBody(ServiceEndpoint[].class).returnResult();


        ServiceEndpoint[] serviceEndpoints = serviceEndpointResult.getResponseBody();
        assertThat(serviceEndpoints.length).isEqualTo(0);
    }
}
