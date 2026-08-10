package com.fdddf.wechat;

import com.fdddf.wechat.contact.GetUserIdByEmailRequest;
import com.fdddf.wechat.contact.GetUserIdByMobileRequest;
import com.fdddf.wechat.contact.GetUserIdResponse;
import com.fdddf.wechat.email.*;
import com.fdddf.wechat.eml.ParsedEmail;
import com.netease.lowcode.core.annotation.NaslConnector;

@NaslConnector(connectorKind = "weixinEmailConnector")
public class WeixinEmailConnector {
    /**
     * 企业微信企业id
     */
    private String cropid;
    /**
     * 企业微信应用密钥
     */
    private String secret;

    @NaslConnector.Creator
    public WeixinEmailConnector initBean(String cropid, String secret)
    {
        WeixinEmailConnector connector = new WeixinEmailConnector();
        connector.cropid = cropid;
        connector.secret = secret;
        return connector;
    }

    @NaslConnector.Tester
    public Boolean test(String cropid, String secret) {
        if (null != cropid && !cropid.isEmpty() && null != secret && !secret.isEmpty()) {
            String token = WeiXinUtil.getAccessToken(cropid, secret);
            return null != token && !token.isEmpty();
        }
        return false;
    }

    /////////logics////////

    /**
     * 邮箱获取userid
     * @param request GetUserIdByEmailRequest
     * @param token String token
     * @return
     */
    @NaslConnector.Logic
    public static GetUserIdResponse getUserIdByEmail(GetUserIdByEmailRequest request, String token) {
        return Contact.getUserIdByEmail(request, token);
    }

    /**
     * 手机号获取userid
     * @param request GetUserIdByMobileRequest
     * @param token String token
     * @return
     */
    @NaslConnector.Logic
    public static GetUserIdResponse getUserIdByMobile(GetUserIdByMobileRequest request, String token) {
        return Contact.getUserIdByMobile(request, token);
    }

    /**
     * 发送日程邮件
     * @param request EmailScheduleRequest
     * @param token 接口凭证
     * @return
     */
    @NaslConnector.Logic
    public static WeChatResponse sendScheduleEmail(EmailScheduleRequest request, String token) {
        return Compose.sendScheduleEmail(request, token);
    }

    /**
     * 发送会议邮件
     * @param request EmailMeetingRequest
     * @param token 接口凭证
     * @return
     */
    @NaslConnector.Logic
    public static WeChatResponse sendMeetingEmail(EmailMeetingRequest request, String token) {
        return Compose.sendMeetingEmail(request, token);
    }

    /**
     * 发送普通邮件,支持附件能力
     * @param request EmailRequest
     * @param token 接口凭证
     * @return
     */
    @NaslConnector.Logic
    public static WeChatResponse sendNormalEmail(EmailRequest request, String token) {
        return Compose.sendNormalEmail(request, token);
    }

    /**
     * Parse an EML file and return a ParsedEmail object
     * @param emlContent The content of the EML file
     * @return ParsedEmail
     */
    @NaslConnector.Logic
    public static ParsedEmail parseEml(String emlContent) {
        return EmlUtil.parseEml(emlContent);
    }

    /**
     * 获取应用的收件箱邮件列表
     * @param request EmailListRequest
     * @param token 调用接口凭证
     * @return
     */
    @NaslConnector.Logic
    public static EmailListResponse getInboxEmails(EmailListRequest request, String token) {
        return QueryEmail.getInboxEmails(request, token);
    }

    /**
     * 获取邮件内容 eml数据
     * @param request EmailReadRequest
     * @param token 调用接口凭证
     * @return
     */
    @NaslConnector.Logic
    public static EmailReadResponse readEmail(EmailReadRequest request, String token) {
        return QueryEmail.readEmail(request, token);
    }

    /**
     * 获取指定成员邮箱当前未读邮件数量
     * @param request EmailUnreadRequest
     * @param token 调用接口凭证
     * @return
     */
    @NaslConnector.Logic
    public static EmailUnreadResponse getUnreadEmails(EmailUnreadRequest request, String token) {
        return QueryEmail.getUnreadEmails(request, token);
    }

    /**
     * 获取access_token
     * @return
     */
    @NaslConnector.Logic
    public String getAccessToken() {
        return WeiXinUtil.getAccessToken(cropid, secret);
    }

}
