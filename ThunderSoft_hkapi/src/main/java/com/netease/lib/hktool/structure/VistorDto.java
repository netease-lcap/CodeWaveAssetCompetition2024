package com.netease.lib.hktool.structure;

import com.netease.lowcode.core.annotation.NaslStructure;

@NaslStructure
public class VistorDto {
    public String receptionistId;

    public String visitStartTime;

    public String visitEndTime;

    public String visitPurpose;

    public Integer personNum;

    public VisitorInfoParam visitorInfo;

    public VisitorPermissionSetParam visitorPermissionSet;

    public String getReceptionistId() {
        return receptionistId;
    }

    public void setReceptionistId(String receptionistId) {
        this.receptionistId = receptionistId;
    }

    public String getVisitStartTime() {
        return visitStartTime;
    }

    public void setVisitStartTime(String visitStartTime) {
        this.visitStartTime = visitStartTime;
    }

    public String getVisitEndTime() {
        return visitEndTime;
    }

    public void setVisitEndTime(String visitEndTime) {
        this.visitEndTime = visitEndTime;
    }

    public String getVisitPurpose() {
        return visitPurpose;
    }

    public void setVisitPurpose(String visitPurpose) {
        this.visitPurpose = visitPurpose;
    }

    public Integer getPersonNum() {
        return personNum;
    }

    public void setPersonNum(Integer personNum) {
        this.personNum = personNum;
    }

    public VisitorInfoParam getVisitorInfo() {
        return visitorInfo;
    }

    public void setVisitorInfo(VisitorInfoParam visitorInfo) {
        this.visitorInfo = visitorInfo;
    }

    public VisitorPermissionSetParam getVisitorPermissionSet() {
        return visitorPermissionSet;
    }

    public void setVisitorPermissionSet(VisitorPermissionSetParam visitorPermissionSet) {
        this.visitorPermissionSet = visitorPermissionSet;
    }
}