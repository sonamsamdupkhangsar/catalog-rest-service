package me.sonam.catalog.component;

import me.sonam.catalog.repo.ComponentRepository;
import me.sonam.catalog.repo.entity.Component;
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
public class ComponentService implements ComponentBehavior{
    private static final Logger LOG = LoggerFactory.getLogger(ComponentService.class);

    @Autowired
    private ComponentRepository componentRepository;

    @Override
    public Mono<Component> update(Component component) {
        LOG.info("update component: {}", component);
        if (component.getId() == null) {
            component.setId(UUID.randomUUID());
            component.setIsNew(true);
        }

        Mono<Component> componentMono =  componentRepository.save(component).doOnNext(component1 -> {
            if (component1.getParentId() != null) {
                componentRepository.findById(component1.getParentId()).map(component2 -> {
                    LOG.info("set parent component in component saved");
                    component1.setParent(component2);
                    return component1;
                });
            }
            LOG.info("return component saved");
        });
        componentMono.subscribe(component1 -> LOG.info("saved component"));
        return componentMono;
    }

    @Override
    public Flux<Component> getParentComponents() {
        LOG.info("getParentComponents");
        return componentRepository.findByParentIdNull();
    }

    @Override
    public Mono<Page<Component>> getPage(Pageable pageable) {
        LOG.info("getPage");
        return componentRepository.findAllBy(pageable).collectList()
                .zipWith(this.componentRepository.count())
                .map(t -> {
                    for(Component component: t.getT1()) {
                        if (component.getParentId() != null) {
                            LOG.info("set parent in getPage");
                            componentRepository.findById(component.getParentId()).doOnNext(parent ->
                                    component.setParent(parent));
                        }
                    }
                    return new PageImpl<>(t.getT1(), pageable, t.getT2());
                });
    }

    @Override
    public Mono<Component> getComponent(UUID componentId) {
        LOG.info("getComponent with id: {}", componentId);
        Mono<Component> componentMono = componentRepository.findById(componentId);

       return componentMono.map(component -> component.getParentId()).map(parentId -> {
           if (parentId != null) {
               LOG.info("parientid is null");
             return null;
           }
           else {
               LOG.info("get by parentId");
               return componentRepository.findById(parentId);
           }
        }).map(componentMono1 -> componentRepository.findById(componentId).zipWith(componentMono).doOnNext(objects ->
                objects.getT1().setParent(objects.getT2())
   /*return componentRepository.findById(componentId).zipWith(componentMono).doOnNext(objects ->
           objects.getT1().setParent(objects.getT2())*/
       )).then(componentMono);
/*
        return componentRepository.findById(componentId).doOnNext(component1 -> {
            if (component1.getParentId() != null) {
                componentRepository.findById(component1.getParentId()).map(component2 -> {
                    LOG.info("set parent component in component saved");
                    component1.setParent(component2);
                    return component1;
                });
            }
            LOG.info("return component saved");
        });*/
    }

    @Override
    public Mono<String> delete(UUID componentId) {
        LOG.info("delete component by id");
        return componentRepository.deleteById(componentId).flatMap(unused -> {
            LOG.info("deleted component by id: {}", componentId);
            return Mono.just("deleted component");
        });
    }
}
