package com.netease.lib.hktool.structure;

import com.netease.lowcode.core.annotation.NaslStructure;

/**
 * 查询区域
 */
@NaslStructure
public class RegionDto {
    public String parentIndexCode;

    public Integer clientId;

    public String indexCode;

    public String name;

    public String description;

    public String getParentIndexCode() {
        return parentIndexCode;
    }

    public void setParentIndexCode(String parentIndexCode) {
        this.parentIndexCode = parentIndexCode;
    }

    public Integer getClientId() {
        return clientId;
    }

    public void setClientId(Integer clientId) {
        this.clientId = clientId;
    }

    public String getIndexCode() {
        return indexCode;
    }

    public void setIndexCode(String indexCode) {
        this.indexCode = indexCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}