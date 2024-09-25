//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.hikvision.artemis.sdk;

import com.hikvision.artemis.sdk.enums.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class Request<T> {
    private Method method;
    private String host;
    private String path;
    private String appKey;
    private String appSecret;
    private int timeout;
    private Map<String, String> headers;
    private Map<String, String> querys;
    private Map<String, T> bodys;
    private String stringBody;
    private byte[] bytesBody;
    private List<String> signHeaderPrefixList;

    public Request() {
    }

    public Request(Method method, String host, String path, String appKey, String appSecret, int timeout) {
        this.method = method;
        this.host = host;
        this.path = path;
        this.appKey = appKey;
        this.appSecret = appSecret;
        this.timeout = timeout;
    }

    public Method getMethod() {
        return this.method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public String getHost() {
        return this.host;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getPath() {
        return this.path;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getAppKey() {
        return this.appKey;
    }

    public void setAppKey(String appKey) {
        this.appKey = appKey;
    }

    public String getAppSecret() {
        return this.appSecret;
    }

    public void setAppSecret(String appSecret) {
        this.appSecret = appSecret;
    }

    public int getTimeout() {
        return this.timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public Map<String, String> getHeaders() {
        return this.headers;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public Map<String, String> getQuerys() {
        return this.querys;
    }

    public void setQuerys(Map<String, String> querys) {
        this.querys = querys;
    }

    public Map<String, T> getBodys() {
        return this.bodys;
    }

    public void setBodys(Map<String, T> bodys) {
        this.bodys = bodys;
    }

    public String getStringBody() {
        return this.stringBody;
    }

    public void setStringBody(String stringBody) {
        this.stringBody = stringBody;
    }

    public byte[] getBytesBody() {
        return this.bytesBody;
    }

    public void setBytesBody(byte[] bytesBody) {
        this.bytesBody = bytesBody;
    }

    public List<String> getSignHeaderPrefixList() {
        return this.signHeaderPrefixList;
    }

    public void setSignHeaderPrefixList(List<String> signHeaderPrefixList) {
        this.signHeaderPrefixList = signHeaderPrefixList;
    }

    public String toString() {
        return "RequestInfo {method=" + this.method + ", host='" + this.host + '\'' + ", path='" + this.path + '\'' + ", appKey='" + this.appKey + '\'' + ", timeout=" + this.timeout + ", headers=" + this.headers + ", querys=" + this.querys + ", bodys=" + this.bodys + ", stringBody='" + this.stringBody + '\'' + ", bytesBody=" + Arrays.toString(this.bytesBody) + ", signHeaderPrefixList=" + this.signHeaderPrefixList + '}';
    }
}
