package com.fdddf.wechat;

import com.alibaba.fastjson.JSONObject;
import com.fdddf.wechat.implement.IWeixinRequest;
import com.fdddf.wechat.implement.IWeixinResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class WeiXinUtil {
    private static final Logger LCAP_LOGGER = LoggerFactory.getLogger("LCAP_EXTENSION_LOGGER");

    /**
     * 获取access_token
     * @param cropid 企业微信企业id
     * @param secret 企业微信应用密钥
     * @return
     */
    public static String getAccessToken(String cropid, String secret) {

        Map<String, String> params = new HashMap<>();
        params.put("corpid", cropid);
        params.put("corpsecret", secret);
        String token = "";
        try {
            String url = "https://qyapi.weixin.qq.com/cgi-bin/gettoken";
            String responseBody = HttpUtils.httpGetMethod(url, params);
            JSONObject tokenBody = JSONObject.parseObject(responseBody);
            token = tokenBody.getString("access_token");
        } catch (IOException e) {
            LCAP_LOGGER.error("Error while sending HTTP request: {}", e.getMessage());
            throw new RuntimeException("Error while sending HTTP request");
        }
        return token;
    }

    public static <T extends IWeixinResponse> T request(String url, String token, IWeixinRequest request, Class<T> responseClass) {
        JSONObject jsonReq = (JSONObject) JSONObject.toJSON(request);
        String jsonBody = jsonReq.toJSONString();
    
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
    
        try {
            okhttp3.Response response = HttpUtils.httpPostResponse(url, headers, jsonBody);
            assert response.body() != null;
            String responseString = response.body().string();
            
            // Parse the response as the class passed in the parameter
            return JSONObject.parseObject(responseString, responseClass);
    
        } catch (IOException e) {
            LCAP_LOGGER.error("Error while sending HTTP request: {}", e.getMessage());
        }
        return null;
    }

}
