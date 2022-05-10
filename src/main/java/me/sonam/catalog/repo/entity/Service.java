package me.sonam.catalog.repo.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;


import java.util.List;
import java.util.Objects;
import java.util.UUID;


public class Service implements Persistable<UUID> {

    @Id
    private UUID id;

    private String name;

    private UUID applicationId;

    private String description;

    private String endpoint;

    //default to false
    private Boolean healthEndpoint = false;

    private Boolean accessTokenRequired;

    private Boolean pingIt;

    private String restMethod;

    @Transient
    private Application application;

    @Transient
    private List<Environment> environmentList;

    @Transient
    private String platformName;

    @Transient
    private boolean isNew;

    //this property is only here to support the front-end VueJs so they don't have to dynamically create
    //this field
    @Transient
    private ServiceEndpoint[] serviceEndpoints;

    public Service() {
    }

    public Service(String name, UUID applicationId) {
        this.isNew = true;
        this.id = UUID.randomUUID();
        this.name = name;
        this.applicationId = applicationId;
    }

    public UUID getId() {
        return id;
    }

    @Override
    public boolean isNew() {
        return isNew;
    }

    public void setIsNew(boolean value) {
        this.isNew = value;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public UUID getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(UUID applicationId) {
        this.applicationId = applicationId;
    }

    public Application getApplication() {
        return application;
    }

    public void setApplication(Application application) {
        this.application = application;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public Boolean getPingIt() {
        return pingIt;
    }

    public void setPingIt(Boolean ping) {
        this.pingIt = ping;
    }

    public Boolean getAccessTokenRequired() {
        return accessTokenRequired;
    }

    public void setAccessTokenRequired(Boolean accessTokenRequired) {
        this.accessTokenRequired = accessTokenRequired;
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

    public List<Environment> getEnvironmentList() {
        return environmentList;
    }

    public void setEnvironmentList(List<Environment> environmentList) {
        this.environmentList = environmentList;
    }

    public String getPlatformName() {
        return platformName;
    }

    public void setPlatformName(String platformName) {
        this.platformName = platformName;
    }

    public ServiceEndpoint[] getServiceEndpoints() {
        return serviceEndpoints;
    }

    public void setServiceEndpoints(ServiceEndpoint[] serviceEndpoints) {
        this.serviceEndpoints = serviceEndpoints;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Service service = (Service) o;
        return Objects.equals(id, service.id) &&
                Objects.equals(name, service.name) &&
                Objects.equals(applicationId, service.applicationId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, applicationId);
    }

    @Override
    public String toString() {
        return "Service{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", applicationId=" + applicationId +
                ", description='" + description + '\'' +
                ", endpoint='" + endpoint + '\'' +
                ", accessTokenRequired=" + accessTokenRequired +
                ", pingIt=" + pingIt +
                '}';
    }
}
