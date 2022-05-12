package me.sonam.catalog.repo.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;

import java.util.UUID;

public class Connection implements Persistable<UUID> {
    public enum ConnectionType {
        READ, WRITE, READE_WRITE
    }

    public enum CONNECTING {
       SERVICE, APP, COMPONENT
    }

    @Id
    private UUID id;

    private String connection;

    private UUID serviceEndpointId;

    private UUID serviceId;

    private UUID appId;

    private UUID targetId;

    private String connecting;

    @Transient
    private boolean isNew;

    public Connection() {
    }

    public Connection(ConnectionType connectionType, String connecting, UUID serviceEndpointId, UUID targetId, UUID serviceId, UUID appId) {
        this.isNew = true;
        this.id = UUID.randomUUID();
        this.connection = connectionType.name();
        this.serviceEndpointId = serviceEndpointId;
        this.targetId = targetId;
        this.connecting = connecting;
        this.serviceId = serviceId;
        this.appId = appId;
    }

    public UUID getId() {
        return id;
    }

    @Override
    public boolean isNew() {
        return isNew;
    }

    public String getConnection() {
        return connection;
    }

    public UUID getServiceEndpointId() {
        return serviceEndpointId;
    }

    public UUID getTargetId() {
        return targetId;
    }

    public String getConnecting() {
        return connecting;
    }

    public UUID getAppId() {
        return appId;
    }

    public void setAppId(UUID appId) {
        this.appId = appId;
    }

    public UUID getServiceId() {
        return serviceId;
    }

    public void setServiceId(UUID serviceId) {
        this.serviceId = serviceId;
    }

    @Override
    public String toString() {
        return "Connection{" +
                "id=" + id +
                ", connection='" + connection + '\'' +
                ", serviceEndpointId=" + serviceEndpointId +
                ", targetId=" + targetId +
                ", connecting='" + connecting + '\'' +
                ", appId=" +appId +
                ", serviceId="+serviceId +
            '}';
    }
}
