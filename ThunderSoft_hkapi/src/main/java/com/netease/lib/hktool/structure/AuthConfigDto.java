package com.netease.lib.hktool.structure;

import com.netease.lowcode.core.annotation.NaslStructure;

import java.util.List;

@NaslStructure
public class AuthConfigDto {
    public List<PersonDataParam> personDatas;

    public List<ResourceInfoParam> resourceInfos;

    public String startTime;

    public String endTime;

    public List<PersonDataParam> getPersonDatas() {
        return personDatas;
    }

    public void setPersonDatas(List<PersonDataParam> personDatas) {
        this.personDatas = personDatas;
    }

    public List<ResourceInfoParam> getResourceInfos() {
        return resourceInfos;
    }

    public void setResourceInfos(List<ResourceInfoParam> resourceInfos) {
        this.resourceInfos = resourceInfos;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }
}
