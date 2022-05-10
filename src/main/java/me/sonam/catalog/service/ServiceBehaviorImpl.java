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
        return serviceRepository.findAllBy(pageable).filter(service -> service.getApplicationId() != null)
                .flatMap(service -> {
                    LOG.info("service.appId: {}", service.getApplicationId());
                    return applicationRepository.findById(service.getApplicationId()).
                            doOnNext(application -> service.setApplication(application)).thenReturn(service);
                })
                .filter(service -> service.getApplication().getPlatformId() != null)
                .doOnNext(service -> {
                    LOG.info("service.getApplication().getPlatformId(): {}", service.getApplication().getPlatformId());
                      clusterRepository.findById(service.getApplication().getPlatformId())

                            .doOnNext(cluster -> {service.getApplication().setPlatformName(cluster.getName());
                            LOG.info("set clustername: {}", service.getApplication().getPlatformName());
                            }).subscribe();
                    // TO DO: fix this so there is no subscribe method on it.
                }).doOnNext(service -> LOG.info("service.getApplication().getPlatformName(): {}",
                        service.getApplication().getPlatformName()))
                .collectList()
                .zipWith(this.serviceRepository.count())
                .map(t -> new PageImpl<>(t.getT1(), pageable, t.getT2()));
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
