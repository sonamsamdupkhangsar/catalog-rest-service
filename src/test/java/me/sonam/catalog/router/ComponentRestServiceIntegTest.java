package me.sonam.catalog.router;

import me.sonam.catalog.repo.ComponentRepository;
import me.sonam.catalog.repo.entity.Component;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.EntityExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.assertj.core.api.Assertions.assertThat;

@EnableAutoConfiguration
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ComponentRestServiceIntegTest {
    private static final Logger LOG = LoggerFactory.getLogger(ComponentRestServiceIntegTest.class);

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private ComponentRepository componentRepository;

    @Test
    public void getPage() {
        for (int i = 0; i < 10; i++) {
            Component component = new Component("Mysql Component", null);
            componentRepository.save(component).subscribe(cluster1 -> LOG.info("saved component"));

            Component child = new Component("database", component.getId());
            componentRepository.save(child).subscribe(child1 -> LOG.info("saved child"));
        }

        LOG.info("get Page of clusters");
        EntityExchangeResult<String> result = webTestClient.get().uri("/clusters")
                .exchange().expectStatus().isOk().expectBody(String.class).returnResult();

        LOG.info("found page of clusters: {}", result.getResponseBody());
    }

    @Test
    public void getComponent() {
        Component component = new Component("Mysql Component", null);
        componentRepository.save(component).subscribe(cluster1 -> LOG.info("saved component"));

        LOG.info("get component");
        EntityExchangeResult<Component> result = webTestClient.get().uri("/components/component/"+component.getId())
                .exchange().expectStatus().isOk().expectBody(Component.class).returnResult();

        LOG.info("found component: {}", result.getResponseBody());
        assertThat(result.getResponseBody()).isEqualTo(component);
    }

    @Test
    public void getParentComponents() {
        for (int i = 0; i < 10; i++) {
            Component component = new Component("Mysql Component", null);
            componentRepository.save(component).subscribe(cluster1 -> LOG.info("saved component"));

            Component child = new Component("database", component.getId());
            componentRepository.save(child).subscribe(child1 -> LOG.info("saved child"));
        }


        LOG.info("get parent components");
        EntityExchangeResult<Component[]> result = webTestClient.get().uri("/components/parents")
                .exchange().expectStatus().isOk().expectBody(Component[].class).returnResult();

        LOG.info("found cluster.length: {}, clusters: {}", result.getResponseBody());
        assertThat(result.getResponseBody().length).isGreaterThan(9);
    }

    @Test
    public void update() {
        Component child = new Component("Mysql database", null);
        componentRepository.save(child).subscribe(child1 -> LOG.info("saved component"));

        Component parent = new Component("Mysql Component", null);
        componentRepository.save(parent).subscribe(component1 -> LOG.info("saved parent component"));

        child.setParentId(parent.getId());

        EntityExchangeResult<Component> result = webTestClient.post().uri("/components").bodyValue(child)
                .exchange().expectStatus().isOk().expectBody(Component.class).returnResult();

        LOG.info("component: {}", result.getResponseBody());
        assertThat(result.getResponseBody()).isEqualTo(child);
        assertThat(result.getResponseBody().getParentId()).isEqualTo(parent.getId());

    }

    @Test
    public void delete() {
        LOG.info("delete component");
        Component child = new Component("Mysql database", null);
        componentRepository.save(child).subscribe(child1 -> LOG.info("saved component"));

        Component parent = new Component("Mysql Component", null);
        componentRepository.save(parent).subscribe(component1 -> LOG.info("saved parent component"));

        child.setParentId(parent.getId());

        EntityExchangeResult<Component> result = webTestClient.delete().uri("/components/"+child.getId())
                .exchange().expectStatus().isOk().expectBody(Component.class).returnResult();

        LOG.info("response for delete component: {}", result.getResponseBody());
        assertThat(result.getResponseBody()).isNull();

        LOG.info("check the component is null on get");
        EntityExchangeResult<Component> result2 = webTestClient.get().uri("/components/component/"+child.getId())
                .exchange().expectStatus().isOk().expectBody(Component.class).returnResult();

        LOG.info("found component: {}", result2.getResponseBody());
        assertThat(result2.getResponseBody()).isNull();


    }
}
