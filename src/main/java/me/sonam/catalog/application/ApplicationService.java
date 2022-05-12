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
        LOG.info("get apps by pageable {}", pageable);
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
                LOG.info("need to save association for environmentAssociation: {}", environmentAssociation);
                applicationEnvironmentRepository.existsByApplicationIdAndEnvironmentId(applicationId, environmentAssociation.getEnvironmentId())
                        .doOnNext(aBoolean -> {
                            LOG.info("if it's false then save: {}", aBoolean);
                            if(!aBoolean) {
                                LOG.info("save in repo now");
                                applicationEnvironmentRepository.save(new ApplicationEnvironment(
                                        applicationId, environmentAssociation.getEnvironmentId())).subscribe(applicationEnvironment ->
                                        LOG.info("associated application with envrionment: {}", applicationEnvironment));
                            }
                        }).subscribe(aBoolean -> LOG.info("aBoolean is {}", aBoolean));
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

                        }).subscribe(aBoolean -> LOG.info("subscribe for deletion to cause it."));
            }
        }

        LOG.info("return environment association");
        return getEnvironmentAssociationForApplicationId(applicationId);
    }

    @Override
    public Flux<EnvironmentAssociation> associateCluster(UUID applicationId, UUID clusterId) {
        LOG.info("associate cluster");
        Mono<Cluster> clusterMono = clusterRepository.findById(clusterId);

       return applicationRepository.findById(applicationId).zipWith(clusterMono).doOnNext(objects -> {
           LOG.info("delete app from applicationEnvironment");
           applicationEnvironmentRepository.deleteByApplicationId(objects.getT1().getId());
           Application application = objects.getT1();
           application.setPlatformId(objects.getT2().getId());
           applicationRepository.save(application).subscribe(application1 -> LOG.info("updated app cluster-id"));

        }).thenMany(
       getEnvironmentAssociationForApplicationId(applicationId));
    }

    @Override
    public Flux<Component> getConnectedComponents(UUID applicationId) {
        LOG.info("getConnectedComponents for appId: {}", applicationId);

        return connectionRepository.findByAppIdAndConnecting(applicationId, Connection.CONNECTING.COMPONENT.name())
                .flatMap(connection -> componentRepository.findById(connection.getTargetId()));
    }



    @Override
    public Flux<Application> getConnectedApps(UUID applicationId) {
        return connectionRepository.findByAppId(applicationId).flatMap(connection ->
            applicationRepository.findById(connection.getTargetId()));
    }
}
