package com.fdddf.wechat;

import com.fdddf.wechat.contact.GetUserIdByEmailRequest;
import com.fdddf.wechat.contact.GetUserIdByMobileRequest;
import com.fdddf.wechat.contact.GetUserIdResponse;

public class Contact {

    /**
     * 邮箱获取userid
     * @param request GetUserIdByEmailRequest
     * @param token String token
     * @return
     */
    public static GetUserIdResponse getUserIdByEmail(GetUserIdByEmailRequest request, String token) {
        String url = "https://qyapi.weixin.qq.com/cgi-bin/user/get_userid_by_email?access_token=" + token;
        return WeiXinUtil.request(url, token, request, GetUserIdResponse.class);
    }

    /**
     * 手机号获取userid
     * @param request GetUserIdByMobileRequest
     * @param token String token
     * @return
     */
    public static GetUserIdResponse getUserIdByMobile(GetUserIdByMobileRequest request, String token) {
        String url = "https://qyapi.weixin.qq.com/cgi-bin/user/getuserid?access_token=" + token;
        return WeiXinUtil.request(url, token, request, GetUserIdResponse.class);
    }

}
