package me.sonam.catlog.repo.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ConnectionForm {
    private UUID appId;
    private String connecting;

    private List<UUID> targetIdList = new ArrayList<>();


    public ConnectionForm() {
    }

    public UUID getAppId() {
        return appId;
    }

    public List<UUID> getTargetIdList() {
        return targetIdList;
    }

    public String getConnecting() {
        return connecting;
    }

    public void setAppId(UUID appId) {
        this.appId = appId;
    }

    public void setConnecting(String connecting) {
        this.connecting = connecting;
    }

    public void setTargetIdList(List<UUID> targetIdList) {
        this.targetIdList = targetIdList;
    }

    @Override
    public String toString() {
        return "ConnectionForm{" +
                "appId=" + appId +
                ", targetIdList=" + targetIdList +
                '}';
    }
}
