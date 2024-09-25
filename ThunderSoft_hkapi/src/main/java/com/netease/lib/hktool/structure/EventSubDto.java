package com.netease.lib.hktool.structure;

import com.netease.lowcode.core.annotation.NaslStructure;

import java.util.List;

@NaslStructure
public class EventSubDto {
    public List<Integer> eventTypes;

    public String eventDest;

    public Integer subType;

    public List<Integer> eventLvl;

    public List<Integer> getEventTypes() {
        return eventTypes;
    }

    public void setEventTypes(List<Integer> eventTypes) {
        this.eventTypes = eventTypes;
    }

    public String getEventDest() {
        return eventDest;
    }

    public void setEventDest(String eventDest) {
        this.eventDest = eventDest;
    }

    public Integer getSubType() {
        return subType;
    }

    public void setSubType(Integer subType) {
        this.subType = subType;
    }

    public List<Integer> getEventLvl() {
        return eventLvl;
    }

    public void setEventLvl(List<Integer> eventLvl) {
        this.eventLvl = eventLvl;
    }
}
