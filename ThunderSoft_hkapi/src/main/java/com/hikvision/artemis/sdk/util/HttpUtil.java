/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.hikvision.artemis.sdk.util;

import com.hikvision.artemis.sdk.Response;
import com.hikvision.artemis.sdk.constant.*;
import com.hikvision.artemis.sdk.util.MessageDigestUtil;
import com.hikvision.artemis.sdk.util.SignUtil;
import com.netease.lib.hktool.service.RegionHkAPIService;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.*;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreConnectionPNames;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;


/**
 * Http工具类
 */
public class HttpUtil {
    private static Map<String, RequestConfig> configMap = new ConcurrentHashMap<String, RequestConfig>(2);
    private static CloseableHttpClient closeableHttpClient;
    private static final Object LOCK = new Object();
    private static final org.slf4j.Logger log=org.slf4j.LoggerFactory.getLogger(HttpUtil.class);
    /**
     * HTTP GET
     * @param host
     * @param path
     * @param connectTimeout
     * @param headers
     * @param querys
     * @param signHeaderPrefixList
     * @param appKey
     * @param appSecret
     * @return
     * @throws Exception
     */
    public static Response httpGet(String host, String path, int connectTimeout, Map<String, String> headers, Map<String, String> querys, List<String> signHeaderPrefixList, String appKey, String appSecret)
            throws Exception {
        headers = initialBasicHeader(HttpMethod.GET, path, headers, querys, null, signHeaderPrefixList, appKey, appSecret);

        CloseableHttpClient httpClient = wrapClient(host);
        Response r = null;

        HttpGet get = new HttpGet(initUrl(host, path, querys));

        for (Map.Entry<String, String> e : headers.entrySet()) {
            get.addHeader(e.getKey(), MessageDigestUtil.utf8ToIso88591(e.getValue()));
        }
        CloseableHttpResponse rp = httpClient.execute(get);
        return convert(rp);
    }


    public static Response httpImgGet(String host, String path, int connectTimeout, Map<String, String> headers, Map<String, String> querys, List<String> signHeaderPrefixList, String appKey, String appSecret)
            throws Exception {
        headers = initialBasicHeader(HttpMethod.GET, path, headers, querys, null, signHeaderPrefixList, appKey, appSecret);

        CloseableHttpClient httpClient = wrapClient(host);
        HttpGet get = new HttpGet(initUrl(host, path, querys));
        for (Map.Entry<String, String> e : headers.entrySet()) {
            get.addHeader(e.getKey(), MessageDigestUtil.utf8ToIso88591(e.getValue()));
        }
        CloseableHttpResponse rp = httpClient.execute(get);
        return convertImg(rp);
    }

    /**
     * HTTP POST表单
     * @param host
     * @param path
     * @param connectTimeout
     * @param headers
     * @param querys
     * @param bodys
     * @param signHeaderPrefixList
     * @param appKey
     * @param appSecret
     * @return
     * @throws Exception
     */
    public static Response httpPost(String host, String path, int connectTimeout, Map<String, String> headers, Map<String, String> querys, Map<String, String> bodys, List<String> signHeaderPrefixList, String appKey, String appSecret)
            throws Exception {
        if (headers == null) {
            headers = new HashMap<String, String>();
        }

        headers.put(HttpHeader.HTTP_HEADER_CONTENT_TYPE, ContentType.CONTENT_TYPE_FORM);

        headers = initialBasicHeader(HttpMethod.POST, path, headers, querys, bodys, signHeaderPrefixList, appKey, appSecret);

        CloseableHttpClient httpClient = wrapClient(host);

        HttpPost post = new HttpPost(initUrl(host, path, querys));
        for (Map.Entry<String, String> e : headers.entrySet()) {
            post.addHeader(e.getKey(), MessageDigestUtil.utf8ToIso88591(e.getValue()));
        }

        UrlEncodedFormEntity formEntity = buildFormEntity(bodys);
        if (formEntity != null) {
            post.setEntity(formEntity);
        }
        return convert(httpClient.execute(post));
    }

    /**
     * HTTP POST表单
     * @param host
     * @param path
     * @param connectTimeout
     * @param headers
     * @param querys
     * @param bodys
     * @param signHeaderPrefixList
     * @param appKey
     * @param appSecret
     * @return
     * @throws Exception
     */
    public static Response httpImgPost(String host, String path, int connectTimeout, Map<String, String> headers, Map<String, String> querys, Map<String, String> bodys, List<String> signHeaderPrefixList, String appKey, String appSecret)
            throws Exception {
        if (headers == null) {
            headers = new HashMap<String, String>();
        }

        headers.put(HttpHeader.HTTP_HEADER_CONTENT_TYPE, ContentType.CONTENT_TYPE_FORM);

        headers = initialBasicHeader(HttpMethod.POST, path, headers, querys, bodys, signHeaderPrefixList, appKey, appSecret);

        CloseableHttpClient httpClient = wrapClient(host);

        HttpPost post = new HttpPost(initUrl(host, path, querys));
        for (Map.Entry<String, String> e : headers.entrySet()) {
            post.addHeader(e.getKey(), MessageDigestUtil.utf8ToIso88591(e.getValue()));
        }

        UrlEncodedFormEntity formEntity = buildFormEntity(bodys);
        if (formEntity != null) {
            post.setEntity(formEntity);
        }
        return convertImg(httpClient.execute(post));
    }


    /**
     * Http POST 字符
     * @param host
     * @param path
     * @param connectTimeout
     * @param headers
     * @param querys
     * @param body
     * @param signHeaderPrefixList
     * @param appKey
     * @param appSecret
     * @return
     * @throws Exception
     */
    public static Response httpPost(String host, String path, int connectTimeout, Map<String, String> headers, Map<String, String> querys, String body, List<String> signHeaderPrefixList, String appKey, String appSecret)
            throws Exception {

        String contentType =headers.get(HttpHeader.HTTP_HEADER_CONTENT_TYPE);
        if(ContentType.CONTENT_TYPE_FORM.equals(contentType)){//postString发鿁content-type为表单的请求，请求的body字符串必须为key-value组成的串，类似a=1&b=2这种形式
            Map<String,String> paramMap = strToMap(body);

            String modelDatas = paramMap.get("modelDatas"); //这个base64的字符串经过url编码，签名时先解砿(这个是针对大数据某个请求包含url编码的参敿)，对某个请求包含的参数的特殊处理
            if(StringUtils.isNotBlank(modelDatas)){
                paramMap.put("modelDatas",URLDecoder.decode(modelDatas));
            }

            headers = initialBasicHeader(HttpMethod.POST, path, headers, querys, paramMap, signHeaderPrefixList, appKey, appSecret);
        }else{
            headers = initialBasicHeader(HttpMethod.POST, path, headers, querys, null, signHeaderPrefixList, appKey, appSecret);
        }

        CloseableHttpClient httpClient = wrapClient(host);

        HttpPost post = new HttpPost(initUrl(host, path, querys));
        for (Map.Entry<String, String> e : headers.entrySet()) {
            post.addHeader(e.getKey(), MessageDigestUtil.utf8ToIso88591(e.getValue()));
        }

        if (StringUtils.isNotBlank(body)) {
            post.setEntity(new StringEntity(body, Constants.ENCODING));
        }
        return convert(httpClient.execute(post));
    }


    /**
     * Http POST 字符
     * @param host
     * @param path
     * @param connectTimeout
     * @param headers
     * @param querys
     * @param body
     * @param signHeaderPrefixList
     * @param appKey
     * @param appSecret
     * @return
     * @throws Exception
     */
    public static Response httpImgPost(String host, String path, int connectTimeout, Map<String, String> headers, Map<String, String> querys, String body, List<String> signHeaderPrefixList, String appKey, String appSecret)
            throws Exception {

        String contentType =headers.get(HttpHeader.HTTP_HEADER_CONTENT_TYPE);
        if(ContentType.CONTENT_TYPE_FORM.equals(contentType)){//postString发鿁content-type为表单的请求，请求的body字符串必须为key-value组成的串，类似a=1&b=2这种形式
            Map<String,String> paramMap = strToMap(body);

            String modelDatas = paramMap.get("modelDatas"); //这个base64的字符串经过url编码，签名时先解砿(这个是针对大数据某个请求包含url编码的参敿)，对某个请求包含的参数的特殊处理
            if(StringUtils.isNotBlank(modelDatas)){
                paramMap.put("modelDatas",URLDecoder.decode(modelDatas));
            }

            headers = initialBasicHeader(HttpMethod.POST, path, headers, querys, paramMap, signHeaderPrefixList, appKey, appSecret);
        }else{
            headers = initialBasicHeader(HttpMethod.POST, path, headers, querys, null, signHeaderPrefixList, appKey, appSecret);
        }

        CloseableHttpClient httpClient = wrapClient(host);

        HttpPost post = new HttpPost(initUrl(host, path, querys));
        for (Map.Entry<String, String> e : headers.entrySet()) {
            post.addHeader(e.getKey(), MessageDigestUtil.utf8ToIso88591(e.getValue()));
        }

        if (StringUtils.isNotBlank(body)) {
            post.setEntity(new StringEntity(body, Constants.ENCODING));

        }
        return convertImg(httpClient.execute(post));
    }

    /**
     * HTTP POST 字节数组
     * @param host
     * @param path
     * @param connectTimeout
     * @param headers
     * @param querys
     * @param bodys
     * @param signHeaderPrefixList
     * @param appKey
     * @param appSecret
     * @return
     * @throws Exception
     */
    public static Response httpPost(String host, String path, int connectTimeout, Map<String, String> headers, Map<String, String> querys, byte[] bodys, List<String> signHeaderPrefixList, String appKey, String appSecret)
            throws Exception {
        headers = initialBasicHeader(HttpMethod.POST, path, headers, querys, null, signHeaderPrefixList, appKey, appSecret);

        CloseableHttpClient httpClient = wrapClient(host);

        HttpPost post = new HttpPost(initUrl(host, path, querys));
        for (Map.Entry<String, String> e : headers.entrySet()) {
            post.addHeader(e.getKey(), MessageDigestUtil.utf8ToIso88591(e.getValue()));
        }

        if (bodys != null) {
            post.setEntity(new ByteArrayEntity(bodys));
        }
        return convert(httpClient.execute(post));
    }

    /**
     * HTTP POST 文件上传
     * @param host
     * @param path
     * @param connectTimeout
     * @param headers
     * @param querys
     * @param bodys
     * @param signHeaderPrefixList
     * @param appKey
     * @param appSecret
     * @return
     * @throws Exception
     */
    public static Response httpFilePost(String host, String path, int connectTimeout, Map<String, String> headers, Map<String, String> querys, final Map<String, Object> bodys, List<String> signHeaderPrefixList, String appKey, String appSecret)
            throws Exception {
        if (headers == null) {
            headers = new HashMap<String, String>();
        }
        HttpEntity formEntity = buildFormHttpEntity(bodys);
        String boundary = null;
        if (formEntity != null) {
            String contentType = formEntity.getContentType().getValue();
            boundary = contentType.substring(contentType.indexOf(Constants.SPE6) + 2, contentType.lastIndexOf(Constants.SPE6)).trim();
        }

        headers.put(HttpHeader.HTTP_HEADER_CONTENT_TYPE, ContentType.CONTENT_TYPE_FILE_FORM + Constants.SPE6 + boundary);

        headers = initialBasicHeader(HttpMethod.POST, path, headers, querys, null, signHeaderPrefixList, appKey, appSecret);

        CloseableHttpClient httpClient = wrapClient(host);
        HttpPost post = new HttpPost(initUrl(host, path, querys));
        for (Map.Entry<String, String> e : headers.entrySet()) {
            post.addHeader(e.getKey(), MessageDigestUtil.utf8ToIso88591(e.getValue()));
        }

        if (formEntity != null) {
            post.setEntity(formEntity);
        }
        return convert(httpClient.execute(post));
    }

    /**
     * HTTP PUT 字符丿
     * @param host
     * @param path
     * @param connectTimeout
     * @param headers
     * @param querys
     * @param body
     * @param signHeaderPrefixList
     * @param appKey
     * @param appSecret
     * @return
     * @throws Exception
     */
    public static Response httpPut(String host, String path, int connectTimeout, Map<String, String> headers, Map<String, String> querys, String body, List<String> signHeaderPrefixList, String appKey, String appSecret)
            throws Exception {
        headers = initialBasicHeader(HttpMethod.PUT, path, headers, querys, null, signHeaderPrefixList, appKey, appSecret);

        CloseableHttpClient httpClient = wrapClient(host);

        HttpPut put = new HttpPut(initUrl(host, path, querys));
        for (Map.Entry<String, String> e : headers.entrySet()) {
            put.addHeader(e.getKey(), MessageDigestUtil.utf8ToIso88591(e.getValue()));
        }

        if (StringUtils.isNotBlank(body)) {
            put.setEntity(new StringEntity(body, Constants.ENCODING));

        }
        return convert(httpClient.execute(put));
    }

    /**
     * HTTP PUT字节数组
     * @param host
     * @param path
     * @param connectTimeout
     * @param headers
     * @param querys
     * @param bodys
     * @param signHeaderPrefixList
     * @param appKey
     * @param appSecret
     * @return
     * @throws Exception
     */
    public static Response httpPut(String host, String path, int connectTimeout, Map<String, String> headers, Map<String, String> querys, byte[] bodys, List<String> signHeaderPrefixList, String appKey, String appSecret)
            throws Exception {
        headers = initialBasicHeader(HttpMethod.PUT, path, headers, querys, null, signHeaderPrefixList, appKey, appSecret);

        CloseableHttpClient httpClient = wrapClient(host);
        httpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, getTimeout(connectTimeout));

        HttpPut put = new HttpPut(initUrl(host, path, querys));
        for (Map.Entry<String, String> e : headers.entrySet()) {
            put.addHeader(e.getKey(), MessageDigestUtil.utf8ToIso88591(e.getValue()));
        }

        if (bodys != null) {
            put.setEntity(new ByteArrayEntity(bodys));
        }
        Response r = null;
        try {
            r = convert(httpClient.execute(put));
        } finally {
            if (httpClient != null) {
                httpClient.close();
            }
        }
        return r;
    }

    /**
     * HTTP DELETE
     * @param host
     * @param path
     * @param connectTimeout
     * @param headers
     * @param querys
     * @param signHeaderPrefixList
     * @param appKey
     * @param appSecret
     * @return
     * @throws Exception
     */
    public static Response httpDelete(String host, String path, int connectTimeout, Map<String, String> headers, Map<String, String> querys, List<String> signHeaderPrefixList, String appKey, String appSecret)
            throws Exception {
        headers = initialBasicHeader(HttpMethod.DELETE, path, headers, querys, null, signHeaderPrefixList, appKey, appSecret);

        CloseableHttpClient httpClient = wrapClient(host);

        HttpDelete delete = new HttpDelete(initUrl(host, path, querys));
        for (Map.Entry<String, String> e : headers.entrySet()) {
            delete.addHeader(e.getKey(), MessageDigestUtil.utf8ToIso88591(e.getValue()));
        }

        Response r = null;
        try {
            r = convert(httpClient.execute(delete));
        } finally {
            if (httpClient != null) {
                httpClient.close();
            }
        }
        return r;
    }

    /**
     * 构建FormEntity
     *
     * @param formParam
     * @return
     * @throws UnsupportedEncodingException
     */
    private static UrlEncodedFormEntity buildFormEntity(Map<String, String> formParam)
            throws UnsupportedEncodingException {
        if (formParam != null) {
            List<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>();

            for (String key : formParam.keySet()) {
                nameValuePairList.add(new BasicNameValuePair(key, formParam.get(key)));
            }
            UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(nameValuePairList, Constants.ENCODING);
            formEntity.setContentType(ContentType.CONTENT_TYPE_FORM);
            return formEntity;
        }

        return null;
    }

    public static String initUrl(String host, String path, Map<String, String> querys) throws UnsupportedEncodingException {
        StringBuilder sbUrl = new StringBuilder();
        sbUrl.append(host);
        if (!StringUtils.isBlank(path)) {
            sbUrl.append(path);
        }
        if (null != querys) {
            StringBuilder sbQuery = new StringBuilder();
            for (Map.Entry<String, String> query : querys.entrySet()) {
                if (0 < sbQuery.length()) {
                    sbQuery.append(Constants.SPE3);
                }
                if (StringUtils.isBlank(query.getKey()) && !StringUtils.isBlank(query.getValue())) {
                    sbQuery.append(query.getValue());
                }
                if (!StringUtils.isBlank(query.getKey())) {
                    sbQuery.append(query.getKey());
                    if (!StringUtils.isBlank(query.getValue())) {
                        sbQuery.append(Constants.SPE4);
                        sbQuery.append(URLEncoder.encode(query.getValue(), Constants.ENCODING));
                    }
                }
            }
            if (0 < sbQuery.length()) {
                sbUrl.append(Constants.SPE5).append(sbQuery);
            }
        }

        return sbUrl.toString();
    }


    /**
     * 初始化基硿Header
     * @param method
     * @param path
     * @param headers
     * @param querys
     * @param bodys
     * @param signHeaderPrefixList
     * @param appKey
     * @param appSecret
     * @return
     * @throws MalformedURLException
     */
    private static Map<String, String> initialBasicHeader(String method, String path,
                                                          Map<String, String> headers,
                                                          Map<String, String> querys,
                                                          Map<String, String> bodys,
                                                          List<String> signHeaderPrefixList,
                                                          String appKey, String appSecret)
            throws MalformedURLException {
        if (headers == null) {
            headers = new HashMap<String, String>();
        }

        headers.put(SystemHeader.X_CA_TIMESTAMP, String.valueOf(new Date().getTime()));
        headers.put(SystemHeader.X_CA_NONCE, UUID.randomUUID().toString());
        headers.put(SystemHeader.X_CA_KEY, appKey);
        headers.put(SystemHeader.X_CA_SIGNATURE,
                SignUtil.sign(appSecret, method, path, headers, querys, bodys, signHeaderPrefixList));
        log.info("请求路径=============="+path);
        log.info("签名=============="+SignUtil.sign(appSecret, method, path, headers, querys, bodys, signHeaderPrefixList));
        return headers;
    }

    /**
     * 读取超时时间
     *
     * @param timeout
     * @return
     */
    private static int getTimeout(int timeout) {
        if (timeout == 0) {
            return Constants.DEFAULT_TIMEOUT;
        }

        return timeout;
    }

    private static Response convert(CloseableHttpResponse response) throws IOException {
        Response res = new Response();

        if (null != response) {
            res.setStatusCode(response.getStatusLine().getStatusCode());
            for (Header header : response.getAllHeaders()) {
                res.setHeader(header.getName(), MessageDigestUtil.iso88591ToUtf8(header.getValue()));
            }

            res.setContentType(res.getHeader("Content-Type"));
            res.setRequestId(res.getHeader("X-Ca-Request-Id"));
            res.setErrorMessage(res.getHeader("X-Ca-Error-Message"));
            if(response.getEntity() == null){
                res.setBody(null);
            }else{
                res.setBody(readStreamAsStr(response.getEntity().getContent()));
            }
        } else {
            //服务器无回应
            res.setStatusCode(500);
            res.setErrorMessage("No Response");
        }

        return res;
    }


    private static Response convertImg(CloseableHttpResponse response) throws IOException {
        Response res = new Response();

        if (null != response) {
            res.setStatusCode(response.getStatusLine().getStatusCode());
            for (Header header : response.getAllHeaders()) {
                res.setHeader(header.getName(), MessageDigestUtil.iso88591ToUtf8(header.getValue()));
            }

            res.setContentType(res.getHeader("Content-Type"));
            res.setRequestId(res.getHeader("X-Ca-Request-Id"));
            res.setErrorMessage(res.getHeader("X-Ca-Error-Message"));
            res.setResponse(response);
        } else {
            res.setStatusCode(500);
            res.setErrorMessage("No Response");
        }

        return res;
    }


    /**
     * 将流转换为字符串
     *
     * @param is
     * @return
     * @throws IOException
     */
    public static String readStreamAsStr(InputStream is) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        WritableByteChannel dest = Channels.newChannel(bos);
        ReadableByteChannel src = Channels.newChannel(is);
        ByteBuffer bb = ByteBuffer.allocate(4096);

        while (src.read(bb) != -1) {
            bb.flip();
            dest.write(bb);
            bb.clear();
        }
        src.close();
        dest.close();

        return new String(bos.toByteArray(), Constants.ENCODING);
    }

    /**
     * 将流转换为字符串
     *
     * @param src
     * @return
     * @throws IOException
     */
    public static String readImageAsStr(byte[] src) throws IOException {
        StringBuilder stringBuilder = new StringBuilder("");
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }

    public static String inStream2String(InputStream  src) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buf = new byte[1024];
        int len = -1;
        while ((len = src.read(buf)) != -1) {
            baos.write(buf, 0, len);
        }
        return new String(baos.toByteArray());
    }



    public static CloseableHttpClient wrapClient(String host) throws NoSuchAlgorithmException, KeyManagementException {
        if (closeableHttpClient == null) {
            synchronized (LOCK){
                if (closeableHttpClient == null) {
                    SSLConnectionSocketFactory socketFactory = null;
                    if (host.startsWith("https://")) {
                        socketFactory = sslClientNew();
                    }
                    closeableHttpClient = HttpClients.custom()
                            .setSSLSocketFactory(socketFactory)
                            .setDefaultRequestConfig(getConfig())
                            .build();
                    return closeableHttpClient;
                }
            }
        }
        return closeableHttpClient;
    }

    private static SSLConnectionSocketFactory sslClientNew() {
        try {
            SSLContext ctx = null;
            /** JDK1.6支持TLS的版本为最高为TLSv1.1
             ** 如果不指定具体版本，使用默认版本建立连接，JDK1.8默认版本TLSv1.2；JDK1.7默认版本TLSv1.1；JDK1.6默认版本TLSv1
             ** TLSv1、TLSv1.1 陆续禁用,导致JDK6无法支持https调用
             */
            String jdkVersion = System.getProperty("java.specification.version");
            if (Double.parseDouble(jdkVersion) >= Constants.JDK_VERSION) {
                ctx = SSLContext.getInstance("TLSv1.2");
            } else {
                ctx = SSLContext.getInstance("TLS");
            }
            //   SSLContext ctx = SSLContext.getInstance("SSLv3");
            X509TrustManager tm = new X509TrustManager() {
                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }

                public void checkClientTrusted(X509Certificate[] xcs, String str) {

                }

                public void checkServerTrusted(X509Certificate[] xcs, String str) {

                }
            };
            ctx.init(null, new TrustManager[]{tm}, null);
            //SSLSocketFactory ssf = new SSLSocketFactory(ctx);
            return new SSLConnectionSocketFactory(ctx, NoopHostnameVerifier.INSTANCE);
        }catch  (KeyManagementException ex) {
            throw new RuntimeException(ex);
        } catch (NoSuchAlgorithmException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Deprecated
    private static void sslClient(CloseableHttpClient httpClient) {
        try {
            SSLContext ctx = null;

            /** JDK1.6支持TLS的版本为最高为TLSv1.1
            ** 如果不指定具体版本，使用默认版本建立连接，JDK1.8默认版本TLSv1.2；JDK1.7默认版本TLSv1.1；JDK1.6默认版本TLSv1
            ** TLSv1、TLSv1.1 陆续禁用,导致JDK6无法支持https调用
            */
            String jdkVersion = System.getProperty("java.specification.version");
            if (Double.parseDouble(jdkVersion) >= Constants.JDK_VERSION) {
                ctx = SSLContext.getInstance("TLSv1.2");
            } else {
                ctx = SSLContext.getInstance("TLS");
            }
            //   SSLContext ctx = SSLContext.getInstance("SSLv3");
            X509TrustManager tm = new X509TrustManager() {
                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }
                public void checkClientTrusted(X509Certificate[] xcs, String str) {

                }
                public void checkServerTrusted(X509Certificate[] xcs, String str) {

                }
            };

            ctx.init(null, new TrustManager[] { tm }, null);
            SSLSocketFactory ssf = new SSLSocketFactory(ctx);
            ssf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
            ClientConnectionManager ccm = httpClient.getConnectionManager();
            SchemeRegistry registry = ccm.getSchemeRegistry();
            registry.register(new Scheme("https", 443, ssf));
        } catch (KeyManagementException ex) {
            throw new RuntimeException(ex);
        } catch (NoSuchAlgorithmException ex) {
            throw new RuntimeException(ex);
        }
    }
    private static Map<String,String> strToMap(String str){
        Map<String,String> map = new HashMap<String,String>();
        try{
            String[] params =str.split("&");
            for(String param : params){
                String[] a =param.split("=");
                map.put(a[0], a[1]);
            }
        }catch(Exception e){
            throw new RuntimeException(e);
        }
        return  map;
    }

    private static HttpEntity buildFormHttpEntity(Map<String, Object> formParam){
        final MultipartEntityBuilder builder = MultipartEntityBuilder.create().setMode(HttpMultipartMode.RFC6532);
        String fileName=null;
        if (formParam != null) {
            try {
                for (Map.Entry<String,Object> entry : formParam.entrySet()) {
                    if (entry.getValue() instanceof String) {
                        builder.addTextBody(entry.getKey(), String.valueOf(entry.getValue()) );
                    } else if (entry.getValue() instanceof Number) {
                        builder.addTextBody(entry.getKey(),  String.valueOf(entry.getValue()));
                    } else if (entry.getValue() instanceof Boolean) {
                        builder.addTextBody(entry.getKey(),  String.valueOf(entry.getValue()));
                    } else if (entry.getValue() instanceof Enum) {
                        builder.addTextBody(entry.getKey(),  String.valueOf(entry.getValue()));
                    } else if (entry.getValue() instanceof File){
                        builder.addBinaryBody(entry.getKey(), (File)entry.getValue());
                        fileName = entry.getKey();
                    } else {
                        builder.addBinaryBody(entry.getKey(), (InputStream) entry.getValue());
                    }
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        if (StringUtils.isNotBlank(fileName)) {
            //去除文件参数，该参数不参与签名
            formParam.remove(fileName);
        }
        builder.setContentType(org.apache.http.entity.ContentType.MULTIPART_FORM_DATA);
        return builder.build();
    }

    private static RequestConfig getConfig() {
        RequestConfig config = configMap.get(Constants.REQUEST_CONFIG);
        if (config == null) {
            RequestConfig.Builder custom = RequestConfig.custom();
            custom.setConnectTimeout(Constants.DEFAULT_TIMEOUT);
            custom.setSocketTimeout(Constants.SOCKET_TIMEOUT);
            config = custom.build();
            configMap.put(Constants.REQUEST_CONFIG, config);
        }
        return config;
    }
}
