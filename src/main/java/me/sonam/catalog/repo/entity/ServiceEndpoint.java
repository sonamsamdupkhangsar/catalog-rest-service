package me.sonam.catalog.repo.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;

import java.util.UUID;

public class ServiceEndpoint implements Persistable<UUID> {
    public enum REST_METHOD {
        GET, POST, PUT, DELETE
    }

    @Id
    private UUID id;

    private UUID serviceId;

    private String name;

    private String endpoint;

    private String description;

    private String restMethod;

    private Boolean healthEndpoint = false;

    private Boolean accessTokenRequired;

    private Boolean pingIt;

    @Transient
    private boolean isNew;

    public ServiceEndpoint() {
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getServiceId() {
        return serviceId;
    }

    public void setServiceId(UUID serviceId) {
        this.serviceId = serviceId;
    }

    public void setIsNew(boolean value) {
        this.isNew = value;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getRestMethod() {
        return restMethod;
    }

    public void setRestMethod(String restMethod) {
        this.restMethod = restMethod;
    }

    public Boolean getHealthEndpoint() {
        return healthEndpoint;
    }

    public void setHealthEndpoint(Boolean healthEndpoint) {
        this.healthEndpoint = healthEndpoint;
    }

    public Boolean getAccessTokenRequired() {
        return accessTokenRequired;
    }

    public void setAccessTokenRequired(Boolean accessTokenRequired) {
        this.accessTokenRequired = accessTokenRequired;
    }

    public Boolean getPingIt() {
        return pingIt;
    }

    public void setPingIt(Boolean pingIt) {
        this.pingIt = pingIt;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public UUID getId() {
        return this.id;
    }

    @Override
    public boolean isNew() {
        return isNew;
    }

    @Override
    public String toString() {
        return "ServiceEndpoint{" +
                "id=" + id +
                ", serviceId=" + serviceId +
                ", name='" + name + '\'' +
                ", endpoint='" + endpoint + '\'' +
                ", description='" + description + '\'' +
                ", restMethod='" + restMethod + '\'' +
                ", healthEndpoint=" + healthEndpoint +
                ", accessTokenRequired=" + accessTokenRequired +
                ", pingIt=" + pingIt +
                ", isNew=" + isNew +
                '}';
    }
}