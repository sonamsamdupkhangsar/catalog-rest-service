package me.sonam.catalog.service;

import me.sonam.catalog.repo.*;
import me.sonam.catalog.repo.entity.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

@org.springframework.stereotype.Service
public class ServiceBehaviorImpl implements ServiceBehavior{
    private static final Logger LOG = LoggerFactory.getLogger(ServiceBehaviorImpl.class);

    @Autowired
    private ServiceRepository serviceRepository;

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private ClusterRepository clusterRepository;

    @Autowired
    private ApplicationEnvironmentRepository applicationEnvironmentRepository;

    @Autowired
    private EnvironmentRepository environmentRepository;

    @Override
    public Mono<Page<Service>> getPage(Pageable pageable) {
        LOG.info("get page of services");
        return serviceRepository.findAllBy(pageable).collectList()
                .zipWith(this.serviceRepository.count())
                .map(t -> {
                    List<Service> list = t.getT1();

                    for(Service s: list) {
                        LOG.info("service.applicationId: {}", s.getApplicationId());
                        if (s.getApplicationId() != null) {
                            applicationRepository.findById(s.getApplicationId()).doOnNext(application -> {
                                s.setApplication(application);
                                LOG.info("set service.application");

                                if (application.getPlatformId() != null) {
                                    clusterRepository.findById(application.getPlatformId()).map(cluster -> {
                                        LOG.info("set service.platformName");
                                        s.setPlatformName(cluster.getName());

                                        applicationEnvironmentRepository.findByApplicationId(application.getId())
                                                .doOnNext(applicationEnvironment -> {

                                                    environmentRepository.findById(applicationEnvironment.getEnvironmentId())
                                                            .map(environment -> s.getEnvironmentList().add(environment));
                                                });
                                        return cluster;
                                    });
                                }

                            });
                        }
                    }
                    return new PageImpl<>(t.getT1(), pageable, t.getT2());
                });
    }

    @Override
    public Mono<Service> update(Service service) {
        LOG.info("update service: {}", service);
        if (service.getId() == null) {
            service.setId(UUID.randomUUID());
            service.setIsNew(true);
            LOG.info("set new to true");
        }
        Mono<Service> serviceMono = serviceRepository.save(service);
        serviceMono.subscribe(service1 -> LOG.info("Saved service"));
        return serviceMono;
    }

    @Override
    public Mono<String> delete(UUID serviceId) {
        LOG.info("delete service");
        return serviceRepository.deleteById(serviceId).flatMap(unused -> {
            LOG.info("deleted service");
            return Mono.just("deleted service");
        });
    }

}
