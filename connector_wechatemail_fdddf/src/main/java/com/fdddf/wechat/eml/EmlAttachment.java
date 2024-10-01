package com.fdddf.wechat.eml;


import com.netease.lowcode.core.annotation.NaslStructure;

import java.util.Base64;

@NaslStructure
public class EmlAttachment {
    /**
     * 文件名
     */
    public String fileName;
    /**
     * 文件类型
     */
    public String mimeType;
    /**
     * base64编码的文件内容
     */
    public String content;

    public EmlAttachment() {}

    public EmlAttachment(String fileName, String mimeType, byte[] content) {
        this.fileName = fileName;
        this.mimeType = mimeType;
        this.content = Base64.getEncoder().encodeToString(content);
    }

    @Override
    public String toString() {
        return "EmlAttachment{" +
                "fileName='" + fileName + '\'' +
                ", mimeType='" + mimeType + '\'' +
                '}';
    }
}
