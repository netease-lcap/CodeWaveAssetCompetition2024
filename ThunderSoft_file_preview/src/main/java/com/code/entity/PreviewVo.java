package com.code.entity;

import com.netease.lowcode.core.annotation.NaslStructure;

/**
 * @author zhouzz
 */
@NaslStructure
public class PreviewVo {

    public  String previewUrl;

    public String objectKey;

    public String fileName;


    public String getPreviewUrl() {
        return previewUrl;
    }

    public void setPreviewUrl(String previewUrl) {
        this.previewUrl = previewUrl;
    }

    public String getObjectKey() {
        return objectKey;
    }

    public void setObjectKey(String objectKey) {
        this.objectKey = objectKey;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }


}
