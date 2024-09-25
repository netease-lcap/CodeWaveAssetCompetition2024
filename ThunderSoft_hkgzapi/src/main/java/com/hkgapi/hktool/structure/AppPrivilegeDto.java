package com.hkgapi.hktool.structure;

import com.hkgapi.hktool.param.AppPrivilege;
import com.netease.lowcode.core.annotation.NaslStructure;

import java.util.List;

@NaslStructure
public class AppPrivilegeDto {

    public Integer pageSize;

    public Integer pageNo;

    public List<AppPrivilege> array;

    public void setPageSize(int pageSize){
        this.pageSize = pageSize;
    }
    public Integer getPageSize(){
        return this.pageSize;
    }
    public void setPageNo(int pageNo){
        this.pageNo = pageNo;
    }
    public Integer getPageNo(){
        return this.pageNo;
    }
    public void setArray(List<AppPrivilege> array){
        this.array = array;
    }
    public List<AppPrivilege> getArray(){
        return this.array;
    }
}
