package me.sonam.catalog.repo.entity;


import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;

import java.util.Objects;
import java.util.UUID;

public class Dependency implements Persistable<UUID> {
    @Id
    private UUID id;

    //this is the serviceId
    private UUID providerId;

    //this is the applicationId
    private UUID consumerId;

    @Transient
    private boolean isNew;

    public Dependency() {
    }

    public Dependency(UUID providerId, UUID consumerId) {
        this.isNew = true;
        this.id = UUID.randomUUID();
        this.providerId = providerId;
        this.consumerId = consumerId;
    }

    public UUID getId() {
        return id;
    }

    @Override
    public boolean isNew() {
        return isNew;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getProviderId() {
        return providerId;
    }

    public void setProviderId(UUID providerId) {
        this.providerId = providerId;
    }

    public UUID getConsumerId() {
        return consumerId;
    }

    public void setConsumerId(UUID consumerId) {
        this.consumerId = consumerId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Dependency that = (Dependency) o;
        return Objects.equals(providerId, that.providerId) &&
                Objects.equals(consumerId, that.consumerId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(providerId, consumerId);
    }
}
