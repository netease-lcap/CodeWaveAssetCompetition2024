package com.hkgapi.hktool.structure;

import com.hkgapi.hktool.param.ForwardData;
import com.hkgapi.hktool.param.ForwardDest;
import com.netease.lowcode.core.annotation.NaslStructure;

import java.util.List;

@NaslStructure
public class AddRuleDto
{
    public List<ForwardDest> forwardDest;

    public List<ForwardData> forwardData;

    public String name;

    public String remark;

    public String type;

    public String mqUrl;

    public String topicName;

    public Integer status;

    public String startTime;

    public String endTime;

    public String id;


    public List<ForwardDest> getForwardDest() {
        return forwardDest;
    }

    public void setForwardDest(List<ForwardDest> forwardDest) {
        this.forwardDest = forwardDest;
    }

    public List<ForwardData> getForwardData() {
        return forwardData;
    }

    public void setForwardData(List<ForwardData> forwardData) {
        this.forwardData = forwardData;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMqUrl() {
        return mqUrl;
    }

    public void setMqUrl(String mqUrl) {
        this.mqUrl = mqUrl;
    }

    public String getTopicName() {
        return topicName;
    }

    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}