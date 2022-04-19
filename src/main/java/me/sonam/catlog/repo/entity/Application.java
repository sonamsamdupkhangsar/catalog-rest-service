package me.sonam.catlog.repo.entity;


import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * represents a Account record in Account table.
 */
public class Application implements Persistable<UUID> {
    @Id
    private UUID id;

    private String name;

    private Boolean deprecated;

    private String description;

    private String gitRepo;

    private String documentationUrl;

    private UUID platformId;

    @Transient
    private String platformName;

    @Transient
    private List<Environment> environmentList = new ArrayList<>();

    @Transient
    private List<Service> serviceList;

    @Transient
    private boolean isNew;

    public Application() {
    }

    @Override
    public boolean isNew() {
        return this.isNew;
    }

    public void setIsNew(boolean value) {
        this.isNew = value;
    }

    @Override
    public UUID getId() {
        return id;
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

    public Boolean getDeprecated() {
        return deprecated;
    }

    public void setDeprecated(Boolean deprecated) {
        this.deprecated = deprecated;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getGitRepo() {
        return gitRepo;
    }

    public void setGitRepo(String gitRepo) {
        this.gitRepo = gitRepo;
    }

    public String getDocumentationUrl() {
        return documentationUrl;
    }

    public void setDocumentationUrl(String documentationUrl) {
        this.documentationUrl = documentationUrl;
    }

    public UUID getPlatformId() {
        return platformId;
    }

    public void setPlatformId(UUID platformId) {
        this.platformId = platformId;
    }

    public String getPlatformName() {
        return platformName;
    }

    public void setPlatformName(String platformName) {
        this.platformName = platformName;
    }

    public List<Environment> getEnvironmentList() {
        return environmentList;
    }

    public void setEnvironmentList(List<Environment> environmentList) {
        this.environmentList = environmentList;
    }

    public List<Service> getServiceList() {
        return serviceList;
    }

    public void setServiceList(List<Service> serviceList) {
        this.serviceList = serviceList;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Application that = (Application) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Application{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", deprecated=" + deprecated +
                ", description='" + description + '\'' +
                ", gitUrl='" + gitRepo + '\'' +
                ", documentationUrl='" + documentationUrl + '\'' +
                ", platformId=" + platformId +
                ", platformName='" + platformName + '\'' +
                ", environmentList=" + environmentList +
                ", serviceList=" + serviceList +
                ", isNew=" + isNew +
                '}';
    }
}