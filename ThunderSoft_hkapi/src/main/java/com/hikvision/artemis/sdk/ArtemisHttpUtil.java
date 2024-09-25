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
package com.hikvision.artemis.sdk;


import com.hikvision.artemis.sdk.config.ArtemisConfig;
import com.hikvision.artemis.sdk.constant.Constants;
import com.hikvision.artemis.sdk.constant.ContentType;
import com.hikvision.artemis.sdk.constant.HttpHeader;
import com.hikvision.artemis.sdk.constant.MsgConstants;
import com.hikvision.artemis.sdk.enums.Method;
import com.hikvision.artemis.sdk.util.MessageDigestUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;

import java.util.HashMap;
import java.util.Map;

public class ArtemisHttpUtil {


    //自定义参与签名Header前缀（可选,默认只有"X-Ca-"开头的参与到Header签名）
    //private final static List<String> CUSTOM_HEADERS_TO_SIGN_PREFIX = new ArrayList<String>();

    //调用网关成功的标志,标志位
    private final static String SUCC_PRE = "2";

    //调用网关重定向的标志,标志位
    private final static String REDIRECT_PRE = "3";

    /**
     * get请求
     *
     * @param artemisConfig 请求路径，请求时合作方aksk参数封装类
     * @param path          artemis配置的get请求的路径 是一个数组长度为1的Hashmap集合，只存一组数据，key为http的请求方式，value为host后面的path路径。
     * @param querys        map类型  get请求的url查询参数（url中的query参数,没有就是为空）形如 "?aa=1&&bb=2"形式参数变成map键值对 query.put("aa","1");query.put("bb","2")
     * @param accept        指定客户端能够接收的内容类型，该参数传空时的默认全部类型接受
     * @param contentType   请求的与实体对应的MIME信息，该参数传空时的取默认值
     * @param header
     */
    public static String doGetArtemis(ArtemisConfig artemisConfig, Map<String, String> path, Map<String, String> querys, String accept, String contentType, Map<String, String> header) throws Exception {
        /**
         * 根据传入的path获取是请求是http还是https
         */
        String httpSchema = (String) path.keySet().toArray()[0];

        if (httpSchema == null || StringUtils.isEmpty(httpSchema))
            throw new RuntimeException(MsgConstants.HTTP_SCHEMA_ERROR + "httpSchema: " + httpSchema);

        String responseStr = null;

        Map<String, String> headers = new HashMap<String, String>();
        //（必填）根据期望的Response内容类型设置
        if (StringUtils.isNotBlank(accept)) {
            headers.put(HttpHeader.HTTP_HEADER_ACCEPT, accept);
        } else {
            headers.put(HttpHeader.HTTP_HEADER_ACCEPT, "*/*");
        }
        //请求的与实体对应的MIME信息
        if (StringUtils.isNotBlank(contentType)) {
            headers.put(HttpHeader.HTTP_HEADER_CONTENT_TYPE, contentType);
        } else {
            headers.put(HttpHeader.HTTP_HEADER_CONTENT_TYPE, ContentType.CONTENT_TYPE_TEXT);
        }
        if(header!=null){
            headers.putAll(header);
        }
        Request request = new Request(Method.GET, httpSchema + artemisConfig.getHost(),
                path.get(httpSchema), artemisConfig.getAppKey(), artemisConfig.getAppSecret(), Constants.DEFAULT_TIMEOUT);
        request.setHeaders(headers);

        request.setQuerys(querys);
        //调用服务端
        Response response = execute(request);

        return response.getBody();

    }


    /**
     * get请求
     *
     * @param artemisConfig 请求路径，请求时合作方aksk参数封装类
     * @param path          artemis配置的get请求的路径 是一个数组长度为1的Hashmap集合，只存一组数据，key为http的请求方式，value为host后面的path路径。
     * @param querys        map类型  get请求的url查询参数（url中的query参数,没有就是为空）形如 "?aa=1&&bb=2"形式参数变成map键值对 query.put("aa","1");query.put("bb","2")
     * @param accept        指定客户端能够接收的内容类型，该参数传空时的默认全部类型接受
     * @param contentType   请求的与实体对应的MIME信息，该参数传空时的取默认值
     */

    public static String doGetArtemis(ArtemisConfig artemisConfig, Map<String, String> path, Map<String, String> querys, String accept, String contentType) throws Exception {

        /**
         * 根据传入的path获取是请求是http还是https
         */
        String httpSchema = (String) path.keySet().toArray()[0];

        if (httpSchema == null || StringUtils.isEmpty(httpSchema))
            throw new RuntimeException(MsgConstants.HTTP_SCHEMA_ERROR + "httpSchema: " + httpSchema);

        String responseStr = null;

        Map<String, String> headers = new HashMap<String, String>();
        //（必填）根据期望的Response内容类型设置
        if (StringUtils.isNotBlank(accept)) {
            headers.put(HttpHeader.HTTP_HEADER_ACCEPT, accept);
        } else {
            headers.put(HttpHeader.HTTP_HEADER_ACCEPT, "*/*");
        }
        //请求的与实体对应的MIME信息
        if (StringUtils.isNotBlank(contentType)) {
            headers.put(HttpHeader.HTTP_HEADER_CONTENT_TYPE, contentType);
        } else {
            headers.put(HttpHeader.HTTP_HEADER_CONTENT_TYPE, ContentType.CONTENT_TYPE_TEXT);
        }

        Request request = new Request(Method.GET, httpSchema + artemisConfig.getHost(),
                path.get(httpSchema), artemisConfig.getAppKey(), artemisConfig.getAppSecret(), Constants.DEFAULT_TIMEOUT);
        request.setHeaders(headers);

        request.setQuerys(querys);

        //调用服务端
        Response response = execute(request);
        return response.getBody();

    }

    /**
     *
     * @param artemisConfig 请求路径，请求时合作方aksk参数封装类
     * @param path         artemis配置的get请求的路径 是一个数组长度为1的Hashmap集合，只存一组数据，key为http的请求方式，value为host后面的path路径。
     * @param querys        map类型  get请求的url查询参数（url中的query参数,没有就是为空）形如 "?aa=1&&bb=2"形式参数变成map键值对 query.put("aa","1");query.put("bb","2")
     * @param accept        指定客户端能够接收的内容类型，该参数传空时的默认全部类型接受
     * @param contentType   请求的与实体对应的MIME信息，该参数传空时的取默认值
     * @param header        请求参数有header以map的方式,没有则为null
     * @return              GET图片下载类型 HttpResponse类型
     */
    public static HttpResponse doGetResponse(ArtemisConfig artemisConfig, Map<String, String> path, Map<String, String> querys, String accept, String contentType, Map<String, String> header) throws Exception {
        /**
         * 根据传入的path获取是请求是http还是https
         */
        String httpSchema = (String) path.keySet().toArray()[0];

        if (httpSchema == null || StringUtils.isEmpty(httpSchema))
            throw new RuntimeException(MsgConstants.HTTP_SCHEMA_ERROR + "httpSchema: " + httpSchema);

        HttpResponse httpResponse = null;

        Map<String, String> headers = new HashMap<String, String>();
        //（必填）根据期望的Response内容类型设置
        if (StringUtils.isNotBlank(accept)) {
            headers.put(HttpHeader.HTTP_HEADER_ACCEPT, accept);
        } else {
            headers.put(HttpHeader.HTTP_HEADER_ACCEPT, "*/*");
        }
        //请求的与实体对应的MIME信息
        if (StringUtils.isNotBlank(contentType)) {
            headers.put(HttpHeader.HTTP_HEADER_CONTENT_TYPE, contentType);
        } else {
            headers.put(HttpHeader.HTTP_HEADER_CONTENT_TYPE, ContentType.CONTENT_TYPE_TEXT);
        }
        if(header!=null){
            headers.putAll(header);
        }
        Request request = new Request(Method.GET_RESPONSE, httpSchema + artemisConfig.getHost(),
                path.get(httpSchema), artemisConfig.getAppKey(), artemisConfig.getAppSecret(), Constants.DEFAULT_TIMEOUT);
        request.setHeaders(headers);

        request.setQuerys(querys);
        //调用服务端
        Response response = execute(request);

        return response.getResponse();

    }




    /**
     * postForm请求，postForm请求包含query参数和form表单参数
     *
     * @param artemisConfig 请求路径，请求时合作方aksk参数封装类
     * @param path          artemis配置的get请求的路径 是一个数组长度为1的Hashmap集合，只存一组数据，key为http的请求方式，value为host后面的path路径。
     * @param paramMap      Form表单请求的参数，键值对形式的map
     * @param querys        map类型  post请求的url查询参数（url中的query参数,没有就是为空）形如 "?aa=1&&bb=2"形式参数变成map键值对 query.put("aa","1");query.put("bb","2")
     * @param accept        指定客户端能够接收的内容类型，该参数传空时的默认全部类型接受
     * @param contentType   请求的与实体对应的MIME信息，该参数传空时的取默认值("application/x-www-form-urlencoded; charset=UTF-8")
     * @param header        请求参数有header以map的方式,没有则为null
     * @return              返回表单post请求,返回字符串类型
     *
     */

    public static String doPostFormArtemis(ArtemisConfig artemisConfig, Map<String, String> path, Map<String, String> paramMap, Map<String, String> querys,String accept, String contentType, Map<String, String> header) throws Exception {
        /**
         * 根据传入的path获取是请求是http还是https
         */
        String httpSchema = (String) path.keySet().toArray()[0];

        if (httpSchema == null || StringUtils.isEmpty(httpSchema))
            throw new RuntimeException(MsgConstants.HTTP_SCHEMA_ERROR + "httpSchema: " + httpSchema);
        String responseStr = null;

        Map<String, String> headers = new HashMap<String, String>();
        //（必填）根据期望的Response内容类型设置
        if (StringUtils.isNotBlank(accept)) {
            headers.put(HttpHeader.HTTP_HEADER_ACCEPT, accept);
        } else {
            headers.put(HttpHeader.HTTP_HEADER_ACCEPT, "*/*");
        }
        //请求的与实体对应的MIME信息
        if (StringUtils.isNotBlank(contentType)) {
            headers.put(HttpHeader.HTTP_HEADER_CONTENT_TYPE, contentType);
        } else {
            headers.put(HttpHeader.HTTP_HEADER_CONTENT_TYPE, ContentType.CONTENT_TYPE_FORM);
        }
        if(header!=null){
            headers.putAll(header);
        }
        Request request = new Request(Method.POST_FORM, httpSchema + artemisConfig.getHost(),
                path.get(httpSchema), artemisConfig.getAppKey(), artemisConfig.getAppSecret(), Constants.DEFAULT_TIMEOUT);
        request.setHeaders(headers);
        //postForm请求的query参数
        request.setQuerys(querys);
        //postForm请求的表单参数
        request.setBodys(paramMap);
        //调用服务端
        Response response = execute(request);
        return getResponseResult(response);
    }


    /**
     * postForm请求，postForm请求包含query参数和form表单参数
     *
     * @param artemisConfig 请求路径，请求时合作方aksk参数封装类
     * @param path          artemis配置的get请求的路径 是一个数组长度为1的Hashmap集合，只存一组数据，key为http的请求方式，value为host后面的path路径。
     * @param paramMap      Form表单请求的参数，键值对形式的map
     * @param querys        map类型  post请求的url查询参数（url中的query参数,没有就是为空）形如 "?aa=1&&bb=2"形式参数变成map键值对 query.put("aa","1");query.put("bb","2")
     * @param accept        指定客户端能够接收的内容类型，该参数传空时的默认全部类型接受
     * @param contentType   请求的与实体对应的MIME信息，该参数传空时的取默认值("application/x-www-form-urlencoded; charset=UTF-8")
     * @return              返回表单post请求,返回字符串类型
     *
     */

    public static String doPostFormArtemis(ArtemisConfig artemisConfig, Map<String, String> path, Map<String, Object> paramMap, Map<String, String> querys, String accept, String contentType) throws Exception {
        /**
         * 根据传入的path获取是请求是http还是https
         */
        String httpSchema = (String) path.keySet().toArray()[0];

        if (httpSchema == null || StringUtils.isEmpty(httpSchema))
            throw new RuntimeException(MsgConstants.HTTP_SCHEMA_ERROR + "httpSchema: " + httpSchema);
        String responseStr = null;

        Map<String, String> headers = new HashMap<String, String>();
        //（必填）根据期望的Response内容类型设置
        if (StringUtils.isNotBlank(accept)) {
            headers.put(HttpHeader.HTTP_HEADER_ACCEPT, accept);
        } else {
            headers.put(HttpHeader.HTTP_HEADER_ACCEPT, "*/*");
        }
        //请求的与实体对应的MIME信息
        if (StringUtils.isNotBlank(contentType)) {
            headers.put(HttpHeader.HTTP_HEADER_CONTENT_TYPE, contentType);
        } else {
            headers.put(HttpHeader.HTTP_HEADER_CONTENT_TYPE, ContentType.CONTENT_TYPE_FORM);
        }
        Request request = new Request(Method.POST_FORM, httpSchema + artemisConfig.getHost(),
                path.get(httpSchema), artemisConfig.getAppKey(), artemisConfig.getAppSecret(), Constants.DEFAULT_TIMEOUT);
        request.setHeaders(headers);
        //postForm请求的query参数
        request.setQuerys(querys);
        //postForm请求的表单参数
        request.setBodys(paramMap);
        //调用服务端
        Response response = execute(request);

        return getResponseResult(response);
    }

    /**
     * postformImg请求
     */

    /**
     *
     * @param artemisConfig 请求路径，请求时合作方aksk参数封装类
     * @param path          artemis配置的get请求的路径 是一个数组长度为1的Hashmap集合，只存一组数据，key为http的请求方式，value为host后面的path路径。
     * @param paramMap      Form表单请求的参数，键值对形式的map
     * @param querys        map类型  post请求的url查询参数（url中的query参数,没有就是为空）形如 "?aa=1&&bb=2"形式参数变成map键值对 query.put("aa","1");query.put("bb","2")
     * @param accept        指定客户端能够接收的内容类型，该参数传空时的默认全部类型接受
     * @param contentType   请求的与实体对应的MIME信息，该参数传空时的取默认值("application/x-www-form-urlencoded; charset=UTF-8")
     * @param header        请求参数有header以map的方式,没有则为null
     * @return              POST表单类型图片下载接口  HttpResponse类型
     */
    public static HttpResponse doPostFormImgArtemis(ArtemisConfig artemisConfig, Map<String, String> path, Map<String, String> paramMap, Map<String, String> querys,String accept, String contentType, Map<String, String> header) throws Exception {
        /**
         * 根据传入的path获取是请求是http还是https
         */
        String httpSchema = (String) path.keySet().toArray()[0];

        if (httpSchema == null || StringUtils.isEmpty(httpSchema))
            throw new RuntimeException(MsgConstants.HTTP_SCHEMA_ERROR + "httpSchema: " + httpSchema);
        HttpResponse response = null;

        Map<String, String> headers = new HashMap<String, String>();
        //（必填）根据期望的Response内容类型设置
        if (StringUtils.isNotBlank(accept)) {
            headers.put(HttpHeader.HTTP_HEADER_ACCEPT, accept);
        } else {
            headers.put(HttpHeader.HTTP_HEADER_ACCEPT, "*/*");
        }
        //请求的与实体对应的MIME信息
        if (StringUtils.isNotBlank(contentType)) {
            headers.put(HttpHeader.HTTP_HEADER_CONTENT_TYPE, contentType);
        } else {
            headers.put(HttpHeader.HTTP_HEADER_CONTENT_TYPE, ContentType.CONTENT_TYPE_FORM);
        }
        if(header!=null){
            headers.putAll(header);
        }
        Request request = new Request(Method.POST_FORM_RESPONSE, httpSchema + artemisConfig.getHost(),
                path.get(httpSchema), artemisConfig.getAppKey(), artemisConfig.getAppSecret(), Constants.DEFAULT_TIMEOUT);
        request.setHeaders(headers);
        //postForm请求的query参数
        request.setQuerys(querys);
        //postForm请求的表单参数
        request.setBodys(paramMap);
        //调用服务端
        Response response1 = execute(request);

        return response1.getResponse();
    }


    /**
     * postString请求
     *
     * @param artemisConfig 请求路径，请求时合作方aksk参数封装类
     * @param path          artemis配置的get请求的路径 是一个数组长度为1的Hashmap集合，只存一组数据，key为http的请求方式，value为host后面的path路径。
     * @param body          postString String请求体
     * @param querys        map类型  post请求的url查询参数（url中的query参数,没有就是为空）形如 "?aa=1&&bb=2"形式参数变成map键值对 query.put("aa","1");query.put("bb","2")
     * @param accept        指定客户端能够接收的内容类型，该参数传空时的默认全部类型接受
     * @param contentType   请求的与实体对应的MIME信息，该参数传空时的取默认值("application/text; charset=UTF-8")
     * @param header        header参数,无过没有值为null
     * @return  POST json类型接口  返回字符串类型
     */


    public static String doPostStringArtemis(ArtemisConfig artemisConfig, Map<String, String> path, String body,
                                             Map<String, String> querys, String accept, String contentType, Map<String, String> header) throws Exception {
        /**
         * 根据传入的path获取是请求是http还是https
         */
        String httpSchema = (String) path.keySet().toArray()[0];

        if (httpSchema == null || StringUtils.isEmpty(httpSchema))
            throw new RuntimeException(MsgConstants.HTTP_SCHEMA_ERROR + "httpSchema: " + httpSchema);
        String responseStr = null;


        Map<String, String> headers = new HashMap<String, String>();
        //（必填）根据期望的Response内容类型设置
        if (StringUtils.isNotBlank(accept)) {
            headers.put(HttpHeader.HTTP_HEADER_ACCEPT, accept);
        } else {
            headers.put(HttpHeader.HTTP_HEADER_ACCEPT, "*/*");
        }
        //（POST/PUT请求必选）请求Body内容格式请求的与实体对应的MIME信息
        if (StringUtils.isNotBlank(contentType)) {
            headers.put(HttpHeader.HTTP_HEADER_CONTENT_TYPE, contentType);
        } else {
            headers.put(HttpHeader.HTTP_HEADER_CONTENT_TYPE, ContentType.CONTENT_TYPE_TEXT);
        }
        if(header!=null){
            headers.putAll(header);
        }
        Request request = new Request(Method.POST_STRING, httpSchema + artemisConfig.getHost(),
                path.get(httpSchema), artemisConfig.getAppKey(), artemisConfig.getAppSecret(), Constants.DEFAULT_TIMEOUT);
        request.setHeaders(headers);
        //请求的query
        request.setQuerys(querys);
        //请求的bodyString
        request.setStringBody(body);
        //调用服务端
        Response response = execute(request);

        return getResponseResult(response);
    }


    /**
     * postString请求
     *
     * @param artemisConfig 请求路径，请求时合作方aksk参数封装类
     * @param path          artemis配置的get请求的路径 是一个数组长度为1的Hashmap集合，只存一组数据，key为http的请求方式，value为host后面的path路径。
     * @param body          postString String请求体
     * @param querys        map类型  post请求的url查询参数（url中的query参数,没有就是为空）形如 "?aa=1&&bb=2"形式参数变成map键值对 query.put("aa","1");query.put("bb","2")
     * @param accept        指定客户端能够接收的内容类型，该参数传空时的默认全部类型接受
     * @param contentType   请求的与实体对应的MIME信息，该参数传空时的取默认值("application/text; charset=UTF-8")
     * @return  POST json类型接口  返回字符串类型
     */


    public static String doPostStringArtemis(ArtemisConfig artemisConfig, Map<String, String> path, String body,
                                             Map<String, String> querys, String accept, String contentType) throws Exception {
        /**
         * 根据传入的path获取是请求是http还是https
         */
        String httpSchema = (String) path.keySet().toArray()[0];

        if (httpSchema == null || StringUtils.isEmpty(httpSchema))
            throw new RuntimeException(MsgConstants.HTTP_SCHEMA_ERROR + "httpSchema: " + httpSchema);
        String responseStr = null;


        Map<String, String> headers = new HashMap<String, String>();
        //（必填）根据期望的Response内容类型设置
        if (StringUtils.isNotBlank(accept)) {
            headers.put(HttpHeader.HTTP_HEADER_ACCEPT, accept);
        } else {
            headers.put(HttpHeader.HTTP_HEADER_ACCEPT, "*/*");
        }
        //（POST/PUT请求必选）请求Body内容格式请求的与实体对应的MIME信息
        if (StringUtils.isNotBlank(contentType)) {
            headers.put(HttpHeader.HTTP_HEADER_CONTENT_TYPE, contentType);
        } else {
            headers.put(HttpHeader.HTTP_HEADER_CONTENT_TYPE, ContentType.CONTENT_TYPE_TEXT);
        }
        Request request = new Request(Method.POST_STRING, httpSchema + artemisConfig.getHost(),
                path.get(httpSchema), artemisConfig.getAppKey(), artemisConfig.getAppSecret(), Constants.DEFAULT_TIMEOUT);
        request.setHeaders(headers);
        //请求的query
        request.setQuerys(querys);
        //请求的bodyString
        request.setStringBody(body);
        //调用服务端
        Response response = execute(request);

        return getResponseResult(response);

    }

    /**
     * postString请求
     *
     * @param artemisConfig 请求路径，请求时合作方aksk参数封装类
     * @param path        artemis配置的get请求的路径 是一个数组长度为1的Hashmap集合，只存一组数据，key为http的请求方式，value为host后面的path路径。
     * @param body        postString String请求体
     * @param querys      map类型  post请求的url查询参数（url中的query参数,没有就是为空）形如 "?aa=1&&bb=2"形式参数变成map键值对 query.put("aa","1");query.put("bb","2")
     * @param accept      指定客户端能够接收的内容类型，该参数传空时的默认全部类型接受
     * @param contentType 请求的与实体对应的MIME信息，该参数传空时的取默认值("application/x-www-form-urlencoded; charset=UTF-8")
     * @param header 请求参数有header以map的方式,没有则为null
     * @return  POST json请求类型图片下载接口  HttpResponse类型
     */
    public static HttpResponse doPostStringImgArtemis(ArtemisConfig artemisConfig, Map<String, String> path, String body,
                                                      Map<String, String> querys, String accept, String contentType, Map<String, String> header) throws Exception {
        /**
         * 根据传入的path获取是请求是http还是https
         */
        String httpSchema = (String) path.keySet().toArray()[0];

        if (httpSchema == null || StringUtils.isEmpty(httpSchema))
            throw new RuntimeException(MsgConstants.HTTP_SCHEMA_ERROR + "httpSchema: " + httpSchema);
        HttpResponse responseStr = null;

        Map<String, String> headers = new HashMap<String, String>();
        //（必填）根据期望的Response内容类型设置
        if (StringUtils.isNotBlank(accept)) {
            headers.put(HttpHeader.HTTP_HEADER_ACCEPT, accept);
        } else {
            headers.put(HttpHeader.HTTP_HEADER_ACCEPT, "*/*");
        }
        //（POST/PUT请求必选）请求Body内容格式请求的与实体对应的MIME信息
        if (StringUtils.isNotBlank(contentType)) {
            headers.put(HttpHeader.HTTP_HEADER_CONTENT_TYPE, contentType);
        } else {
            headers.put(HttpHeader.HTTP_HEADER_CONTENT_TYPE, ContentType.CONTENT_TYPE_TEXT);
        }
        if(header!=null){
            headers.putAll(header);
        }
        Request request = new Request(Method.POST_STRING_RESPONSE, httpSchema + artemisConfig.getHost(),
                path.get(httpSchema), artemisConfig.getAppKey(), artemisConfig.getAppSecret(), Constants.DEFAULT_TIMEOUT);
        request.setHeaders(headers);
        //请求的query
        request.setQuerys(querys);
        //请求的bodyString
        request.setStringBody(body);
        //调用服务端
        Response response = execute(request);

        return response.getResponse();

    }

    /**
     * postBytes请求
     *
     * @param artemisConfig 请求路径，请求时合作方aksk参数封装类
     * @param path        artemis配置的get请求的路径 是一个数组长度为1的Hashmap集合，只存一组数据，key为http的请求方式，value为host后面的path路径。
     * @param bytesBody   请求体，byte字节
     * @param querys      map类型  post请求的url查询参数（url中的query参数,没有就是为空）形如 "?aa=1&&bb=2"形式参数变成map键值对 query.put("aa","1");query.put("bb","2")
     * @param accept      指定客户端能够接收的内容类型，该参数传空时的默认全部类型接受
     * @param contentType 请求的与实体对应的MIME信息，该参数传空时的取默认值("application/text; charset=UTF-8")
     */
    public static String doPostBytesArtemis(ArtemisConfig artemisConfig, Map<String, String> path, byte[] bytesBody, Map<String, String> querys, String accept, String contentType, Map<String, String> header) throws Exception {
        /**
         * 根据传入的path获取是请求是http还是https
         */
        String httpSchema = (String) path.keySet().toArray()[0];

        if (httpSchema == null || StringUtils.isEmpty(httpSchema))
            throw new RuntimeException(MsgConstants.HTTP_SCHEMA_ERROR + "httpSchema: " + httpSchema);

        String responseStr = null;

        Map<String, String> headers = new HashMap<String, String>();
        //（必填）根据期望的Response内容类型设置
        if (StringUtils.isNotBlank(accept)) {
            headers.put(HttpHeader.HTTP_HEADER_ACCEPT, accept);
        } else {
            headers.put(HttpHeader.HTTP_HEADER_ACCEPT, "*/*");
        }
        //（可选）Body MD5,服务端会校验Body内容是否被篡改,建议Body非Form表单时添加此Header
        if (bytesBody != null) {
            headers.put(HttpHeader.HTTP_HEADER_CONTENT_MD5, MessageDigestUtil.base64AndMD5(bytesBody));
        }
        //（POST/PUT请求必选）请求Body内容格式
        if (StringUtils.isNotBlank(contentType)) {
            headers.put(HttpHeader.HTTP_HEADER_CONTENT_TYPE, contentType);
        } else {
            headers.put(HttpHeader.HTTP_HEADER_CONTENT_TYPE, ContentType.CONTENT_TYPE_TEXT);
        }
        if(header!=null){
            headers.putAll(header);
        }
        Request request = new Request(Method.POST_BYTES,httpSchema + artemisConfig.getHost(),
                path.get(httpSchema), artemisConfig.getAppKey(), artemisConfig.getAppSecret(), Constants.DEFAULT_TIMEOUT);
        request.setHeaders(headers);
        //请求的query
        request.setQuerys(querys);
        request.setBytesBody(bytesBody);
        //调用服务端
        Response response = execute(request);

        return getResponseResult(response);
    }


    /**
     * postBytes请求
     *
     * @param artemisConfig 请求路径，请求时合作方aksk参数封装类
     * @param path        artemis配置的get请求的路径 是一个数组长度为1的Hashmap集合，只存一组数据，key为http的请求方式，value为host后面的path路径。
     * @param bytesBody   请求体，byte字节
     * @param querys      map类型  post请求的url查询参数（url中的query参数,没有就是为空）形如 "?aa=1&&bb=2"形式参数变成map键值对 query.put("aa","1");query.put("bb","2")
     * @param accept      指定客户端能够接收的内容类型，该参数传空时的默认全部类型接受
     * @param contentType 请求的与实体对应的MIME信息，该参数传空时的取默认值("application/text; charset=UTF-8")
     */
    public static String doPostBytesArtemis(ArtemisConfig artemisConfig, Map<String, String> path, byte[] bytesBody, Map<String, String> querys, String accept, String contentType) throws Exception {
        /**
         * 根据传入的path获取是请求是http还是https
         */
        String httpSchema = (String) path.keySet().toArray()[0];

        if (httpSchema == null || StringUtils.isEmpty(httpSchema))
            throw new RuntimeException(MsgConstants.HTTP_SCHEMA_ERROR + "httpSchema: " + httpSchema);

        String responseStr = null;

        Map<String, String> headers = new HashMap<String, String>();
        //（必填）根据期望的Response内容类型设置
        if (StringUtils.isNotBlank(accept)) {
            headers.put(HttpHeader.HTTP_HEADER_ACCEPT, accept);
        } else {
            headers.put(HttpHeader.HTTP_HEADER_ACCEPT, "*/*");
        }
        //（可选）Body MD5,服务端会校验Body内容是否被篡改,建议Body非Form表单时添加此Header
        if (bytesBody != null) {
            headers.put(HttpHeader.HTTP_HEADER_CONTENT_MD5, MessageDigestUtil.base64AndMD5(bytesBody));
        }
        //（POST/PUT请求必选）请求Body内容格式
        if (StringUtils.isNotBlank(contentType)) {
            headers.put(HttpHeader.HTTP_HEADER_CONTENT_TYPE, contentType);
        } else {
            headers.put(HttpHeader.HTTP_HEADER_CONTENT_TYPE, ContentType.CONTENT_TYPE_TEXT);
        }
        Request request = new Request(Method.POST_BYTES,httpSchema + artemisConfig.getHost(),
                path.get(httpSchema), artemisConfig.getAppKey(), artemisConfig.getAppSecret(), Constants.DEFAULT_TIMEOUT);
        request.setHeaders(headers);
        //请求的query
        request.setQuerys(querys);
        request.setBytesBody(bytesBody);
        //调用服务端
        Response response = execute(request);

        return getResponseResult(response);
    }

    /**
     * postFile请求
     *
     * @param artemisConfig 请求路径，请求时合作方aksk参数封装类
     * @param path        artemis配置的get请求的路径 是一个数组长度为1的Hashmap集合，只存一组数据，key为http的请求方式，value为host后面的path路径。
     * @param paramMap    form表单请求参数，包含文件
     * @param querys      map类型  post请求的url查询参数（url中的query参数,没有就是为空）形如 "?aa=1&&bb=2"形式参数变成map键值对 query.put("aa","1");query.put("bb","2")
     * @param accept      指定客户端能够接收的内容类型，该参数传空时的默认全部类型接受
     * @param contentType 请求的与实体对应的MIME信息，该参数传空时的取默认值("application/text; charset=UTF-8")
     */
    public static String doPostFileFormArtemis(ArtemisConfig artemisConfig, Map<String, String> path, Map<String, Object> paramMap, Map<String, String> querys, String accept, String contentType) throws Exception{
        /**
         * 根据传入的path获取是请求是http还是https
         */
        String httpSchema = (String) path.keySet().toArray()[0];

        if (httpSchema == null || StringUtils.isEmpty(httpSchema))
            throw new RuntimeException(MsgConstants.HTTP_SCHEMA_ERROR + "httpSchema: " + httpSchema);
        String responseStr = null;

        Map<String, String> headers = new HashMap<String, String>();
        //（必填）根据期望的Response内容类型设置
        if (StringUtils.isNotBlank(accept)) {
            headers.put(HttpHeader.HTTP_HEADER_ACCEPT, accept);
        } else {
            headers.put(HttpHeader.HTTP_HEADER_ACCEPT, "*/*");
        }
        //请求的与实体对应的MIME信息
        if (StringUtils.isNotBlank(contentType)) {
            headers.put(HttpHeader.HTTP_HEADER_CONTENT_TYPE, contentType);
        } else {
            headers.put(HttpHeader.HTTP_HEADER_CONTENT_TYPE, ContentType.CONTENT_TYPE_FILE_FORM);
        }
        Request request = new Request(Method.POST_FILE, httpSchema + artemisConfig.getHost(),
                path.get(httpSchema), artemisConfig.getAppKey(), artemisConfig.getAppSecret(), Constants.DEFAULT_TIMEOUT);
        request.setHeaders(headers);
        //postForm请求的query参数
        request.setQuerys(querys);
        //postForm请求的表单参数
        request.setBodys(paramMap);
        //调用服务端
        Response response = execute(request);

        return getResponseResult(response);
    }

    /**
     * putString请求
     *
     * @param path        artemis配置的get请求的路径 是一个数组长度为1的Hashmap集合，只存一组数据，key为http的请求方式，value为host后面的path路径。
     * @param body        putString String请求体
     * @param accept      指定客户端能够接收的内容类型，该参数传空时的默认全部类型接受
     * @param contentType 请求的与实体对应的MIME信息，该参数传空时的取默认值("application/text; charset=UTF-8")
     */
    public static String doPutStringArtemis(ArtemisConfig artemisConfig, Map<String, String> path, String body, String accept, String contentType) throws Exception {
        /**
         * 根据传入的path获取是请求是http还是https
         */
        String httpSchema = (String) path.keySet().toArray()[0];

        if (httpSchema == null || StringUtils.isEmpty(httpSchema))
            throw new RuntimeException(MsgConstants.HTTP_SCHEMA_ERROR + "httpSchema: " + httpSchema);

        String responseStr = null;

        Map<String, String> headers = new HashMap<String, String>();
        //（必填）根据期望的Response内容类型设置
        if (StringUtils.isNotBlank(accept)) {
            headers.put(HttpHeader.HTTP_HEADER_ACCEPT, accept);
        } else {
            headers.put(HttpHeader.HTTP_HEADER_ACCEPT, "*/*");
        }
        //（可选）Body MD5,服务端会校验Body内容是否被篡改,建议Body非Form表单时添加此Header
        if (StringUtils.isNotBlank(body)) {
            headers.put(HttpHeader.HTTP_HEADER_CONTENT_MD5, MessageDigestUtil.base64AndMD5(body));
        }
        //（POST/PUT请求必选）请求Body内容格式
        if (StringUtils.isNotBlank(contentType)) {
            headers.put(HttpHeader.HTTP_HEADER_CONTENT_TYPE, contentType);
        } else {
            headers.put(HttpHeader.HTTP_HEADER_CONTENT_TYPE, ContentType.CONTENT_TYPE_TEXT);
        }
        Request request = new Request(Method.PUT_STRING, httpSchema + artemisConfig.host,
                path.get(httpSchema), artemisConfig.appKey, artemisConfig.appSecret, Constants.DEFAULT_TIMEOUT);
        request.setHeaders(headers);
        request.setStringBody(body);
        //调用服务端
        Response response = execute(request);

        return getResponseResult(response);
    }

    /**
     * putBytes请求
     *
     * @param path        artemis配置的putBytes请求的路径
     * @param bytesBody   请求体，byte字节
     * @param accept      指定客户端能够接收的内容类型，该参数传空时的默认全部类型接受
     * @param contentType 请求的与实体对应的MIME信息，该参数传空时的取默认值("application/text; charset=UTF-8")
     */
    public static String doPutBytesArtemis(ArtemisConfig artemisConfig, Map<String, String> path, byte[] bytesBody, String accept, String contentType) throws Exception {

        /**
         * 根据传入的path获取是请求是http还是https
         */
        String httpSchema = (String) path.keySet().toArray()[0];

        if (httpSchema == null || StringUtils.isEmpty(httpSchema))
            throw new RuntimeException(MsgConstants.HTTP_SCHEMA_ERROR + "httpSchema: " + httpSchema);

        String responseStr = null;

        Map<String, String> headers = new HashMap<String, String>();
        //（必填）根据期望的Response内容类型设置
        if (StringUtils.isNotBlank(accept)) {
            headers.put(HttpHeader.HTTP_HEADER_ACCEPT, accept);
        } else {
            headers.put(HttpHeader.HTTP_HEADER_ACCEPT, "*/*");
        }
        //（可选）Body MD5,服务端会校验Body内容是否被篡改,建议Body非Form表单时添加此Header
        if (bytesBody != null) {
            headers.put(HttpHeader.HTTP_HEADER_CONTENT_MD5, MessageDigestUtil.base64AndMD5(bytesBody));
        }
        //（POST/PUT请求必选）请求Body内容格式
        if (StringUtils.isNotBlank(contentType)) {
            headers.put(HttpHeader.HTTP_HEADER_CONTENT_TYPE, contentType);
        } else {
            headers.put(HttpHeader.HTTP_HEADER_CONTENT_TYPE, ContentType.CONTENT_TYPE_TEXT);
        }
        Request request = new Request(Method.PUT_BYTES, httpSchema + artemisConfig.host,
                path.get(httpSchema), artemisConfig.appKey, artemisConfig.appSecret, Constants.DEFAULT_TIMEOUT);
        request.setHeaders(headers);
        request.setBytesBody(bytesBody);
        //调用服务端
        Response response = execute(request);

        return getResponseResult(response);
    }

    /**
     * delete请求
     *
     * @param path        artemis配置的get请求的路径 是一个数组长度为1的Hashmap集合，只存一组数据，key为http的请求方式，value为host后面的path路径。
     * @param querys      map类型  delete请求的url查询参数（url中的query参数,没有就是为空）形如 "?aa=1&&bb=2"形式参数变成map键值对 query.put("aa","1");query.put("bb","2")
     * @param accept      指定客户端能够接收的内容类型，该参数传空时的默认全部类型接受
     * @param contentType 请求的与实体对应的MIME信息，该参数传空时的取默认值
     */
    public static String doDeleteArtemis(ArtemisConfig artemisConfig, Map<String, String> path, Map<String, String> querys, String accept, String contentType) throws Exception {
        /**
         * 根据传入的path获取是请求是http还是https
         */
        String httpSchema = (String) path.keySet().toArray()[0];

        if (httpSchema == null || StringUtils.isEmpty(httpSchema))
            throw new RuntimeException(MsgConstants.HTTP_SCHEMA_ERROR + "httpSchema: " + httpSchema);

        String responseStr = null;

        Map<String, String> headers = new HashMap<String, String>();
        //（必填）根据期望的Response内容类型设置
        if (StringUtils.isNotBlank(accept)) {
            headers.put(HttpHeader.HTTP_HEADER_ACCEPT, accept);
        } else {
            headers.put(HttpHeader.HTTP_HEADER_ACCEPT, "*/*");
        }
        //请求的与实体对应的MIME信息
        if (StringUtils.isNotBlank(contentType)) {
            headers.put(HttpHeader.HTTP_HEADER_CONTENT_TYPE, contentType);
        }
        Request request = new Request(Method.DELETE, httpSchema + artemisConfig.host,
                path.get(httpSchema), artemisConfig.appKey, artemisConfig.appSecret, Constants.DEFAULT_TIMEOUT);
        request.setHeaders(headers);
        request.setQuerys(querys);
        //调用服务端
        Response response = execute(request);
        return getResponseResult(response);
    }

    /**
     * response 获取body内容
     *
     * @param response
     */
    private static String getResponseResult(Response response) {
        String responseStr = null;

        int statusCode = response.getStatusCode();
        //调用Artemis网关成功
        if (String.valueOf(statusCode).startsWith(SUCC_PRE) || String.valueOf(statusCode).startsWith(REDIRECT_PRE)) {
            responseStr = response.getBody();
        } else {
            String msg = response.getErrorMessage();
            responseStr = response.getBody();
        }
        return responseStr;
    }

    private static Response execute(Request request) throws Exception {
        long startTime = 0;
//        if (LOGGER.isDebugEnabled()) {
//            LOGGER.debug("artemis request info: {}", request);
//            startTime = System.currentTimeMillis();
//            LOGGER.debug("Start request...");
//        }
        //调用服务端
        Response response = Client.execute(request);
//        if (LOGGER.isDebugEnabled()) {
//            long time = System.currentTimeMillis() - startTime;
//            LOGGER.debug("Complete the request,  time is : {} ms ", time);
//        }
        return response;
    }

}
