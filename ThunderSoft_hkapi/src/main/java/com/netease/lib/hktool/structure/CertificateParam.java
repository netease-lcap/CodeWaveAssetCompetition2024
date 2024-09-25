package com.netease.lib.hktool.structure;

import com.netease.lowcode.core.annotation.NaslStructure;

import java.util.List;

@NaslStructure
public class CertificateParam {
    public String certificationCategoriesType;

    public String credentialLevelType;

    public String certificationCode;

    public String confirmOrganization;

    public String certGrantOrg;

    public String certificationStatus;

    public String firstBeginDate;

    public String validBeginDate;

    public String validEndDate;

    public List<String> certificationType;

    public ExtendParam extend;

    public String getCertificationCategoriesType() {
        return certificationCategoriesType;
    }

    public void setCertificationCategoriesType(String certificationCategoriesType) {
        this.certificationCategoriesType = certificationCategoriesType;
    }

    public String getCredentialLevelType() {
        return credentialLevelType;
    }

    public void setCredentialLevelType(String credentialLevelType) {
        this.credentialLevelType = credentialLevelType;
    }

    public String getCertificationCode() {
        return certificationCode;
    }

    public void setCertificationCode(String certificationCode) {
        this.certificationCode = certificationCode;
    }

    public String getConfirmOrganization() {
        return confirmOrganization;
    }

    public void setConfirmOrganization(String confirmOrganization) {
        this.confirmOrganization = confirmOrganization;
    }

    public String getCertGrantOrg() {
        return certGrantOrg;
    }

    public void setCertGrantOrg(String certGrantOrg) {
        this.certGrantOrg = certGrantOrg;
    }

    public String getCertificationStatus() {
        return certificationStatus;
    }

    public void setCertificationStatus(String certificationStatus) {
        this.certificationStatus = certificationStatus;
    }

    public String getFirstBeginDate() {
        return firstBeginDate;
    }

    public void setFirstBeginDate(String firstBeginDate) {
        this.firstBeginDate = firstBeginDate;
    }

    public String getValidBeginDate() {
        return validBeginDate;
    }

    public void setValidBeginDate(String validBeginDate) {
        this.validBeginDate = validBeginDate;
    }

    public String getValidEndDate() {
        return validEndDate;
    }

    public void setValidEndDate(String validEndDate) {
        this.validEndDate = validEndDate;
    }

    public List<String> getCertificationType() {
        return certificationType;
    }

    public void setCertificationType(List<String> certificationType) {
        this.certificationType = certificationType;
    }

    public ExtendParam getExtend() {
        return extend;
    }

    public void setExtend(ExtendParam extend) {
        this.extend = extend;
    }
}
