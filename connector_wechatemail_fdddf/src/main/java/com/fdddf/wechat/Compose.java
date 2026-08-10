package com.fdddf.wechat;

import com.fdddf.wechat.email.EmailMeetingRequest;
import com.fdddf.wechat.email.EmailRequest;
import com.fdddf.wechat.email.EmailScheduleRequest;

public class Compose {
    public final static String emailUrl = "https://qyapi.weixin.qq.com/cgi-bin/exmail/app/compose_send?access_token=";
    
    /**
     * 发送日程邮件
     * @param request EmailScheduleRequest
     * @param token 接口凭证
     * @return
     */
    public static WeChatResponse sendScheduleEmail(EmailScheduleRequest request, String token) {
        String url = emailUrl + token;
        return WeiXinUtil.request(url, token, request, WeChatResponse.class);
    }

    /**
     * 发送会议邮件
     * @param request EmailMeetingRequest
     * @param token 接口凭证
     * @return
     */
    public static WeChatResponse sendMeetingEmail(EmailMeetingRequest request, String token) {
        String url = emailUrl + token;
        return WeiXinUtil.request(url, token, request, WeChatResponse.class);
    }

    /**
     * 发送普通邮件,支持附件能力
     * @param request EmailRequest
     * @param token 接口凭证
     * @return
     */
    public static WeChatResponse sendNormalEmail(EmailRequest request, String token) {
        String url = emailUrl + token;
        request.organizeAttachment();
        return WeiXinUtil.request(url, token, request, WeChatResponse.class);
    }

}
