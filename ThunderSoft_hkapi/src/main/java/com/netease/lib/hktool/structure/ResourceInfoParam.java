package com.netease.lib.hktool.structure;

import com.netease.lowcode.core.annotation.NaslStructure;

import java.util.List;

/**
 * 设备通道对象列表
 */
@NaslStructure
public class ResourceInfoParam
{
    public String resourceIndexCode;

    public String resourceType;

    public List<Integer> channelNos;

    public String getResourceIndexCode() {
        return resourceIndexCode;
    }

    public void setResourceIndexCode(String resourceIndexCode) {
        this.resourceIndexCode = resourceIndexCode;
    }

    public String getResourceType() {
        return resourceType;
    }

    public void setResourceType(String resourceType) {
        this.resourceType = resourceType;
    }

    public List<Integer> getChannelNos() {
        return channelNos;
    }

    public void setChannelNos(List<Integer> channelNos) {
        this.channelNos = channelNos;
    }
}