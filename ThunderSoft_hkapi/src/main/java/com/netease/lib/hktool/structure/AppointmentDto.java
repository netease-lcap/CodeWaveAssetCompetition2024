package com.netease.lib.hktool.structure;

import com.netease.lowcode.core.annotation.NaslStructure;

@NaslStructure
public class AppointmentDto {

    public String receptionistId;

    public String visitorName;

    public String phoneNo;

    public String visitStartTimeBegin;

    public String visitStartTimeEnd;

    public String visitEndTimeBegin;

    public String visitEndTimeEnd;

    public Integer pageNo;

    public Integer pageSize;

    public String verificationCode;

    public Integer visitorStatus;

    public String QRCode;

    public String orderId;

    public String getReceptionistId() {
        return receptionistId;
    }

    public void setReceptionistId(String receptionistId) {
        this.receptionistId = receptionistId;
    }

    public String getVisitorName() {
        return visitorName;
    }

    public void setVisitorName(String visitorName) {
        this.visitorName = visitorName;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    public String getVisitStartTimeBegin() {
        return visitStartTimeBegin;
    }

    public void setVisitStartTimeBegin(String visitStartTimeBegin) {
        this.visitStartTimeBegin = visitStartTimeBegin;
    }

    public String getVisitStartTimeEnd() {
        return visitStartTimeEnd;
    }

    public void setVisitStartTimeEnd(String visitStartTimeEnd) {
        this.visitStartTimeEnd = visitStartTimeEnd;
    }

    public String getVisitEndTimeBegin() {
        return visitEndTimeBegin;
    }

    public void setVisitEndTimeBegin(String visitEndTimeBegin) {
        this.visitEndTimeBegin = visitEndTimeBegin;
    }

    public String getVisitEndTimeEnd() {
        return visitEndTimeEnd;
    }

    public void setVisitEndTimeEnd(String visitEndTimeEnd) {
        this.visitEndTimeEnd = visitEndTimeEnd;
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

    public String getVerificationCode() {
        return verificationCode;
    }

    public void setVerificationCode(String verificationCode) {
        this.verificationCode = verificationCode;
    }

    public Integer getVisitorStatus() {
        return visitorStatus;
    }

    public void setVisitorStatus(Integer visitorStatus) {
        this.visitorStatus = visitorStatus;
    }

    public String getQRCode() {
        return QRCode;
    }

    public void setQRCode(String QRCode) {
        this.QRCode = QRCode;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }
}
