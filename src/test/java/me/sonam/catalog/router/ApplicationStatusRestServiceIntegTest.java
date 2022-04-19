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

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@EnableAutoConfiguration
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ApplicationStatusRestServiceIntegTest {
    private static final Logger LOG = LoggerFactory.getLogger(ApplicationStatusRestServiceIntegTest.class);

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private EnvironmentRepository environmentRepository;

    @Autowired
    private ApplicationEnvironmentRepository applicationEnvironmentRepository;

    @Autowired
    private ServiceRepository serviceRepository;

    @Autowired
    private ApplicationServiceStatusRepository applicationServiceStatusRepository;

    @Autowired
    private ApplicationStatusRepository applicationStatusRepository;

    @Test
    public void getPage() {
        LOG.info("get Page of appstatus");
        Application application = new Application();
        application.setName("Closed Circuit Application");
        application.setDeprecated(false);
        application.setDescription("this is a closed circuit tv app");
        application.setDocumentationUrl("http://www.circuitdummy.co.ohl.com");
        application.setGitRepo("http://github.com/org/project");
        application.setIsNew(true);
        application.setId(UUID.randomUUID());

        Environment environment = new Environment();
        environment.setSortOrder(1);
        environment.setEnvironmentType("test");
        environment.setName("dev");
        environment.setDomain("domain");
        environment.setDeploymentLink("some deployment link");
        environment.setIsNew(true);

        Mono<Environment> environmentMono = environmentRepository.save(environment);
        Mono<Application> applicationMono = applicationRepository.save(application);

        applicationMono.zipWith(environmentMono).doOnNext(objects -> {
            Application application1 = objects.getT1();
            Environment environment1 = objects.getT2();

            LOG.info("saved application and environment, now create association using repo");
            ApplicationEnvironment applicationEnvironment = new ApplicationEnvironment(application1.getId(),
                    environment1.getId());
            applicationEnvironmentRepository.save(applicationEnvironment).subscribe(applicationEnvironment1 ->
                    LOG.info("saved applicationEnvrionment"));

            Service service = new Service("ping service", application1.getId());
            service.setDescription("a pinging service");
            service.setEndpoint("http://localhost/health/liveness");
            service.setHealthEndpoint(false);
            service.setAccessTokenRequired(true);
            service.setPingIt(true);
            service.setRestMethod("mymethod");
            service.setIsNew(true);

            service.setApplicationId(application1.getId());

            serviceRepository.save(service).subscribe(service1 ->LOG.info("saved service with application-id: {}", service1));

            Service service2 = new Service("ping service", application1.getId());
            service2.setDescription("a pinging service");
            service2.setEndpoint("http://localhost/health/liveness");
            service2.setHealthEndpoint(false);
            service2.setAccessTokenRequired(true);
            service2.setPingIt(true);
            service2.setRestMethod("mymethod");
            service2.setIsNew(true);

            service2.setApplicationId(application1.getId());

            serviceRepository.save(service2).subscribe(service1 ->LOG.info("saved service2 with application-id: {}", service1));


            ApplicationServiceStatus applicationServiceStatus = new ApplicationServiceStatus(application1.getId(),
                    service.getId(), environment1.getId(), 200, LocalDateTime.now(), service.getEndpoint());

            applicationServiceStatusRepository.save(applicationServiceStatus).subscribe(applicationServiceStatus1 -> LOG.info("saved" +
                    "applicationServiceStatus"));

            ApplicationServiceStatus applicationServiceStatus2 = new ApplicationServiceStatus(application1.getId(),
                    service2.getId(), environment1.getId(), 200, LocalDateTime.now(), service.getEndpoint());

            applicationServiceStatusRepository.save(applicationServiceStatus2).subscribe(applicationServiceStatus1 -> LOG.info("saved " +
                    "applicationServiceStatus2"));


        }).subscribe(objects -> LOG.info("should subscribe to save"));

        List<ApplicationStatus> applicationStatusList = new ArrayList<>();

        for(int i = 0; i < 100; i++) {
            ApplicationStatus applicationStatus = new ApplicationStatus();
            applicationStatus.setIsNew(true);
            applicationStatus.setApplicationId(UUID.randomUUID());
            applicationStatus.setApplicationName("dummy app");
            applicationStatus.setProdEnvironmentId(UUID.randomUUID());
            applicationStatus.setStageEnvironmentId(UUID.randomUUID());
            applicationStatus.setPlatform("some platform");
            applicationStatus.setProdStatus("good");
            applicationStatus.setDevStatus("good");
            applicationStatus.setProdEnvStatus(new EnvironmentStatus());

            applicationStatusList.add(applicationStatus);

            applicationStatusRepository.save(applicationStatus).subscribe(applicationStatus1 -> LOG.info("saved application status"));
        }

        LOG.info("get Page of appstatus");
        EntityExchangeResult<String> result = webTestClient.get().uri("/appstatus")
                .exchange().expectStatus().isOk().expectBody(String.class).returnResult();

        LOG.info("found page of appstatus: {}", result.getResponseBody());
    }

    @Test
    public void getAppByEnv() {
        LOG.info("get app by env");

        Application application = new Application();
        application.setName("Closed Circuit Application");
        application.setDeprecated(false);
        application.setDescription("this is a closed circuit tv app");
        application.setDocumentationUrl("http://www.circuitdummy.co.ohl.com");
        application.setGitRepo("http://github.com/org/project");
        application.setIsNew(true);
        application.setId(UUID.randomUUID());

        Environment environment = new Environment();
        environment.setSortOrder(1);
        environment.setEnvironmentType("test");
        environment.setName("dev");
        environment.setDomain("domain");
        environment.setDeploymentLink("some deployment link");
        environment.setIsNew(true);

        Mono<Environment> environmentMono = environmentRepository.save(environment);
        Mono<Application> applicationMono = applicationRepository.save(application);

        applicationMono.zipWith(environmentMono).doOnNext(objects -> {
           Application application1 = objects.getT1();
           Environment environment1 = objects.getT2();

           LOG.info("saved application and environment, now create association using repo");
            ApplicationEnvironment applicationEnvironment = new ApplicationEnvironment(application1.getId(),
                    environment1.getId());
            applicationEnvironmentRepository.save(applicationEnvironment).subscribe(applicationEnvironment1 ->
                    LOG.info("saved applicationEnvrionment"));

            Service service = new Service("ping service", application1.getId());
            service.setDescription("a pinging service");
            service.setEndpoint("http://localhost/health/liveness");
            service.setHealthEndpoint(false);
            service.setAccessTokenRequired(true);
            service.setPingIt(true);
            service.setRestMethod("mymethod");
            service.setIsNew(true);

            service.setApplicationId(application1.getId());

            serviceRepository.save(service).subscribe(service1 ->LOG.info("saved service with application-id: {}", service1));

            Service service2 = new Service("ping service", application1.getId());
            service2.setDescription("a pinging service");
            service2.setEndpoint("http://localhost/health/liveness");
            service2.setHealthEndpoint(false);
            service2.setAccessTokenRequired(true);
            service2.setPingIt(true);
            service2.setRestMethod("mymethod");
            service2.setIsNew(true);

            service2.setApplicationId(application1.getId());

            serviceRepository.save(service2).subscribe(service1 ->LOG.info("saved service2 with application-id: {}", service1));


            ApplicationServiceStatus applicationServiceStatus = new ApplicationServiceStatus(application1.getId(),
                    service.getId(), environment1.getId(), 200, LocalDateTime.now(), service.getEndpoint());

            applicationServiceStatusRepository.save(applicationServiceStatus).subscribe(applicationServiceStatus1 -> LOG.info("saved" +
                    "applicationServiceStatus"));

            ApplicationServiceStatus applicationServiceStatus2 = new ApplicationServiceStatus(application1.getId(),
                    service2.getId(), environment1.getId(), 200, LocalDateTime.now(), service.getEndpoint());

            applicationServiceStatusRepository.save(applicationServiceStatus2).subscribe(applicationServiceStatus1 -> LOG.info("saved" +
                    "applicationServiceStatus2"));


        }).subscribe(objects -> LOG.info("should subscribe to save"));

        LOG.info("get Page of appstatus");
        EntityExchangeResult<String> result = webTestClient.get().uri("/appstatus")
                .exchange().expectStatus().isOk().expectBody(String.class).returnResult();

        LOG.info("found page of appstatus: {}", result.getResponseBody());


        EntityExchangeResult<ApplicationServiceStatus[]> resultPage = webTestClient.get().uri("/appstatus/app/"+application.getId()+
                "/env/"+environment.getId())
                .exchange().expectStatus().isOk().expectBody(ApplicationServiceStatus[].class).returnResult();

        assertThat(resultPage.getResponseBody().length).isEqualTo(2);
        for(ApplicationServiceStatus applicationServiceStatus: resultPage.getResponseBody()) {
            assertThat(applicationServiceStatus.getId()).isNotNull();
            assertThat(applicationServiceStatus.getApplicationId()).isNotNull();
            assertThat(applicationServiceStatus.getApplicationId()).isEqualTo(application.getId());

            LOG.info("found applicationServiceStatus: {}", applicationServiceStatus);
        }
        LOG.info("found page applicationServiceStatus: {}", result.getResponseBody());

    }
}
