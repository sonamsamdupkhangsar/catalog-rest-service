package me.sonam.catlog.repo.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;

import java.util.UUID;

public class ApplicationEnvironment  implements Persistable<UUID> {

    @Id
    private UUID applicationId;

    private UUID environmentId;

    @Transient
    private boolean isNew;

    public ApplicationEnvironment() {
    }

    public ApplicationEnvironment(UUID applicationId, UUID environmentId) {
        this.applicationId = applicationId;
        this.environmentId = environmentId;

        this.isNew = true;
    }

    public UUID getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(UUID applicationId) {
        this.applicationId = applicationId;
    }

    public UUID getEnvironmentId() {
        return environmentId;
    }

    public void setEnvironmentId(UUID environmentId) {
        this.environmentId = environmentId;
    }

    @Override
    public UUID getId() {
        return applicationId;
    }

    @Override
    public boolean isNew() {
        return isNew;
    }

    @Override
    public String toString() {
        return "ApplicationEnvironment{" +
                "applicationId=" + applicationId +
                ", environmentId=" + environmentId +
                ", isNew=" + isNew +
                '}';
    }
}
