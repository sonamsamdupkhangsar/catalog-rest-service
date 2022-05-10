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

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@EnableAutoConfiguration
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ServiceRestServiceIntegTest {
    private static final Logger LOG = LoggerFactory.getLogger(ServiceRestServiceIntegTest.class);

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private ServiceRepository serviceRepository;

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private EnvironmentRepository environmentRepository;

    @Autowired
    private ApplicationEnvironmentRepository applicationEnvironmentRepository;

    @Autowired
    private ClusterRepository clusterRepository;

    @Test
    public void getPage() {
        Application application = new Application();
        application.setName("Closed Circuit Application");
        application.setDeprecated(false);
        application.setDescription("this is a closed circuit tv app");
        application.setDocumentationUrl("http://www.circuitdummy.co.ohl.com");
        application.setGitRepo("http://github.com/org/project");
        application.setIsNew(true);
        application.setId(UUID.randomUUID());

        Cluster cluster = new Cluster(UUID.randomUUID(), "My platform cluster");
        clusterRepository.save(cluster).subscribe(cluster1 -> LOG.info("saved cluster"));

        application.setPlatformId(cluster.getId());
        applicationRepository.save(application).subscribe(application1 -> LOG.info("saved app"));


        Environment environment = new Environment();
        environment.setSortOrder(1);
        environment.setEnvironmentType("test");
        environment.setName("dev");
        environment.setDomain("domain");
        environment.setDeploymentLink("some deployment link");
        environment.setIsNew(true);

        environmentRepository.save(environment).subscribe(environment1 -> LOG.info("saved environment"));

        Service service = new Service("ping service", application.getId());
        service.setDescription("a pinging service");
        service.setEndpoint("http://localhost/health/liveness");
        service.setHealthEndpoint(false);
        service.setAccessTokenRequired(true);
        service.setPingIt(true);
        service.setRestMethod("mymethod");
        service.setIsNew(true);



        serviceRepository.save(service).subscribe(service1 ->LOG.info("saved service with application-id: {}", service1));

        Service service2 = new Service("ping service", application.getId());
        service2.setDescription("a pinging service");
        service2.setEndpoint("http://localhost/health/liveness");
        service2.setHealthEndpoint(false);
        service2.setAccessTokenRequired(true);
        service2.setPingIt(true);
        service2.setRestMethod("mymethod");
        service2.setIsNew(true);

        serviceRepository.save(service2).subscribe(service1 ->LOG.info("saved service2 with application-id: {}", service1));

        ApplicationEnvironment applicationEnvironment = new ApplicationEnvironment(application.getId(), environment.getId());
        applicationEnvironmentRepository.save(applicationEnvironment).
                subscribe(applicationEnvironment1 -> LOG.info("associated application and environment"));

        LOG.info("get Page of services");
        EntityExchangeResult<String> result = webTestClient.get().uri("/services")
                .exchange().expectStatus().isOk().expectBody(String.class).returnResult();

        LOG.info("found page of service: {}", result.getResponseBody());
    }

    @Test
    public void update() {
        Service service = new Service("Mysql database", null);
        serviceRepository.save(service).subscribe(service1 -> LOG.info("saved service"));

        service.setName("new name");
        EntityExchangeResult<Service> result = webTestClient.post().uri("/services").bodyValue(service)
                .exchange().expectStatus().isOk().expectBody(Service.class).returnResult();

        LOG.info("service: {}", result.getResponseBody());
        assertThat(result.getResponseBody().getName()).isEqualTo("new name");
    }

    @Test
    public void delete() {
        LOG.info("delete service");
        Service service = new Service("Mysql database", null);
        serviceRepository.save(service).subscribe(service1 -> LOG.info("saved service"));

        EntityExchangeResult<Service> result = webTestClient.delete().uri("/services/"+service.getId())
                .exchange().expectStatus().isOk().expectBody(Service.class).returnResult();

        LOG.info("response for delete service: {}", result.getResponseBody());
        assertThat(result.getResponseBody()).isNull();
    }
}
