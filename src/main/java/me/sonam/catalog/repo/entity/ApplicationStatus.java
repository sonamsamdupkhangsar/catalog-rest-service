package me.sonam.catalog.repo.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;

import java.util.Objects;
import java.util.UUID;

public class ApplicationStatus implements Persistable<UUID> {

    @Id
    private UUID id;

    private UUID applicationId;

    private String applicationName;

    private String devStatus;

    private UUID devEnvironmentId;

    //platform is cluster (using platform going forward)
    private UUID platformId;
    private String platform;

    private String stageStatus;
    private UUID stageEnvironmentId;

    private String prodStatus;
    private UUID prodEnvironmentId;

    public EnvironmentStatus getProdEnvStatus() {
        return prodEnvStatus;
    }

    public void setProdEnvStatus(EnvironmentStatus prodEnvStatus) {
        this.prodEnvStatus = prodEnvStatus;
    }

    @Transient
    private EnvironmentStatus prodEnvStatus;

    @Transient
    private boolean isNew;

    public ApplicationStatus() {
        this.id = UUID.randomUUID();
        this.isNew = true;
    }


    public UUID getId() {
        return id;
    }

    @Override
    public boolean isNew() {
        return isNew;
    }

    public void setIsNew(boolean val) {
        this.isNew = val;
    }

    public UUID getApplicationId() {
        return applicationId;
    }

    public String getDevStatus() {
        return devStatus;
    }

    public String getStageStatus() {
        return stageStatus;
    }

    public String getProdStatus() {
        return prodStatus;
    }

    public String getApplicationName() {
        return applicationName;
    }

    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }

    public UUID getDevEnvironmentId() {
        return devEnvironmentId;
    }

    public void setDevEnvironmentId(UUID devEnvironmentId) {
        this.devEnvironmentId = devEnvironmentId;
    }

    public UUID getStageEnvironmentId() {
        return stageEnvironmentId;
    }

    public void setStageEnvironmentId(UUID stageEnvironmentId) {
        this.stageEnvironmentId = stageEnvironmentId;
    }

    public UUID getProdEnvironmentId() {
        return prodEnvironmentId;
    }

    public void setProdEnvironmentId(UUID prodEnvironmentId) {
        this.prodEnvironmentId = prodEnvironmentId;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public UUID getPlatformId() {
        return platformId;
    }

    public void setPlatformId(UUID platformId) {
        this.platformId = platformId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ApplicationStatus that = (ApplicationStatus) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "ApplicationStatus{" +
                "id=" + id +
                ", applicationId=" + applicationId +
                ", devStatus='" + devStatus + '\'' +
                ", stageStatus='" + stageStatus + '\'' +
                ", prodStatus='" + prodStatus + '\'' +
                '}';
    }

    public void setApplicationId(UUID applicationId) {
        this.applicationId = applicationId;
    }

    public void setDevStatus(String devStatus) {
        this.devStatus = devStatus;
    }

    public void setStageStatus(String stageStatus) {
        this.stageStatus = stageStatus;
    }

    public void setProdStatus(String prodStatus) {
        this.prodStatus = prodStatus;
    }
}
