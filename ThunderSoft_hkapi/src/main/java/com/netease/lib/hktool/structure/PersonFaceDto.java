package com.netease.lib.hktool.structure;

import com.netease.lowcode.core.annotation.NaslStructure;

@NaslStructure
public class PersonFaceDto {
    public String indexCode;

    public String facePhotoBase64Data;

    public Integer operationType;

    public String getIndexCode() {
        return indexCode;
    }

    public void setIndexCode(String indexCode) {
        this.indexCode = indexCode;
    }

    public String getFacePhotoBase64Data() {
        return facePhotoBase64Data;
    }

    public void setFacePhotoBase64Data(String facePhotoBase64Data) {
        this.facePhotoBase64Data = facePhotoBase64Data;
    }

    public Integer getOperationType() {
        return operationType;
    }

    public void setOperationType(Integer operationType) {
        this.operationType = operationType;
    }
}
