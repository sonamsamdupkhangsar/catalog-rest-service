package me.sonam.catlog.repo.entity;


import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class Cluster implements Persistable<UUID> {

    @Id
    private UUID id;

    private String name;

    @Transient
    private boolean isNew; // use boolean primitive type so by default it is false instead of being null
    // Object can cause "JSON encoding error: Cannot invoke "java.lang.Boolean.booleanValue()" because "this.isNew" is null;"

    @Transient
    private List<Environment> environmentList = new ArrayList<>();

    @Transient
    private Flux<Environment> environmentFlux;

    public Cluster() {
    }

    public Cluster(UUID id, String name) {
        this.id = id;
        this.name = name;
        this.isNew = true;
    }
    public void setName(String name) {
        this.name = name;
    }

    public UUID getId() {
        return id;
    }

    @Override
    public boolean isNew() {
        return isNew;
    }

    public String getName() {
        return name;
    }

    public List<Environment> getEnvironmentList() {
        return environmentList;
    }

    public void setEnvironmentList(List<Environment> environmentList) {
        this.environmentList = environmentList;
    }

    public Flux<Environment> getEnvironmentFlux() {
        return environmentFlux;
    }

    public void setEnvironmentFlux(Flux<Environment> environmentFlux) {
        this.environmentFlux = environmentFlux;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Cluster cluster = (Cluster) o;
        return Objects.equals(id, cluster.id) &&
                Objects.equals(name, cluster.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }

    @Override
    public String toString() {
        return "Cluster{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
