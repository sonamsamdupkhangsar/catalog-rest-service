package me.sonam.catalog.environment;

import me.sonam.catalog.repo.EnvironmentRepository;
import me.sonam.catalog.repo.entity.Environment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class EnvironmentService implements EnvironmentBehavior{
    private static final Logger LOG = LoggerFactory.getLogger(EnvironmentService.class);

    @Autowired
    private EnvironmentRepository environmentRepository;

    @Override
    public Mono<Page<Environment>> getPage(Pageable pageable) {
        LOG.info("get page of enviroments");
        return environmentRepository.findAllBy(pageable).collectList()
                .zipWith(this.environmentRepository.count())
                .map(t -> new PageImpl<>(t.getT1(), pageable, t.getT2()));
    }

    @Override
    public Flux<Environment> getByCluster(UUID clusterId) {
        LOG.info("getByClusterId");
        return environmentRepository.findByClusterIdOrderBySortOrderAsc(clusterId);
    }

    @Override
    public Mono<Environment> update(Environment environment) {
        LOG.info("update environment: {}", environment);
        if (environment.getId() == null) {
            environment.setId(UUID.randomUUID());
            environment.setIsNew(true);
            LOG.info("set new to true");
        }

        Mono<Environment> mono = environmentRepository.save(environment);
        mono.subscribe(environment1 -> LOG.info("saved environment"));
        return mono;
    }

    @Override
    public Mono<String> delete(UUID environmentId) {
        LOG.info("delete environment");
        return environmentRepository.deleteById(environmentId).flatMap(unused -> {
            LOG.info("deleted envrionment");
            return Mono.just("deleted environemnt");
        });
    }

    @Override
    public Flux<String> getEnvironmentTypes() {
       List<String> list = new ArrayList<>();
        list.add(Environment.EnvironmentTypeEnum.FEATURE.name()+"\n");
        list.add(Environment.EnvironmentTypeEnum.DEV.name()+"\n");
        list.add(Environment.EnvironmentTypeEnum.STAGE.name()+"\n");
        list.add(Environment.EnvironmentTypeEnum.PROD.name()+"\n");

        return Flux.fromIterable(list);
    }
}
