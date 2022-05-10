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

    private String requestBody;

    private String responseBody;

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRequestBody() {
        return requestBody;
    }

    public void setRequestBody(String requestBody) {
        this.requestBody = requestBody;
    }

    public String getResponseBody() {
        return responseBody;
    }

    public void setResponseBody(String responseBody) {
        this.responseBody = responseBody;
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
                ", isNew=" + isNew +
                '}';
    }
}
