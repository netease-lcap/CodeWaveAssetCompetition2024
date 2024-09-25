//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.hikvision.artemis.sdk;

import com.hikvision.artemis.sdk.util.HttpUtil;

public class Client {
    public Client() {
    }

    public static Response execute(Request request) throws Exception {
        request.getHeaders().put("Hik-Request-ID", "artemis&artemis-http-client&1.1.6");
        switch(request.getMethod()) {
        case GET:
            return HttpUtil.httpGet(request.getHost(), request.getPath(), request.getTimeout(), request.getHeaders(), request.getQuerys(), request.getSignHeaderPrefixList(), request.getAppKey(), request.getAppSecret());
        case GET_RESPONSE:
            return HttpUtil.httpImgGet(request.getHost(), request.getPath(), request.getTimeout(), request.getHeaders(), request.getQuerys(), request.getSignHeaderPrefixList(), request.getAppKey(), request.getAppSecret());
        case POST_FORM:
            return HttpUtil.httpPost(request.getHost(), request.getPath(), request.getTimeout(), request.getHeaders(), request.getQuerys(), request.getBodys(), request.getSignHeaderPrefixList(), request.getAppKey(), request.getAppSecret());
        case POST_FORM_RESPONSE:
            return HttpUtil.httpImgPost(request.getHost(), request.getPath(), request.getTimeout(), request.getHeaders(), request.getQuerys(), request.getBodys(), request.getSignHeaderPrefixList(), request.getAppKey(), request.getAppSecret());
        case POST_STRING:
            return HttpUtil.httpPost(request.getHost(), request.getPath(), request.getTimeout(), request.getHeaders(), request.getQuerys(), request.getStringBody(), request.getSignHeaderPrefixList(), request.getAppKey(), request.getAppSecret());
        case POST_STRING_RESPONSE:
            return HttpUtil.httpImgPost(request.getHost(), request.getPath(), request.getTimeout(), request.getHeaders(), request.getQuerys(), request.getStringBody(), request.getSignHeaderPrefixList(), request.getAppKey(), request.getAppSecret());
        case POST_BYTES:
            return HttpUtil.httpPost(request.getHost(), request.getPath(), request.getTimeout(), request.getHeaders(), request.getQuerys(), request.getBytesBody(), request.getSignHeaderPrefixList(), request.getAppKey(), request.getAppSecret());
        case POST_FILE:
            return HttpUtil.httpFilePost(request.getHost(), request.getPath(), request.getTimeout(), request.getHeaders(), request.getQuerys(), request.getBodys(), request.getSignHeaderPrefixList(), request.getAppKey(), request.getAppSecret());
        case PUT_STRING:
            return HttpUtil.httpPut(request.getHost(), request.getPath(), request.getTimeout(), request.getHeaders(), request.getQuerys(), request.getStringBody(), request.getSignHeaderPrefixList(), request.getAppKey(), request.getAppSecret());
        case PUT_BYTES:
            return HttpUtil.httpPut(request.getHost(), request.getPath(), request.getTimeout(), request.getHeaders(), request.getQuerys(), request.getBytesBody(), request.getSignHeaderPrefixList(), request.getAppKey(), request.getAppSecret());
        case DELETE:
            return HttpUtil.httpDelete(request.getHost(), request.getPath(), request.getTimeout(), request.getHeaders(), request.getQuerys(), request.getSignHeaderPrefixList(), request.getAppKey(), request.getAppSecret());
        default:
            throw new IllegalArgumentException(String.format("unsupported method:%s", request.getMethod()));
        }
    }
}
