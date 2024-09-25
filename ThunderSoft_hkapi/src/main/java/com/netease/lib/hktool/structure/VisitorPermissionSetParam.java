package com.netease.lib.hktool.structure;

import com.netease.lowcode.core.annotation.NaslStructure;

import java.util.List;

@NaslStructure
public class VisitorPermissionSetParam {
    public List<String> privilegeGroupIds;

    public String defaultPrivilegeGroupFlag;

    public List<String> getPrivilegeGroupIds() {
        return privilegeGroupIds;
    }

    public void setPrivilegeGroupIds(List<String> privilegeGroupIds) {
        this.privilegeGroupIds = privilegeGroupIds;
    }

    public String getDefaultPrivilegeGroupFlag() {
        return defaultPrivilegeGroupFlag;
    }

    public void setDefaultPrivilegeGroupFlag(String defaultPrivilegeGroupFlag) {
        this.defaultPrivilegeGroupFlag = defaultPrivilegeGroupFlag;
    }
}