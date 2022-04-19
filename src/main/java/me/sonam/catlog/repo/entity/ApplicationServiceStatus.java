package me.sonam.catlog.repo.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;


public class ApplicationServiceStatus implements Persistable<UUID> {

    @Id
    private UUID id;

    private UUID applicationId;

    @Transient
    private Application application;

    private UUID serviceId;

    private String serviceEndpoint;

    @Transient
    private Service service;

    private UUID environmentId;
    @Transient
    private Environment environment;

    private int httpStatusValue;

    private LocalDateTime localDateTime;

    private LocalDateTime lastPingDateTime;

    private Integer successPingCount = 0;

    private String exceptionMessage;

    @Transient
    private boolean isNew;

    public ApplicationServiceStatus(UUID applicationId, UUID serviceId, UUID environmentId, int httpStatusValue, LocalDateTime localDateTime, String serviceEndpoint) {
        this.isNew = true;
        this.id = UUID.randomUUID();
        this.applicationId = applicationId;
        this.serviceId = serviceId;
        this.environmentId = environmentId;
        this.httpStatusValue = httpStatusValue;
        this.localDateTime = localDateTime;
        this.serviceEndpoint = serviceEndpoint;
    }

    public ApplicationServiceStatus() {
    }

    public String getServiceEndpoint() {
        return serviceEndpoint;
    }

    public void setServiceEndpoint(String serviceEndpoint) {
        this.serviceEndpoint = serviceEndpoint;
    }

    public UUID getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(UUID applicationId) {
        this.applicationId = applicationId;
    }

    public UUID getServiceId() {
        return serviceId;
    }

    public void setServiceId(UUID serviceId) {
        this.serviceId = serviceId;
    }

    public UUID getEnvironmentId() {
        return environmentId;
    }

    public void setEnvironmentId(UUID environmentId) {
        this.environmentId = environmentId;
    }

    public int getHttpStatusValue() {
        return httpStatusValue;
    }

    public void setHttpStatusValue(int httpStatusValue) {
        this.httpStatusValue = httpStatusValue;
    }

    public LocalDateTime getLocalDateTime() {
        return localDateTime;
    }

    public void setLocalDateTime(LocalDateTime localDateTime) {
        this.localDateTime = localDateTime;
    }

    public String getExceptionMessage() {
        return exceptionMessage;
    }

    public void setExceptionMessage(String exceptionMessage) {
        this.exceptionMessage = exceptionMessage;
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

    public LocalDateTime getLastPingDateTime() {
        return lastPingDateTime;
    }

    public void setLastPingDateTime(LocalDateTime lastPingDateTime) {
        this.lastPingDateTime = lastPingDateTime;
    }

    public Application getApplication() {
        return application;
    }

    public void setApplication(Application application) {
        this.application = application;
    }

    public Service getService() {
        return service;
    }

    public void setService(Service service) {
        this.service = service;
    }

    public Environment getEnvironment() {
        return environment;
    }

    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    public Integer getSuccessPingCount() {
        return successPingCount;
    }

    public void setSuccessPingCount(Integer successPingCount) {
        this.successPingCount = successPingCount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ApplicationServiceStatus that = (ApplicationServiceStatus) o;
        return httpStatusValue == that.httpStatusValue &&
                Objects.equals(id, that.id) &&
                Objects.equals(applicationId, that.applicationId) &&
                Objects.equals(serviceId, that.serviceId) &&
                Objects.equals(environmentId, that.environmentId) &&
                Objects.equals(localDateTime, that.localDateTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, applicationId, serviceId, environmentId, httpStatusValue, localDateTime);
    }

    @Override
    public String toString() {
        return "ApplicationServiceStatus{" +
                "id=" + id +
                ", applicationId=" + applicationId +
                ", serviceId=" + serviceId +
                ", environmentId=" + environmentId +
                ", httpStatusValue=" + httpStatusValue +
                ", localDateTime=" + localDateTime +
                '}';
    }
}
