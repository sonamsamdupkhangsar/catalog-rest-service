package me.sonam.catalog.connections;

import me.sonam.catalog.repo.ApplicationRepository;
import me.sonam.catalog.repo.ComponentRepository;
import me.sonam.catalog.repo.ConnectionRepository;
import me.sonam.catalog.repo.entity.Application;
import me.sonam.catalog.repo.entity.Component;
import me.sonam.catalog.repo.entity.Connection;
import me.sonam.catalog.repo.entity.ConnectionForm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
public class ConnectionService implements ConnectionBehavior {
    private static final Logger LOG = LoggerFactory.getLogger(ConnectionService.class);

    @Autowired
    private ConnectionRepository connectionRepository;

    @Autowired
    private ComponentRepository componentRepository;

    @Autowired
    private ApplicationRepository applicationRepository;

    @Override
    public Mono<Page<Connection>> getPage(Pageable pageable) {
        LOG.info("getPage");
        return connectionRepository.findAllBy(pageable).collectList()
                .zipWith(this.connectionRepository.count())
                .map(t -> new PageImpl<>(t.getT1(), pageable, t.getT2()));
    }

    @Override
    public Flux<Component> getConnectedComponents(UUID applicationId) {
        LOG.info("getConnectedComponents for appId: {}", applicationId);

        return connectionRepository.findByServiceEndpointIdAndConnecting(applicationId, Connection.CONNECTING.COMPONENT.name())
                .flatMap(connection -> componentRepository.findById(connection.getTargetId()));
    }

    @Override
    public Mono<String> connect(ConnectionForm connectionForm) {
        LOG.info("connectapp with component: {}", connectionForm);
        Mono<Long> deleteMono = connectionRepository.deleteByServiceEndpointIdAndConnecting(
                connectionForm.getAppId(), connectionForm.getConnecting());

        return deleteMono.map(aLong -> {
            LOG.info("deleted rows: {}", aLong);
            return applicationRepository.findById(connectionForm.getAppId());
        }).doOnNext(application -> {

            LOG.info("set connection now");
            for(UUID targetId: connectionForm.getTargetIdList()) {
                Connection connection = new Connection(Connection.ConnectionType.READE_WRITE, connectionForm.getConnecting(),
                        connectionForm.getServiceEndpointId(), targetId, connectionForm.getServiceId(), connectionForm.getAppId());
                connectionRepository.existsByServiceEndpointIdAndTargetIdAndConnecting(connection.getServiceEndpointId(),
                        connection.getTargetId(), connection.getConnecting()).doOnNext(aBoolean ->
                {
                    if (aBoolean == false) {
                        connectionRepository.save(connection).subscribe(connection1 -> LOG.info("added connection source: {}", connection));
                    }
                    else {
                        LOG.info("connection already exists with appId, targetId and connecting value");
                    }
                }).subscribe();
            }

        }).thenReturn("connection updated");
    }

    @Override
    public Flux<Application> getConnectedApps(UUID serviceEndpointId) {
        return connectionRepository.findByServiceEndpointId(serviceEndpointId).flatMap(connection ->
                applicationRepository.findById(connection.getTargetId()));
    }

    @Override
    public Mono<String> deleteByServiceEndpointId(UUID serviceEndpointId) {
        LOG.info("delete by serviceEndpointId: {}", serviceEndpointId);

        return connectionRepository.deleteByServiceEndpointId(serviceEndpointId).flatMap(aLong -> {
                LOG.info("deleted {} connections by serviceEndpointId", aLong);
            return Mono.just("deleted by serviceEndpointId");
         });
    }

    @Override
    public Mono<String> deleteByServiceId(UUID serviceId) {
        LOG.info("delete by serviceId: {}", serviceId);

        return connectionRepository.deleteByServiceId(serviceId).flatMap(aLong -> {
            LOG.info("deleted {} connections by serviceId", aLong);
            return Mono.just("deleted by serviceId");
        });
    }
}
