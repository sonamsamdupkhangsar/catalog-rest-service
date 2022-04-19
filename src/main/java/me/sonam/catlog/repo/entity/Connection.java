package me.sonam.catlog.repo.entity;

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

    private UUID appIdSource;

    private UUID targetId;

    private String connecting;

    @Transient
    private boolean isNew;

    public Connection() {
    }

    public Connection(ConnectionType connectionType, String connecting, UUID appIdSource, UUID targetId) {
        this.isNew = true;
        this.id = UUID.randomUUID();
        this.connection = connectionType.name();
        this.appIdSource = appIdSource;
        this.targetId = targetId;
        this.connecting = connecting;
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

    public UUID getAppIdSource() {
        return appIdSource;
    }

    public UUID getTargetId() {
        return targetId;
    }

    public String getConnecting() {
        return connecting;
    }

    @Override
    public String toString() {
        return "Connection{" +
                "id=" + id +
                ", connection='" + connection + '\'' +
                ", appIdSource=" + appIdSource +
                ", targetId=" + targetId +
                ", connecting='" + connecting + '\'' +
                '}';
    }
}
