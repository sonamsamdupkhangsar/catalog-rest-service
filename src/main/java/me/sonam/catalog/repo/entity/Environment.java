package me.sonam.catalog.repo.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;

import java.util.Objects;
import java.util.UUID;

public class Environment implements Persistable<UUID> {

    public enum EnvironmentTypeEnum {
        FEATURE, DEV, STAGE, PROD
    }

    @Id
    private UUID id;

    private int sortOrder = 0;

    private String environmentType;

    private String name;

    private String domain;

    private String deploymentLink;

    private UUID clusterId;

    @Transient
    private Cluster cluster;

    @Transient
    private boolean isNew;

    public Environment() {
        this.isNew = true;
        this.id = UUID.randomUUID();
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

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getDeploymentLink() {
        return deploymentLink;
    }

    public void setDeploymentLink(String deploymentLink) {
        this.deploymentLink = deploymentLink;
    }

    public int getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(int sortOrder) {
        this.sortOrder = sortOrder;
    }

    public UUID getClusterId() {
        return clusterId;
    }

    public void setClusterId(UUID clusterId) {
        this.clusterId = clusterId;
    }

    public Cluster getCluster() {
        return cluster;
    }

    public void setCluster(Cluster cluster) {
        this.cluster = cluster;
    }

    public String getEnvironmentType() {
        return environmentType;
    }

    public void setEnvironmentType(String environmentType) {
        this.environmentType = environmentType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Environment that = (Environment) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Environment{" +
                "id=" + id +
                ", sortOrder=" + sortOrder +
                ", environmentType='" + environmentType + '\'' +
                ", name='" + name + '\'' +
                ", domain='" + domain + '\'' +
                ", deploymentLink='" + deploymentLink + '\'' +
                ", clusterId=" + clusterId +
                ", cluster=" + cluster +
                ", isNew=" + isNew +
                '}';
    }
}
