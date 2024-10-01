package com.fdddf.wechat;

import com.fdddf.wechat.email.*;

public class QueryEmail {
    /**
     * 获取应用的收件箱邮件列表
     * @param request EmailListRequest
     * @param token 调用接口凭证
     * @return
     */
    public static EmailListResponse getInboxEmails(EmailListRequest request, String token) {
        String url = "https://qyapi.weixin.qq.com/cgi-bin/exmail/app/get_mail_list?access_token=" + token;
        return WeiXinUtil.request(url, token, request, EmailListResponse.class);
    }

    /**
     * 获取邮件内容 eml数据
     * @param request EmailReadRequest
     * @param token 调用接口凭证
     * @return
     */
    public static EmailReadResponse readEmail(EmailReadRequest request, String token) {
        String url = "https://qyapi.weixin.qq.com/cgi-bin/exmail/app/read_mail?access_token=" + token;
        return WeiXinUtil.request(url, token, request, EmailReadResponse.class);
    }

    /**
     * 获取指定成员邮箱当前未读邮件数量
     * @param request EmailUnreadRequest
     * @param token 调用接口凭证
     * @return
     */
    public static EmailUnreadResponse getUnreadEmails(EmailUnreadRequest request, String token) {
        String url = "https://qyapi.weixin.qq.com/cgi-bin/exmail/mail/get_newcount?access_token=" + token;
        return WeiXinUtil.request(url, token, request, EmailUnreadResponse.class);
    }
    
}
