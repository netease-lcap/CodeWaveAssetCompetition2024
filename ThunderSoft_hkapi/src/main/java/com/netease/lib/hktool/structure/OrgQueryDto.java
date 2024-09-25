package com.netease.lib.hktool.structure;

import com.netease.lowcode.core.annotation.NaslStructure;

import java.util.List;

@NaslStructure
public class OrgQueryDto {

    public List<String> indexCodes;

    public String name;

    public Integer pageNo;

    public Integer pageSize;

    public Integer queryDelete;

    public List<String> siteIndexCodes;

    public String updateTime;

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

    public List<String> getSiteIndexCodes() {
        return siteIndexCodes;
    }

    public void setSiteIndexCodes(List<String> siteIndexCodes) {
        this.siteIndexCodes = siteIndexCodes;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }
}
