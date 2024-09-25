package com.netease.lib.hktool.structure;

import com.netease.lowcode.core.annotation.NaslStructure;

import java.util.List;

/**
 * 查询区域
 */
@NaslStructure
public class RegionQueryDto {
    public List<String> indexCodes;

    public String name;

    public List<String> parentIndexCodes;

    public Integer queryDelete;

    public String updateTime;

    //当前页码
    public Integer pageNo;
    //每页记录总数
    public Integer pageSize;

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

    public List<String> getParentIndexCodes() {
        return parentIndexCodes;
    }

    public void setParentIndexCodes(List<String> parentIndexCodes) {
        this.parentIndexCodes = parentIndexCodes;
    }

    public Integer getQueryDelete() {
        return queryDelete;
    }

    public void setQueryDelete(Integer queryDelete) {
        this.queryDelete = queryDelete;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
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
}