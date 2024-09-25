package com.hkgapi.hktool.param;

import com.netease.lowcode.core.annotation.NaslStructure;

import java.util.List;

@NaslStructure
public class ForwardData {
    public String domainId;

    public List<String> deviceIds;

    public Boolean configAll;

    public String eventId;

    public String eventType;

    public void setDomainId(String domainId) {
        this.domainId = domainId;
    }

    public String getDomainId() {
        return this.domainId;
    }

    public void setDeviceIds(List<String> deviceIds) {
        this.deviceIds = deviceIds;
    }

    public List<String> getDeviceIds() {
        return this.deviceIds;
    }

    public void setConfigAll(boolean configAll) {
        this.configAll = configAll;
    }

    public Boolean getConfigAll() {
        return this.configAll;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getEventId() {
        return this.eventId;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getEventType() {
        return this.eventType;
    }
}