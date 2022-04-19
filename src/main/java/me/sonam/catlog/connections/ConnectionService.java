package me.sonam.catlog.connections;

import me.sonam.catlog.repo.ConnectionRepository;
import me.sonam.catlog.repo.entity.Connection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class ConnectionService implements ConnectionBehavior {
    private static final Logger LOG = LoggerFactory.getLogger(ConnectionService.class);

    @Autowired
    private ConnectionRepository connectionRepository;

    @Override
    public Mono<Page<Connection>> getPage(Pageable pageable) {
        LOG.info("getPage");
        return connectionRepository.findAllBy(pageable).collectList()
                .zipWith(this.connectionRepository.count())
                .map(t -> new PageImpl<>(t.getT1(), pageable, t.getT2()));
    }
}
