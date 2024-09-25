package com.netease.lib.hktool.structure;

import com.netease.lowcode.core.annotation.NaslStructure;

@NaslStructure
public class OrgDto {
    public Integer clientId;

    public String entryTime;

    public String exitTime;

    public String parentIndexCode;

    public String unitCode;

    public String unitName;

    public String indexCode;

    public String legalRepresentative;

    public String linkmanName;

    public String linkmanPhone;

    public String areaCodes;

    public String unitType;

    public String projectManagerPhone;

    public ExtendParam extend;

    public Integer getClientId() {
        return clientId;
    }

    public void setClientId(Integer clientId) {
        this.clientId = clientId;
    }

    public String getEntryTime() {
        return entryTime;
    }

    public void setEntryTime(String entryTime) {
        this.entryTime = entryTime;
    }

    public String getExitTime() {
        return exitTime;
    }

    public void setExitTime(String exitTime) {
        this.exitTime = exitTime;
    }

    public String getParentIndexCode() {
        return parentIndexCode;
    }

    public void setParentIndexCode(String parentIndexCode) {
        this.parentIndexCode = parentIndexCode;
    }

    public String getUnitCode() {
        return unitCode;
    }

    public void setUnitCode(String unitCode) {
        this.unitCode = unitCode;
    }

    public String getUnitName() {
        return unitName;
    }

    public void setUnitName(String unitName) {
        this.unitName = unitName;
    }

    public String getIndexCode() {
        return indexCode;
    }

    public void setIndexCode(String indexCode) {
        this.indexCode = indexCode;
    }

    public String getLegalRepresentative() {
        return legalRepresentative;
    }

    public void setLegalRepresentative(String legalRepresentative) {
        this.legalRepresentative = legalRepresentative;
    }

    public String getLinkmanName() {
        return linkmanName;
    }

    public void setLinkmanName(String linkmanName) {
        this.linkmanName = linkmanName;
    }

    public String getLinkmanPhone() {
        return linkmanPhone;
    }

    public void setLinkmanPhone(String linkmanPhone) {
        this.linkmanPhone = linkmanPhone;
    }

    public String getAreaCodes() {
        return areaCodes;
    }

    public void setAreaCodes(String areaCodes) {
        this.areaCodes = areaCodes;
    }

    public String getUnitType() {
        return unitType;
    }

    public void setUnitType(String unitType) {
        this.unitType = unitType;
    }

    public String getProjectManagerPhone() {
        return projectManagerPhone;
    }

    public void setProjectManagerPhone(String projectManagerPhone) {
        this.projectManagerPhone = projectManagerPhone;
    }

    public ExtendParam getExtend() {
        return extend;
    }

    public void setExtend(ExtendParam extend) {
        this.extend = extend;
    }
}
