package com.fdddf.wechat.email;

import com.netease.lowcode.core.annotation.NaslStructure;

@NaslStructure
public class EmailAttachment {
    /**
     * 文件名
     */
    public String file_name;
    /**
     * 文件内容（base64编码)
     */
    public String content;

    public EmailAttachment() {}

    public EmailAttachment(String file_name, String content) {
        this.file_name = file_name;
        this.content = content;
    }
}
