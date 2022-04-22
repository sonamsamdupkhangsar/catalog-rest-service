package me.sonam.catalog.application;


import me.sonam.catalog.repo.*;
import me.sonam.catalog.repo.entity.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@org.springframework.stereotype.Service
public class ApplicationService implements ApplicationBehavior {
    private static final Logger LOG = LoggerFactory.getLogger(ApplicationService.class);

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private ClusterRepository clusterRepository;

    @Autowired
    private ServiceRepository serviceRepository;

    @Autowired
    private ApplicationEnvironmentRepository applicationEnvironmentRepository;

    @Autowired
    private EnvironmentRepository environmentRepository;

    @Autowired
    private ConnectionRepository connectionRepository;

    @Autowired
    private ComponentRepository componentRepository;

    @Override
    public Mono<Page<Application>> getApplications(Pageable pageable) {
        return applicationRepository.findAllBy(pageable).collectList()
                .zipWith(this.applicationRepository.count())
                .map(t -> new PageImpl<>(t.getT1(), pageable, t.getT2()));
    }

    @Override
    public Flux<Cluster> getPlatforms() {
        return clusterRepository.findAll();
    }

    @Override
    public Mono<Page<Application>> getApplicationsByPlatform(UUID platformId, Pageable pageable) {
        return applicationRepository.findByPlatformId(platformId, pageable)
                .collectList().zipWith(applicationRepository.countByPlatformId(platformId))
                .map(t -> new PageImpl<>(t.getT1(), pageable, t.getT2()));
    }

    @Override
    public Mono<Page<Service>> getServicesByApplicationId(UUID applicationId, Pageable pageable) {
        return serviceRepository.findByApplicationId(applicationId, pageable)
                .collectList().zipWith(serviceRepository.countByApplicationId(applicationId))
                .map(t -> new PageImpl<>(t.getT1(), pageable, t.getT2()));
    }

    @Override
    public Mono<Application> updateApplication(Application application) {
        LOG.info("update application: {}", application);
        if (application.getId() == null) {
            application.setId(UUID.randomUUID());
            application.setIsNew(true);
            LOG.info("set isNew to true and set Id: {}", application);
        }
        Mono<Application> mono = applicationRepository.save(application);
        mono.subscribe(application1 -> LOG.info("app: {}", application1));
        applicationRepository.count().subscribe(aLong -> LOG.info("checking after saving count: {}", aLong));
        return mono;
    }

    /**
     * Pseudocode: Get all environments with their association status
     * 1. If application is not associated to any environment then return all envrionments as not associated
     * 2. If application is associated to an environment then indicate which envrionment it is associated with.
     * 2.a. if
     * @param applicationId
     * @return
     */
    @Override
    public Flux<EnvironmentAssociation> getEnvironmentAssociationForApplicationId(UUID applicationId) {
        Mono<Application> applicationMono = applicationRepository.findById(applicationId);


        Mono<Cluster> clusterMono = applicationMono.flatMap(application -> {
            if (application.getPlatformId() != null) {
                return clusterRepository.findById(application.getPlatformId());
            }
            else {
                return Mono.just(new Cluster());
            }
        });


        Flux<Environment> environmentFlux = clusterMono.flatMapMany(cluster ->
        {
            if (cluster.getId() == null) {
                return environmentRepository.findAll();
            }
            else {
                return environmentRepository.findByClusterIdOrderBySortOrderAsc(cluster.getId());
            }
        });

        Mono<List<Environment>> monoEnvList = environmentFlux.collectList().flatMap(environments -> {
            List<Environment> envList = new ArrayList<>();
           for(Environment e : environments) {
               envList.add(e);
           }

           return Mono.just(envList);
        });

         Flux<ApplicationEnvironment> applicationEnvironmentFlux = applicationMono.flatMapMany(application ->
                applicationEnvironmentRepository.findByApplicationId(application.getId()));

        Flux<EnvironmentAssociation> environmentAssociationFlux = applicationEnvironmentFlux.hasElements().flatMapMany(aBoolean -> {
            LOG.info("aBoolean: {}", aBoolean);
            if(!aBoolean) {
                List<EnvironmentAssociation> list = new ArrayList<>();
                Flux<EnvironmentAssociation> environmentAssociationFlux1 = monoEnvList.flatMapMany(environmentList -> {
                    for (Environment env : environmentList) {
                        EnvironmentAssociation environmentAssociation =
                                new EnvironmentAssociation(false, env.getId(), env.getName());
                        list.add(environmentAssociation);
                        LOG.info("added environmentassociation to list: {}", environmentAssociation);
                    }
                    LOG.info("returning list: {}", list);
                    return Flux.fromIterable(list);
                });
                //environmentAssociationFlux1.subscribe(environmentAssociation -> LOG.info("cause to emit events"));
                return environmentAssociationFlux1;
             }
            else {
                Mono<List> appEnvList = applicationEnvironmentFlux.collectList().flatMap(applicationEnvironments -> {
                    List<UUID> uuidList = new ArrayList<>();
                    for(ApplicationEnvironment applicationEnvironment: applicationEnvironments) {
                        uuidList.add(applicationEnvironment.getEnvironmentId());
                        LOG.info("add envId: {}", applicationEnvironment.getEnvironmentId() );
                    }
                    LOG.info("uudiList: {}", uuidList);
                    return Mono.just(uuidList);
                });

               Flux<EnvironmentAssociation> environmentAssociationFlux1 = monoEnvList.zipWith(appEnvList).flatMapMany(objects -> {
                    List<Environment> environments = objects.getT1();
                    List<UUID> appEnvIds = objects.getT2();

                    LOG.info("when application is associated with an environment");
                    List<EnvironmentAssociation> list = new ArrayList<>();
                    for(Environment env: environments) {
                        boolean val = appEnvIds.contains(env.getId());
                        EnvironmentAssociation environmentAssociation =
                                new EnvironmentAssociation(val, env.getId(), env.getName());
                        list.add(environmentAssociation);
                        LOG.info("add environment association: {}", environmentAssociation);
                    }
                    LOG.info("returning list: {}", list);
                    return Flux.fromIterable(list);
                });
               return environmentAssociationFlux1;
            }
        });
        return environmentAssociationFlux;
    }

    @Override
    public Flux<Cluster> getUnassociatedClustersForApplicationId(UUID applicationId) {
        Mono<Application> applicationMono = applicationRepository.findById(applicationId);

        return applicationMono.flatMapMany(application -> {
           if (application.getPlatformId() == null) {
               LOG.info("app.platformId is null, returning all clusters");
               return clusterRepository.findAll();
           }
           else {
               LOG.info("app.platformId is not null, returning unassociated clusters");
               return clusterRepository.findById(application.getPlatformId()).flatMapMany(
                       cluster -> clusterRepository.findByIdNot(cluster.getId())
               );
           }
        });
    }

    @Override
    public Mono<Cluster> getClusterWithAssociatedEnvironmentsForApplicationId(UUID applicationId) {
        return applicationRepository.findById(applicationId).flatMap(application ->
                clusterRepository.findById(application.getPlatformId()).flatMap(cluster -> {
           Flux<Environment> environmentFlux = environmentRepository.findByClusterIdOrderBySortOrderAsc(cluster.getId());
           environmentFlux.collectList().subscribe(environmentList -> cluster.setEnvironmentList(environmentList));

           return Mono.just(cluster);
       }));
    }

    @Override
    public Flux<EnvironmentAssociation> associateEnvironment(UUID applicationId, List<EnvironmentAssociation> environmentAssociations) {
        LOG.info("environmentAssociations.size: {}, list: {}", environmentAssociations.size(), environmentAssociations);
        for(EnvironmentAssociation environmentAssociation : environmentAssociations) {
            if (environmentAssociation.isAssociated()) {
                LOG.info("associated environmentAssociation {}", environmentAssociation);
                applicationEnvironmentRepository.existsByApplicationIdAndEnvironmentId(applicationId, environmentAssociation.getEnvironmentId())
                        .doOnNext(aBoolean -> {
                            LOG.info("exist?: {}", aBoolean);
                            if(!aBoolean) {
                                applicationEnvironmentRepository.save(new ApplicationEnvironment(
                                        applicationId, environmentAssociation.getEnvironmentId()));

                                LOG.info("associated application with environment {}", environmentAssociation.getEnvironmentName());
                            }
                        });
            }
            else {
                LOG.info("not associated environmentAssociation {}", environmentAssociation);
                applicationEnvironmentRepository.existsByApplicationIdAndEnvironmentId(applicationId, environmentAssociation.getEnvironmentId())
                        .doOnNext(aBoolean -> {
                            LOG.info("exist?: {}", aBoolean);
                            if(aBoolean == true) {
                                applicationEnvironmentRepository.deleteByApplicationIdAndEnvironmentId(applicationId, environmentAssociation.getEnvironmentId());
                                LOG.info("deleted associated application with environment {}", environmentAssociation.getEnvironmentName());
                            }

                        });
            }
        }

        LOG.info("return environment association");
        return getEnvironmentAssociationForApplicationId(applicationId);
    }

    @Override
    public Flux<EnvironmentAssociation> associateCluster(UUID applicationId, UUID clusterId) {
        applicationRepository.findById(applicationId).doOnNext(application -> {
            clusterRepository.findById(clusterId).doOnNext(cluster -> {
                LOG.info("application.setPlatformId clusterId");
                application.setPlatformId(clusterId);
                applicationRepository.save(application).subscribe(application1 -> LOG.info("updated app cluster-id"));
            });
            LOG.info("delete app from applicationEnvironment");
            applicationEnvironmentRepository.deleteByApplicationId(applicationId);
        });
        return getEnvironmentAssociationForApplicationId(applicationId);
    }

    @Override
    public Flux<Component> getConnectedComponents(UUID applicationId) {
        LOG.info("getConnectedComponents");
        return connectionRepository.findByAppIdSource(applicationId).flatMap(connection -> {
            List<Component> list = new ArrayList<>();
            if(connection.getConnecting() != null && Connection.CONNECTING.COMPONENT.name().equals(connection.getConnecting())) {
                componentRepository.findById(connection.getId()).doOnNext(component -> list.add(component));
            }
            LOG.info("return flux list.size: {}, list: {}", list.size(), list);
            return Flux.fromIterable(list);
        });
    }

    @Override
    public Mono<String> connect(ConnectionForm connectionForm) {
        LOG.info("connect with form");
        applicationRepository.findById(connectionForm.getAppId()).doOnNext(application -> {
            connectionRepository.deleteByAppIdSourceAndConnecting(connectionForm.getAppId(), connectionForm.getConnecting());
            LOG.info("delete connection");
            for(UUID targetId: connectionForm.getTargetIdList()) {
                Connection connection = new Connection(Connection.ConnectionType.READE_WRITE, connectionForm.getConnecting(), connectionForm.getAppId(), targetId);
                connectionRepository.save(connection).subscribe(connection1 -> LOG.info("added connection source: {}",connection));
            }

        });
        return Mono.just("connection updated");
    }

    @Override
    public Flux<Application> getConnectedApps(UUID applicationId) {
        return connectionRepository.findByAppIdSource(applicationId).flatMap(connection ->
            applicationRepository.findById(connection.getTargetId()));
    }
}
