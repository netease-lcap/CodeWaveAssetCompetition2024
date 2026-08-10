package com.fdddf.wechat.email;

import com.fdddf.wechat.FileUtil;
import com.fdddf.wechat.implement.IWeixinRequest;
import com.netease.lowcode.core.annotation.NaslStructure;

import java.util.List;

@NaslStructure
public class EmailRequest implements IWeixinRequest {
    /**
     * 收件人
     */
    public EmailRecipient to;
    /**
     * 抄送人
     */
    public EmailRecipient cc;
    /**
     * 密送人
     */
    public EmailRecipient bcc;
    /**
     * 主题
     */
    public String subject;
    /**
     * 正文
     */
    public String content;
    /**
     * 附件列表, 可使用 fileUrls
     */
    public List<EmailAttachment> attachment_list;
    /**
     * 是否开启id转译
     */
    public Integer enable_id_trans;

    /**
     * 附件地址列表
     */
    public List<String> fileUrls;

    public EmailRequest() {}

    public EmailRequest(EmailRecipient to, EmailRecipient cc, EmailRecipient bcc, String subject, String content,
            List<EmailAttachment> attachment_list, int enable_id_trans, List<String> fileUrls) {
        this.to = to;
        this.cc = cc;
        this.bcc = bcc;
        this.subject = subject;
        this.content = content;
        this.attachment_list = attachment_list;
        this.enable_id_trans = enable_id_trans;
        this.fileUrls = fileUrls;
    }

    public void organizeAttachment() {
        if (attachment_list == null) {
            attachment_list = new java.util.ArrayList<>();
        }
        if (fileUrls == null) {
            return;
        }
        for (String fileUrl : fileUrls) {
            String fileName = FileUtil.getFileName(fileUrl);
            String content = FileUtil.fileBase64Content(fileUrl);
            EmailAttachment attachment = new EmailAttachment(fileName, content);
            attachment_list.add(attachment);
        }
    }
}
