package com.hkgapi.hktool.param;

import com.netease.lowcode.core.annotation.NaslStructure;

@NaslStructure
public class ForwardDest
{
    public Integer forwardType;

    public Integer pushType;

    public Integer status;

    public Boolean abnormalRetrans;

    public String password;

    public String mqUrl;

    public String topicName;

    public String subUrl;


    public Integer getForwardType() {
        return forwardType;
    }

    public void setForwardType(Integer forwardType) {
        this.forwardType = forwardType;
    }

    public Integer getPushType() {
        return pushType;
    }

    public void setPushType(Integer pushType) {
        this.pushType = pushType;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public boolean isAbnormalRetrans() {
        return abnormalRetrans;
    }

    public void setAbnormalRetrans(boolean abnormalRetrans) {
        this.abnormalRetrans = abnormalRetrans;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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

    public String getSubUrl() {
        return subUrl;
    }

    public void setSubUrl(String subUrl) {
        this.subUrl = subUrl;
    }
}