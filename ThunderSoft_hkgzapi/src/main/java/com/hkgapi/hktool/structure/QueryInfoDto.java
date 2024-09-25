package com.hkgapi.hktool.structure;

import com.hkgapi.hktool.param.QueryInfo;
import com.netease.lowcode.core.annotation.NaslStructure;

import java.util.List;
@NaslStructure
public class QueryInfoDto {
    public Integer pageSize;

    public Integer pageNo;

    public List<QueryInfo> array;

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

    public List<QueryInfo> getArray() {
        return array;
    }

    public void setArray(List<QueryInfo> array) {
        this.array = array;
    }
}