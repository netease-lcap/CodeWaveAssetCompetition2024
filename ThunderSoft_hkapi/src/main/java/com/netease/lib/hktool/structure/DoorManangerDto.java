package com.netease.lib.hktool.structure;

import com.netease.lowcode.core.annotation.NaslStructure;

import java.util.List;

@NaslStructure
public class DoorManangerDto {
    public String name;

    public List<String> regionIndexCodes;

    public Boolean isSubRegion;

    public List<String> authCodes;

    public List<ExpressionParam> expressions;

    public String orderBy;

    public String orderType;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getRegionIndexCodes() {
        return regionIndexCodes;
    }

    public void setRegionIndexCodes(List<String> regionIndexCodes) {
        this.regionIndexCodes = regionIndexCodes;
    }

    public Boolean getSubRegion() {
        return isSubRegion;
    }

    public void setSubRegion(Boolean subRegion) {
        isSubRegion = subRegion;
    }

    public List<String> getAuthCodes() {
        return authCodes;
    }

    public void setAuthCodes(List<String> authCodes) {
        this.authCodes = authCodes;
    }

    public List<ExpressionParam> getExpressions() {
        return expressions;
    }

    public void setExpressions(List<ExpressionParam> expressions) {
        this.expressions = expressions;
    }

    public String getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(String orderBy) {
        this.orderBy = orderBy;
    }

    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }
}