package me.sonam.catalog.cluster;

import me.sonam.catalog.repo.ClusterRepository;
import me.sonam.catalog.repo.EnvironmentRepository;
import me.sonam.catalog.repo.entity.Cluster;
import me.sonam.catalog.repo.entity.Environment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Component
public class ClusterService implements ClusterBehavior{
    private static final Logger LOG = LoggerFactory.getLogger(ClusterService.class);

    @Autowired
    private ClusterRepository clusterRepository;

    @Autowired
    private EnvironmentRepository environmentRepository;

    @Override
    public Mono<Page<Cluster>> getClusters(Pageable pageable) {
        LOG.info("get clusters page");
        clusterRepository.count().doOnNext(aLong -> {
           if(aLong == 0) {
               Cluster cluster = new Cluster();
               clusterRepository.save(cluster).subscribe(cluster1 -> LOG.info("save a default cluster when none found"));
           }
        });

        return clusterRepository.findAllBy(pageable).collectList()
                .zipWith(this.clusterRepository.count())
                .map(t -> new PageImpl<>(t.getT1(), pageable, t.getT2()));
    }

    @Override
    public Mono<Cluster> getCluster(UUID clusterId) {
        LOG.info("get cluster with id: {}", clusterId);

        return clusterRepository.findById(clusterId);
    }

    @Override
    public Flux<Cluster> getClustersNotAssociatedWith(UUID environmentId) {
        LOG.info("getClustersNotAssociatedWith");
        return environmentRepository.findById(environmentId).flatMapMany(environment -> {
            if (environment.getClusterId() != null) {
                LOG.info("return id not matching cluster-id: {}", environment.getClusterId());
                return clusterRepository.findByIdNot(environment.getClusterId());
            }
            else {
                LOG.info("return all clusters when env.clusterId is null");
                return clusterRepository.findAll();
            }
        });
    }

    @Override
    public Mono<Cluster> update(Cluster cluster) {
        LOG.info("update cluster: {}", cluster);

        if (cluster.getId() == null) {
            cluster.setIsNew(true);
            LOG.info("set new to true when id is null");
        }
        return clusterRepository.save(cluster).map(cluster1 -> {
            LOG.info("saved cluster");
            return cluster1;
        });
    }

    @Override
    public Flux<Environment> getEnvrionments(UUID clusterId) {
        LOG.info("get environments by cluster-id");

        return clusterRepository.findById(clusterId).flatMapMany(cluster ->
             environmentRepository.findByClusterIdOrderBySortOrderAsc(cluster.getId()));
    }

    @Override
    public Mono<String> delete(UUID clusterId) {
        LOG.info("delete cluster by id");
        return clusterRepository.deleteById(clusterId).flatMap(unused -> {
            LOG.info("deleted cluster by id: {}", clusterId);
            return Mono.just("deleted cluster");
        });
    }
}
