package me.sonam.catlog.repo;


import me.sonam.catlog.repo.entity.Component;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@EnableAutoConfiguration
@ExtendWith(SpringExtension.class)
@SpringBootTest( webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ClusterRepositoryIntegTest {

    private static final Logger LOG = LoggerFactory.getLogger(ClusterRepositoryIntegTest.class);

    @Autowired
    private WebTestClient client;

    @Autowired
    private ComponentRepository componentRepository;

    @Test
    public void save() {
        Component component = new Component("production cluster", null);

        LOG.info("save cluster");
        Mono<Component> componentMono = componentRepository.save(component);

        componentMono.as(StepVerifier::create)
                .assertNext(actual -> {
                    assertThat(actual.getName()).isEqualTo("production cluster");
                    assertThat(actual.getParentId()).isNull();
                    assertThat(actual.getParent()).isNull();
                    LOG.info("verify complete");
                })
                .verifyComplete();

        componentMono = componentRepository.findById(component.getId());
        componentMono.as(StepVerifier::create)
                .assertNext(actual ->
                {
                    componentRepository.delete(actual).subscribe(app1 -> assertThat(app1).isNull());

                    LOG.info("deleted cluster");
                    assertThat(componentRepository.findById(actual.getId()).subscribe(component1
                            -> assertThat(component1).isNull()));
                    LOG.info("should return null after deleted");
                })
                .verifyComplete();
    }
}
