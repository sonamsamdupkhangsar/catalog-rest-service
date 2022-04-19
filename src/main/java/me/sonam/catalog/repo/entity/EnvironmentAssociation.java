package me.sonam.catalog.repo.entity;

import java.util.UUID;

public class EnvironmentAssociation {
        private boolean associated;
        private UUID environmentId;
        private String environmentName;



    public EnvironmentAssociation() {
    }

    public EnvironmentAssociation(boolean associated, UUID environmentId, String environmentName) {
        this.associated = associated;
        this.environmentId = environmentId;
        this.environmentName = environmentName;
    }

    public boolean isAssociated() {
        return associated;
    }

    public UUID getEnvironmentId() {
        return environmentId;
    }

    public String getEnvironmentName() {
        return environmentName;
    }

    public void setAssociated(boolean associated) {
        this.associated = associated;
    }

    public void setEnvironmentId(UUID environmentId) {
        this.environmentId = environmentId;
    }

    public void setEnvironmentName(String environmentName) {
        this.environmentName = environmentName;
    }

    @Override
    public String toString() {
        return "EnvironmentAssociation{" +
                "associated=" + associated +
                ", environmentId=" + environmentId +
                ", environmentName='" + environmentName + '\'' +
                '}';
    }
}
