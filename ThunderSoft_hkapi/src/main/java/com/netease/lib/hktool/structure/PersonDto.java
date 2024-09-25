package com.netease.lib.hktool.structure;

import com.netease.lowcode.core.annotation.NaslStructure;

import java.util.List;

@NaslStructure
public class PersonDto {

    public String certificateNo;

    public Integer clientId;

    public Integer gender;

    public String parentIndexCode;

    public String personName;

    public String indexCode;

    public String nation;

    public String birthday;

    public String address;

    public String birthdayPlace;

    public String grantOrg;

    public String hasBadMedicalHistory;

    public String phoneNo;

    public String urgentLinkman;

    public String urgentLinkmanPhone;

    public String workerType;

    public String workTypeName;

    public String entryTime;

    public String exitTime;

    public String insuranceFlag;

    public String signContractFlag;

    public String contractNumber;

    public String contractPeriodType;

    public String towerCraneDriverFlag;

    public String liftCraneDriverFlag;

    public String cultureLevelType;

    public String politicsType;

    public String contractStartTime;

    public String contractEndTime;

    public Integer dayWorkTime;

    public List<CertificateParam> certificate;

    public ExtendParam extend;

    public String getCertificateNo() {
        return certificateNo;
    }

    public void setCertificateNo(String certificateNo) {
        this.certificateNo = certificateNo;
    }

    public Integer getClientId() {
        return clientId;
    }

    public void setClientId(Integer clientId) {
        this.clientId = clientId;
    }

    public Integer getGender() {
        return gender;
    }

    public void setGender(Integer gender) {
        this.gender = gender;
    }

    public String getParentIndexCode() {
        return parentIndexCode;
    }

    public void setParentIndexCode(String parentIndexCode) {
        this.parentIndexCode = parentIndexCode;
    }

    public String getPersonName() {
        return personName;
    }

    public void setPersonName(String personName) {
        this.personName = personName;
    }

    public String getIndexCode() {
        return indexCode;
    }

    public void setIndexCode(String indexCode) {
        this.indexCode = indexCode;
    }

    public String getNation() {
        return nation;
    }

    public void setNation(String nation) {
        this.nation = nation;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getBirthdayPlace() {
        return birthdayPlace;
    }

    public void setBirthdayPlace(String birthdayPlace) {
        this.birthdayPlace = birthdayPlace;
    }

    public String getGrantOrg() {
        return grantOrg;
    }

    public void setGrantOrg(String grantOrg) {
        this.grantOrg = grantOrg;
    }

    public String getHasBadMedicalHistory() {
        return hasBadMedicalHistory;
    }

    public void setHasBadMedicalHistory(String hasBadMedicalHistory) {
        this.hasBadMedicalHistory = hasBadMedicalHistory;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    public String getUrgentLinkman() {
        return urgentLinkman;
    }

    public void setUrgentLinkman(String urgentLinkman) {
        this.urgentLinkman = urgentLinkman;
    }

    public String getUrgentLinkmanPhone() {
        return urgentLinkmanPhone;
    }

    public void setUrgentLinkmanPhone(String urgentLinkmanPhone) {
        this.urgentLinkmanPhone = urgentLinkmanPhone;
    }

    public String getWorkerType() {
        return workerType;
    }

    public void setWorkerType(String workerType) {
        this.workerType = workerType;
    }

    public String getWorkTypeName() {
        return workTypeName;
    }

    public void setWorkTypeName(String workTypeName) {
        this.workTypeName = workTypeName;
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

    public String getInsuranceFlag() {
        return insuranceFlag;
    }

    public void setInsuranceFlag(String insuranceFlag) {
        this.insuranceFlag = insuranceFlag;
    }

    public String getSignContractFlag() {
        return signContractFlag;
    }

    public void setSignContractFlag(String signContractFlag) {
        this.signContractFlag = signContractFlag;
    }

    public String getContractNumber() {
        return contractNumber;
    }

    public void setContractNumber(String contractNumber) {
        this.contractNumber = contractNumber;
    }

    public String getContractPeriodType() {
        return contractPeriodType;
    }

    public void setContractPeriodType(String contractPeriodType) {
        this.contractPeriodType = contractPeriodType;
    }

    public String getTowerCraneDriverFlag() {
        return towerCraneDriverFlag;
    }

    public void setTowerCraneDriverFlag(String towerCraneDriverFlag) {
        this.towerCraneDriverFlag = towerCraneDriverFlag;
    }

    public String getLiftCraneDriverFlag() {
        return liftCraneDriverFlag;
    }

    public void setLiftCraneDriverFlag(String liftCraneDriverFlag) {
        this.liftCraneDriverFlag = liftCraneDriverFlag;
    }

    public String getCultureLevelType() {
        return cultureLevelType;
    }

    public void setCultureLevelType(String cultureLevelType) {
        this.cultureLevelType = cultureLevelType;
    }

    public String getPoliticsType() {
        return politicsType;
    }

    public void setPoliticsType(String politicsType) {
        this.politicsType = politicsType;
    }

    public String getContractStartTime() {
        return contractStartTime;
    }

    public void setContractStartTime(String contractStartTime) {
        this.contractStartTime = contractStartTime;
    }

    public String getContractEndTime() {
        return contractEndTime;
    }

    public void setContractEndTime(String contractEndTime) {
        this.contractEndTime = contractEndTime;
    }

    public Integer getDayWorkTime() {
        return dayWorkTime;
    }

    public void setDayWorkTime(Integer dayWorkTime) {
        this.dayWorkTime = dayWorkTime;
    }

    public List<CertificateParam> getCertificate() {
        return certificate;
    }

    public void setCertificate(List<CertificateParam> certificate) {
        this.certificate = certificate;
    }

    public ExtendParam getExtend() {
        return extend;
    }

    public void setExtend(ExtendParam extend) {
        this.extend = extend;
    }
}
