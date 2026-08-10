package com.fdddf.wechat.eml;

import com.netease.lowcode.core.annotation.NaslStructure;

import java.time.LocalDate;
import java.util.List;

@NaslStructure
public class ParsedEmail {
    /**
     * 邮件主题
     */
    public String subject;
    /**
     * 发件人
     */
    public String from;
    /**
     * 收件人
     */
    public List<String> toRecipients;
    /**
     * 发送时间
     */
    public LocalDate sentDate;
    /**
     * 邮件内容
     */
    public String body;
    /**
     * 附件列表
     */
    public List<EmlAttachment> attachments;

    public List<EmlAttachment> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<EmlAttachment> attachments) {
        this.attachments = attachments;
    }


    public ParsedEmail() {}

    @Override
    public String toString() {
        return "ParsedEmail{" +
                "subject='" + subject + '\'' +
                ", from='" + from + '\'' +
                ", toRecipients=" + toRecipients +
                ", sentDate=" + sentDate +
                ", body='" + body + '\'' +
                ", attachments=" + attachments +
                '}';
    }
}
