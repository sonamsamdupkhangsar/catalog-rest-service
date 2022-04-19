package me.sonam.catlog.repo.entity;

public class EnvironmentStatus {
    private Environment environment;

    private String status;

    public EnvironmentStatus() {

    }
    public EnvironmentStatus(Environment environment, String status) {
        this.environment = environment;
        this.status = status;
    }

    public Environment getEnvironment() {
        return environment;
    }

    public String getStatus() {
        return status;
    }
}