package com.hkgapi.hktool.structure;

import com.netease.lowcode.core.annotation.NaslStructure;

@NaslStructure
public class EventInfoDto {
    public String deviceCode;

    public String deviceChannelCode;

    public String eventType;

    public String functionDomain;

    public Integer beginTime;

    public Integer endTime;

    public String fullTextKeyWord;

    public String order;

    public Integer pageSize;

    public Integer pageNo;

    public String getDeviceCode() {
        return deviceCode;
    }

    public void setDeviceCode(String deviceCode) {
        this.deviceCode = deviceCode;
    }

    public String getDeviceChannelCode() {
        return deviceChannelCode;
    }

    public void setDeviceChannelCode(String deviceChannelCode) {
        this.deviceChannelCode = deviceChannelCode;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getFunctionDomain() {
        return functionDomain;
    }

    public void setFunctionDomain(String functionDomain) {
        this.functionDomain = functionDomain;
    }

    public Integer getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(Integer beginTime) {
        this.beginTime = beginTime;
    }

    public Integer getEndTime() {
        return endTime;
    }

    public void setEndTime(Integer endTime) {
        this.endTime = endTime;
    }

    public String getFullTextKeyWord() {
        return fullTextKeyWord;
    }

    public void setFullTextKeyWord(String fullTextKeyWord) {
        this.fullTextKeyWord = fullTextKeyWord;
    }

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Integer getPageNo() {
        return pageNo;
    }

    public void setPageNo(Integer pageNo) {
        this.pageNo = pageNo;
    }
}
