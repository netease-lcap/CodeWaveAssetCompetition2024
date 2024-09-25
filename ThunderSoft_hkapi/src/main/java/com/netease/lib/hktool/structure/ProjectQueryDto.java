package com.netease.lib.hktool.structure;

import com.netease.lowcode.core.annotation.NaslStructure;

import java.util.List;

@NaslStructure
public class ProjectQueryDto {

    public List<String> regionIndexCodes;

    public List<String> indexCodes;

    public String name;

    public String updateTime;

    public Integer enableListDisplay;

    public Integer pageNo;

    public Integer pageSize;

    public Integer queryDelete;

    public List<String> getRegionIndexCodes() {
        return regionIndexCodes;
    }

    public void setRegionIndexCodes(List<String> regionIndexCodes) {
        this.regionIndexCodes = regionIndexCodes;
    }

    public List<String> getIndexCodes() {
        return indexCodes;
    }

    public void setIndexCodes(List<String> indexCodes) {
        this.indexCodes = indexCodes;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public Integer getEnableListDisplay() {
        return enableListDisplay;
    }

    public void setEnableListDisplay(Integer enableListDisplay) {
        this.enableListDisplay = enableListDisplay;
    }

    public Integer getPageNo() {
        return pageNo;
    }

    public void setPageNo(Integer pageNo) {
        this.pageNo = pageNo;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Integer getQueryDelete() {
        return queryDelete;
    }

    public void setQueryDelete(Integer queryDelete) {
        this.queryDelete = queryDelete;
    }
}
