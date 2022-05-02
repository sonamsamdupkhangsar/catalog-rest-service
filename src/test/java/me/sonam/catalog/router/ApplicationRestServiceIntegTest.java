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

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@EnableAutoConfiguration
@ExtendWith(SpringExtension.class)
@SpringBootTest( webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ApplicationRestServiceIntegTest {
    private static final Logger LOG = LoggerFactory.getLogger(ApplicationRestServiceIntegTest.class);

    @Autowired
    private WebTestClient client;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private ClusterRepository clusterRepository;

    @Autowired
    private ServiceRepository serviceRepository;

    @Autowired
    private EnvironmentRepository environmentRepository;

    @Autowired
    private ApplicationEnvironmentRepository applicationEnvironmentRepository;

    @Autowired
    private ComponentRepository componentRepository;

    @Autowired
    private ConnectionRepository connectionRepository;

    @Test
    public void getApplications() {
        final String uuid = UUID.randomUUID().toString();
        LOG.info("check for uuid: {}", uuid);
        EntityExchangeResult<String> result = client.get().uri("/applications")
                .exchange().expectStatus().isOk().expectBody(String.class).returnResult();

        LOG.info("found page applications: {}", result.getResponseBody());
    }

    @Test
    public void updateApplication() {
        LOG.info("save application");
        Application application = new Application();
        application.setName("Closed Circuit Application");
        application.setDeprecated(false);
        application.setDescription("this is a closed circuit tv app");
        application.setDocumentationUrl("http://www.circuitdummy.co.ohl.com");
        application.setGitRepo("http://github.com/org/project");
        LOG.info("not setting id in rest integtest, it is handled in the business-service ApplicationService.java");

        EntityExchangeResult<Application> result = client.post().uri("/applications").bodyValue(application)
                .exchange().expectStatus().isOk()
                .expectBody(Application.class).returnResult();

        LOG.info("got saved application: {}", result.getResponseBody());

        applicationRepository.count().doOnNext(aLong -> LOG.info("found {} applications by repository", aLong)).subscribe();
        getApplications();
    }


    @Test
    public void getAllPlatforms()  {
        Cluster cluster = new Cluster(UUID.randomUUID(), "My Personal Cluster");
        clusterRepository.save(cluster).subscribe(cluster1 -> LOG.info("saved cluster: {}", cluster1));

        UUID id = UUID.randomUUID();
        LOG.info("get platforms");
        EntityExchangeResult<List<Cluster>> result = client.get().uri("/applications/platforms")
                .exchange().expectStatus().isOk().expectBodyList(Cluster.class)
                .returnResult();
        for(Cluster c: result.getResponseBody()) {
            LOG.info("cluster: {}", cluster);
        }
    }

    @Test
    public void getApplicationsByPlatformId()  {
        Cluster cluster = new Cluster(UUID.randomUUID(), "My Personal Cluster");
        clusterRepository.save(cluster).map(cluster1 -> {
                    LOG.info("save application");
                    Application application = new Application();
                    application.setName("Closed Circuit Application");
                    application.setDeprecated(false);
                    application.setDescription("this is a closed circuit tv app");
                    application.setDocumentationUrl("http://www.circuitdummy.co.ohl.com");
                    application.setGitRepo("http://github.com/org/project");
                    application.setIsNew(true);
                    application.setId(UUID.randomUUID());
                    application.setPlatformId(cluster1.getId());
                    applicationRepository.save(application).subscribe(application1 -> LOG.info("save application with cluster-id"));
                    return cluster1;
                }).subscribe(cluster1 -> LOG.info("saved cluster/app"));

        LOG.info("get cluster by id: {}", cluster.getId());

        EntityExchangeResult<String> result = client.get().uri("/applications/platform/" + cluster.getId())
                .exchange().expectStatus().isOk().expectBody(String.class).returnResult();//.isEqualTo(cluster);

        LOG.info("cluster found: {}", result.getResponseBody());
    }

    @Test
    public void getServicesByApplicationId() {
        UUID applicationId = UUID.randomUUID();

        Application application = new Application();
        application.setName("Closed Circuit Application");
        application.setDeprecated(false);
        application.setDescription("this is a closed circuit tv app");
        application.setDocumentationUrl("http://www.circuitdummy.co.ohl.com");
        application.setGitRepo("http://github.com/org/project");
        application.setIsNew(true);
        application.setId(UUID.randomUUID());

        applicationRepository.save(application).map(application1 -> {
            Service service = new Service("ping service", applicationId);
            service.setDescription("a pinging service");
            service.setEndpoint("http://localhost/health/liveness");
            service.setHealthEndpoint(false);
            service.setAccessTokenRequired(true);
            service.setPingIt(true);
            service.setRestMethod("mymethod");
            service.setIsNew(true);

            service.setApplicationId(application1.getId());

            serviceRepository.save(service).subscribe(service1 ->LOG.info("saved service with application-id: {}", service1));

            return application1;
        }).subscribe(application1 -> LOG.info("saved application"));

        LOG.info("getServicesByApplicationId: {}", application.getId());
        serviceRepository.count().subscribe(aLong -> LOG.info("service count: {}", aLong));

        EntityExchangeResult<String> result = client.get().uri("/applications/services/" + application.getId())
                .exchange().expectStatus().isOk().expectBody(String.class).returnResult();

        LOG.info("services found: {}", result.getResponseBody());
    }

    @Test
    public void getEnvironmentAssociationForApplicationId() {
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

        Cluster cluster = new Cluster(UUID.randomUUID(), "My Personal Cluster");
        Mono<Cluster> clusterMono = clusterRepository.save(cluster);

        Environment environment2 = new Environment();
        environment2.setSortOrder(2);
        environment2.setEnvironmentType("dev");
        environment2.setName("dev");
        environment2.setDomain("domain");
        environment2.setDeploymentLink("some deployment link");
        environment2.setIsNew(true);
        environment2.setClusterId(cluster.getId());
        environmentRepository.save(environment2).subscribe(env2 -> LOG.info("save env2"));

        LOG.info("save environment");
        Mono<Environment> environmentMono = environmentRepository.save(environment);

        applicationRepository.save(application).zipWith(environmentMono).zipWith(clusterMono).flatMap(objects -> {
                    Application app = objects.getT1().getT1();
                    Cluster clu = objects.getT2();
                    Environment env = objects.getT1().getT2();
                    env.setClusterId(clu.getId());
                    env.setIsNew(false);
                    environmentRepository.save(env).subscribe(environment1 -> LOG.info("updated environment cluster.id"));

                    app.setIsNew(false);
                    app.setPlatformId(clu.getId());
                    applicationRepository.save(app).subscribe(application1 -> LOG.info("update app with cluster.id"));

                    ApplicationEnvironment appEnv = new ApplicationEnvironment(app.getId(), env.getId());
                    applicationEnvironmentRepository.save(appEnv).subscribe(appEnv1 ->
                            LOG.info("saved applicationEnvironment"));
                    return Mono.just(appEnv);
                }).subscribe(applicationEnvironment -> LOG.info("applicationEnvironment created"));

        LOG.info("getEnvironmentAssociationForApplicationId: {}", application.getId());

        applicationEnvironmentRepository.count().subscribe(aLong -> LOG.info("there are {} appenv", aLong));
        applicationEnvironmentRepository.findAll().subscribe(applicationEnvironment -> LOG.info("found appEnv: {}", applicationEnvironment));

        EntityExchangeResult<EnvironmentAssociation[]> result = client.get().uri("/applications/"+application.getId()+"/environments")
                .exchange().expectStatus().isOk().expectBody(EnvironmentAssociation[].class).returnResult();

        LOG.info("EnvironmentAssociationForApplicationId found: {}", result.getResponseBody());

        for(EnvironmentAssociation environmentAssociation1: result.getResponseBody()) {
            LOG.info("environmentAssociated : {}", environmentAssociation1);
        }
    }

    @Test
    public void getUnassociatedClustersForApplicationId() {
        LOG.info("getUnassociatedClustersForApplicationId");

        Application application = new Application();
        application.setName("Closed Circuit Application");
        application.setDeprecated(false);
        application.setDescription("this is a closed circuit tv app");
        application.setDocumentationUrl("http://www.circuitdummy.co.ohl.com");
        application.setGitRepo("http://github.com/org/project");
        application.setIsNew(true);
        application.setId(UUID.randomUUID());

        Environment devEnvrionment = new Environment();
        devEnvrionment.setSortOrder(1);
        devEnvrionment.setEnvironmentType("dev");
        devEnvrionment.setName("dev");
        devEnvrionment.setDomain("domain");
        devEnvrionment.setDeploymentLink("some deployment link");
        devEnvrionment.setIsNew(true);

        Environment testEnvironment = new Environment();
        testEnvironment.setSortOrder(1);
        testEnvironment.setEnvironmentType("test");
        testEnvironment.setName("test");
        testEnvironment.setDomain("domain");
        testEnvironment.setDeploymentLink("some deployment link");
        testEnvironment.setIsNew(true);

        Cluster team1Cluster = new Cluster(UUID.randomUUID(), "Team1 Cluster");
        Mono<Cluster> team1ClusterMono = clusterRepository.save(team1Cluster);

        Cluster team2Cluster = new Cluster(UUID.randomUUID(), "Team2 Cluster");
        Mono<Cluster> team2ClusterMono = clusterRepository.save(team2Cluster);

        Cluster team3Cluster = new Cluster(UUID.randomUUID(), "Team3 Cluster");
        Mono<Cluster> team3ClusterMono = clusterRepository.save(team3Cluster);

        team1ClusterMono.zipWith(team2ClusterMono).zipWith(team3ClusterMono).doOnNext(objects -> {
           Cluster team1 = objects.getT1().getT1();
           Cluster team2 = objects.getT1().getT2();
           Cluster team3 = objects.getT2();

           LOG.info("saved team1, team2, team3 clusters");

           application.setPlatformId(team3.getId());
           applicationRepository.save(application).subscribe(app1 -> LOG.info("saved application to team 3 cluster"));

            devEnvrionment.setClusterId(team3.getId());
            environmentRepository.save(devEnvrionment).subscribe(environment1 -> LOG.info("saved environment with team2 cluster.id"));

            testEnvironment.setClusterId(team3.getId());
            environmentRepository.save(testEnvironment).subscribe(environment1 -> LOG.info("saved test environment with team2 cluster.id"));

        }).subscribe(objects -> LOG.info("Clusters and app-cluster association should be created"));

        LOG.info("get un associated clusters with app");
        EntityExchangeResult<Cluster[]> result = client.get().uri("/applications/"+application.getId()+"/clusters")
                .exchange().expectStatus().isOk().expectBody(Cluster[].class).returnResult();

        LOG.info("Clusters found: {}", result.getResponseBody());

        for(Cluster cluster: result.getResponseBody()) {
            LOG.info("cluster : {}", cluster);
        }
        assertThat(result.getResponseBody().length).isGreaterThan(2);
        //get Cluster with associated environments
        EntityExchangeResult<Cluster> clusterResult = client.get().uri("/applications/"+application.getId()+"/cluster")
                .exchange().expectStatus().isOk().expectBody(Cluster.class).returnResult();

        LOG.info("Clusters found: {}", clusterResult.getResponseBody());
        assertThat(clusterResult.getResponseBody()).isNotNull();
        assertThat(clusterResult.getResponseBody().getId()).isNotNull();

        LOG.info("dev and test environments should be in list");
        assertThat(clusterResult.getResponseBody().getEnvironmentList().size()).isEqualTo(2);

        for(Environment environment: clusterResult.getResponseBody().getEnvironmentList()) {
            LOG.info("found environment: {}", environment);
        }
    }

    @Test
    public void associateEnvironment() {
        LOG.info("associate application with envrionment");

        Application application = new Application();
        application.setName("Closed Circuit Application");
        application.setDeprecated(false);
        application.setDescription("this is a closed circuit tv app");
        application.setDocumentationUrl("http://www.circuitdummy.co.ohl.com");
        application.setGitRepo("http://github.com/org/project");
        application.setIsNew(true);
        application.setId(UUID.randomUUID());

        Environment devEnvrionment = new Environment();
        devEnvrionment.setSortOrder(1);
        devEnvrionment.setEnvironmentType("dev");
        devEnvrionment.setName("dev");
        devEnvrionment.setDomain("domain");
        devEnvrionment.setDeploymentLink("some deployment link");
        devEnvrionment.setIsNew(true);

        Environment testEnvironment = new Environment();
        testEnvironment.setSortOrder(1);
        testEnvironment.setEnvironmentType("test");
        testEnvironment.setName("test");
        testEnvironment.setDomain("domain");
        testEnvironment.setDeploymentLink("some deployment link");
        testEnvironment.setIsNew(true);

        Environment stageEnvironment = new Environment();
        stageEnvironment.setSortOrder(2);
        stageEnvironment.setEnvironmentType("stage");
        stageEnvironment.setName("stage");
        stageEnvironment.setDomain("domain");
        stageEnvironment.setDeploymentLink("some deployment link");
        stageEnvironment.setIsNew(true);

        Cluster team3Cluster = new Cluster(UUID.randomUUID(), "Team3 Cluster");
        clusterRepository.save(team3Cluster).subscribe(cluster -> application.setPlatformId(cluster.getId()));
        devEnvrionment.setClusterId(team3Cluster.getId());
        testEnvironment.setClusterId(team3Cluster.getId());
        stageEnvironment.setClusterId(team3Cluster.getId());

        EnvironmentAssociation[] environmentAssociations = new EnvironmentAssociation[3];

        Mono<Application> applicationMono = applicationRepository.save(application);
        applicationMono.doOnNext(application1 -> {
            environmentRepository.save(devEnvrionment).subscribe(environment -> {
                LOG.info("saved dev environment and associate with application");
               // ApplicationEnvironment applicationEnvironment = new ApplicationEnvironment(application1.getId(), environment.getId());
               // applicationEnvironmentRepository.save(applicationEnvironment).subscribe(applicationEnvironment1 -> LOG.info("saved applicationenvironment"));
                environmentAssociations[0] = new EnvironmentAssociation(true, environment.getId(), environment.getName());
            });
            environmentRepository.save(stageEnvironment).subscribe(environment -> {
                LOG.info("saved stage environment and associate with application");
                ApplicationEnvironment applicationEnvironment = new ApplicationEnvironment(application1.getId(), environment.getId());
                applicationEnvironmentRepository.save(applicationEnvironment).subscribe(applicationEnvironment1 -> LOG.info("saved applicationenvironment"));
                environmentAssociations[1] = new EnvironmentAssociation(true, environment.getId(), environment.getName());
            });
            //create testEnvrionment as NOT associated
            environmentAssociations[2] = new EnvironmentAssociation(false, testEnvironment.getId(), testEnvironment.getName());

            environmentRepository.save(testEnvironment).subscribe(environment -> LOG.info("saved test environment"));

        }).subscribe();

        assertThat(environmentAssociations.length).isEqualTo(3);

        List<EnvironmentAssociation> list = Arrays.asList(environmentAssociations);
        Mono<List<EnvironmentAssociation>> monoList = Mono.just(list);

        LOG.info("associateEnvironment");
        client.post();
        EntityExchangeResult<EnvironmentAssociation[]> result = client.post().uri("/applications/"+application.getId()+
                "/environments/update").body(monoList, EnvironmentAssociation.class)
                .exchange().expectStatus().isOk().expectBody(EnvironmentAssociation[].class).returnResult();

        LOG.info("environmentAssociations found: {}", result.getResponseBody());

        boolean devFound = false;
        boolean stageFound = false;

        for(EnvironmentAssociation ea: result.getResponseBody()) {
            LOG.info("cluster : {}", ea);
            if (ea.isAssociated()) {
                LOG.info("check associated matches dev and test");
                if (ea.getEnvironmentId().equals(devEnvrionment.getId())) {
                    devFound = true;
                }
                if (ea.getEnvironmentId().equals(stageEnvironment.getId())) {
                    stageFound = true;
                }
            }
        }
        assertThat(devFound).isTrue();
        assertThat(stageFound).isTrue();

        assertThat(result.getResponseBody().length).isGreaterThan(2);
        Application application2 = new Application();
        application2.setName("Closed Circuit Application");
        application2.setDeprecated(false);
        application2.setDescription("this is a closed circuit tv app");
        application2.setDocumentationUrl("http://www.circuitdummy.co.ohl.com");
        application2.setGitRepo("http://github.com/org/project");
        application2.setIsNew(true);
        application2.setId(UUID.randomUUID());

        Cluster team4Cluster = new Cluster(UUID.randomUUID(), "Team4 Cluster");
        clusterRepository.save(team4Cluster).subscribe(cluster ->{
            application2.setPlatformId(cluster.getId());

            applicationRepository.save(application2).subscribe(application1 -> LOG.info("saved app"));

            Environment team4Environment = new Environment();
            team4Environment.setSortOrder(1);
            team4Environment.setEnvironmentType("test");
            team4Environment.setName("team4environment");
            team4Environment.setDomain("domain");
            team4Environment.setDeploymentLink("some deployment link");
            team4Environment.setIsNew(true);
            team4Environment.setClusterId(cluster.getId());

            environmentRepository.save(team4Environment).subscribe(environment -> {LOG.info("saved team envrionment with  team4 cluster");
                ApplicationEnvironment applicationEnvironment = new ApplicationEnvironment(application2.getId(), environment.getId());
                applicationEnvironmentRepository.save(applicationEnvironment).subscribe(applicationEnvironment1 -> LOG.info("saved appenv"));
            });
        });



        LOG.info("associateCluster");
        client.post();
        EntityExchangeResult<EnvironmentAssociation[]> associateClusterResult = client.post().uri("/applications/"+application2.getId()+
                "/cluster/update/"+team4Cluster.getId()).body(monoList, EnvironmentAssociation.class)
                .exchange().expectStatus().isOk().expectBody(EnvironmentAssociation[].class).returnResult();

        assertThat(associateClusterResult.getResponseBody().length).isEqualTo(1);
        assertThat(associateClusterResult.getResponseBody()[0].isAssociated()).isTrue();

    }

    @Test
    public void associateCluster() {
        Application application2 = new Application();
        application2.setName("Closed Circuit Application");
        application2.setDeprecated(false);
        application2.setDescription("this is a closed circuit tv app");
        application2.setDocumentationUrl("http://www.circuitdummy.co.ohl.com");
        application2.setGitRepo("http://github.com/org/project");
        application2.setIsNew(true);
        application2.setId(UUID.randomUUID());

        applicationRepository.save(application2).subscribe(application -> LOG.info("application saved"));

        Cluster cluster = new Cluster(UUID.randomUUID(), "Team4 Cluster");
        cluster.setId(UUID.randomUUID());
        cluster.setIsNew(true);

        clusterRepository.save(cluster).subscribe(cluster1 ->
            LOG.info("cluster saved in repo"));

        LOG.info("associateCluster");

        EntityExchangeResult<EnvironmentAssociation[]> associateClusterResult = client.post().uri("/applications/"+application2.getId()+
                "/cluster/update/"+cluster.getId())
                .exchange().expectStatus().isOk().expectBody(EnvironmentAssociation[].class).returnResult();

    }

    @Test
    public void connect() {
        Application application = new Application();
        application.setName("Closed Circuit Application");
        application.setDeprecated(false);
        application.setDescription("this is a closed circuit tv app");
        application.setDocumentationUrl("http://www.circuitdummy.co.ohl.com");
        application.setGitRepo("http://github.com/org/project");
        application.setIsNew(true);
        application.setId(UUID.randomUUID());

        Mono<Application> applicationMono = applicationRepository.save(application);
        Component component = new Component("mykafka-queue", null);
        component.setIsNew(true);
        component.setId(UUID.randomUUID());
        Mono<Component> componentMono = componentRepository.save(component);

        Component dbComponent = new Component("myCatalogDatabase", null);
        dbComponent.setIsNew(true);
        dbComponent.setId(UUID.randomUUID());
        Mono<Component> dbComponentMono = componentRepository.save(dbComponent);

        applicationMono.zipWith(componentMono).zipWith(dbComponentMono).subscribe(objects -> {
           LOG.info("saved application, component and dbComponent");
        });

        LOG.info("connect app {} with component {}", application.getId(), component.getId());

        ConnectionForm connectionForm = new ConnectionForm();
        connectionForm.setAppId(application.getId());
        connectionForm.setConnecting(Connection.CONNECTING.COMPONENT.name());
        LOG.info("set connection from app to queue and db components");
        connectionForm.setTargetIdList(Arrays.asList(component.getId(), dbComponent.getId()));

        client.post().uri("/applications/connection").bodyValue(connectionForm)
                .exchange().expectStatus().isOk().expectBody(String.class).isEqualTo("connection updated");

        LOG.info("checking if connection is found");
        connectionRepository.findByAppIdSourceAndConnecting(application.getId(), Connection.CONNECTING.COMPONENT.name()).subscribe(
                connection -> LOG.info("after saving found connection: {}", connection));


        connectionRepository.findAll().subscribe(connection -> LOG.info("found connection in all: {}", connection));

        EntityExchangeResult<Component[]> result = client.get().uri("/applications/"+application.getId()+"/connection/component")
                .exchange().expectStatus().isOk().expectBody(Component[].class).returnResult();

        LOG.info("got app connected components: {}", result.getResponseBody());
        Component[] components = result.getResponseBody();
        LOG.info("components found: {}", components.length);
        for(Component component1: components) {
            LOG.info("component: {}", component1);
        }
        assertThat(components.length).isEqualTo(2);
    }

}
