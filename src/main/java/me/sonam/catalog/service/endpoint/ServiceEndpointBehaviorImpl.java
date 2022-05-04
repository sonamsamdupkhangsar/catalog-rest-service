package me.sonam.catalog.service.endpoint;

import me.sonam.catalog.repo.*;
import me.sonam.catalog.repo.entity.Service;
import me.sonam.catalog.repo.entity.ServiceEndpoint;
import me.sonam.catalog.service.ServiceBehavior;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

@org.springframework.stereotype.Service
public class ServiceEndpointBehaviorImpl implements ServiceEndpointBehavior {
    private static final Logger LOG = LoggerFactory.getLogger(ServiceEndpointBehaviorImpl.class);

    @Autowired
    private ServiceEndpointRepository serviceEndpointRepository;

    @Override
    public Flux<ServiceEndpoint> getServiceEndpoints(UUID serviceId) {
        LOG.info("get serviceEndpoint by serviceId: {}", serviceId);
        return serviceEndpointRepository.findByServiceId(serviceId);
    }

    @Override
    public Mono<ServiceEndpoint> update(ServiceEndpoint serviceEndpoint) {
        LOG.info("update serviceEndpoint: {}", serviceEndpoint);
        if (serviceEndpoint.getId() == null) {
            serviceEndpoint.setId(UUID.randomUUID());
            serviceEndpoint.setIsNew(true);
            LOG.info("set serviceEndpoint new to true");
        }
        Mono<ServiceEndpoint> serviceEndpointMono = serviceEndpointRepository.save(serviceEndpoint);
        serviceEndpointMono.subscribe(serviceEndpoint1 -> LOG.info("Saved serviceEndpoint"));
        return serviceEndpointMono;
    }

    @Override
    public Mono<String> delete(UUID serviceEndpointId) {
        LOG.info("delete serviceEndpoint");
        return serviceEndpointRepository.deleteById(serviceEndpointId).flatMap(unused -> {
            LOG.info("deleted serviceEndpoint");
            return Mono.just("deleted serviceEndpoint");
        });
    }

}
