package com.fdddf.wechat;

import okhttp3.*;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class HttpUtils {
    private static final Logger LCAP_LOGGER = LoggerFactory.getLogger("LCAP_EXTENSION_LOGGER");

    public static String httpGetMethod(String url, Map<String, String> params)
            throws IOException {
        OkHttpClient client = new OkHttpClient();
        StringBuilder paramStr = new StringBuilder("?");
        params.forEach((key, value) -> paramStr.append(key).append("=").append(value).append("&"));
        paramStr.setLength(paramStr.length() - 1);
        
        Request request = new Request.Builder().url(url + paramStr.toString()).get().build();
        try (Response response = client.newCall(request).execute()) {
            return response.body().string();
        }
    }

    public static String httpPostParams(String url, Map<String, String> params)
            throws IOException {
        OkHttpClient client = new OkHttpClient();
        StringBuilder paramStr = new StringBuilder("?");
        params.forEach((key, value) -> paramStr.append(key).append("=").append(value).append("&"));
        paramStr.setLength(paramStr.length() - 1);

        System.out.println("Testing: " + url + paramStr.toString());

        Request request = new Request.Builder().url(url + paramStr.toString())
                .post(RequestBody.create(null, new byte[0])) // Replace with an empty body
                .build();
        try (Response response = client.newCall(request).execute()) {
            return response.body().string();
        }
    }

    public static String httpPostStringParamsAndLongParams(String url, Map<String, String> stringParams, Map<String, Long> longParams)
            throws IOException {
        OkHttpClient client = new OkHttpClient();
        StringBuilder paramStr = new StringBuilder("?");
        stringParams.forEach((key, value) -> paramStr.append(key).append("=").append(value).append("&"));
        longParams.forEach((key, value) -> paramStr.append(key).append("=").append(value).append("&"));
        paramStr.setLength(paramStr.length() - 1);

        System.out.println("Testing: " + url + paramStr.toString());

        Request request = new Request.Builder().url(url + paramStr.toString())
                .post(RequestBody.create(null, new byte[0])) // Replace with an empty body
                .build();
        try (Response response = client.newCall(request).execute()) {
            return response.body().string();
        }
    }

    public static String httpPostMethod(String url, Map<String, String> params)
            throws IOException {
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .build();
        FormBody.Builder formBody = new FormBody.Builder();
        params.forEach(formBody::add);
        
        Request request = new Request.Builder().url(url).post(formBody.build()).build();
        try (Response response = client.newCall(request).execute()) {
            return response.body().string();
        }
    }

    public static String httpPostMethodWithUtf8(String url, Map<String, String> params) throws IOException {
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .build();
        FormBody.Builder formBody = new FormBody.Builder();
        params.forEach((key, value) -> {
            try {
                formBody.add(key, URLEncoder.encode(value, StandardCharsets.UTF_8.toString()));
            } catch (Exception e) {
                LCAP_LOGGER.error("Error encoding value for key: {}", key);
            }
        });

        Request request = new Request.Builder().url(url).post(formBody.build()).build();
        try (Response response = client.newCall(request).execute()) {
            return response.body().string();
        }
    }

    public static String httpPostMethod(String url, Map<String, String> headers, Map<String, String> params)
            throws IOException {
        OkHttpClient client = new OkHttpClient();
        FormBody.Builder formBody = new FormBody.Builder();
        params.forEach(formBody::add);
        
        Request request = new Request.Builder().url(url)
                .headers(Headers.of(headers))
                .post(formBody.build())
                .build();
        try (Response response = client.newCall(request).execute()) {
            return response.body().string();
        }
    }

    public static Response httpPostResponse(String url, Map<String, String> headers, String requestBodyContent) throws IOException {
        OkHttpClient client = new OkHttpClient();
        MediaType mediaType = MediaType.parse(headers.getOrDefault("Content-Type", "application/json"));
        RequestBody requestBody = RequestBody.create(mediaType, requestBodyContent);
        
        Request request = new Request.Builder().url(url)
                .headers(Headers.of(headers))
                .post(requestBody)
                .build();
        
        return client.newCall(request).execute();
    }

    public static String postMethod(String url, Map<String, String> headers, InputStream bodyContent) throws IOException {
        OkHttpClient client = new OkHttpClient();
        MediaType mediaType = MediaType.parse(headers.getOrDefault("Content-Type", "application/json"));

//        byte[] contentBytes = bodyContent.readAllBytes();
        byte[] contentBytes = toByteArray(bodyContent);
        RequestBody requestBody = RequestBody.create(mediaType, contentBytes);

        Request request = new Request.Builder().url(url)
                .headers(Headers.of(headers))
                .post(requestBody)
                .build();
        
        try (Response response = client.newCall(request).execute()) {
            return response.body().string();
        }
    }

    private static byte[] toByteArray(InputStream inputStream) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        int nRead;
        byte[] data = new byte[4096];  // Read in chunks of 4KB

        while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, nRead);
        }

        buffer.flush();
        return buffer.toByteArray();
    }

    public static String clientPostMethod(String url, Map<String, String> headers, InputStream bodyContent) throws IOException {
        HttpClient httpClient = HttpClientBuilder.create().build();
        HttpPost method = new HttpPost(url);

        headers.forEach((key, value) -> method.addHeader(new BasicHeader(key, value)));

        if (bodyContent != null) {
            method.setEntity(new InputStreamEntity(bodyContent, -1));
        }

        HttpResponse postResponse = httpClient.execute(method);
        return getResponsBodyAsString(postResponse.getEntity().getContent());
    }

    public static String clientPostMethod(String url, Map<String, String> headers) throws IOException {
        HttpClient httpClient = HttpClientBuilder.create().build();
        HttpPost method = new HttpPost(url);
        headers.forEach((key, value) -> method.addHeader(new BasicHeader(key, value)));
        
        HttpResponse postResponse = httpClient.execute(method);
        return getResponsBodyAsString(postResponse.getEntity().getContent());
    }

    private static String getResponsBodyAsString(InputStream input) throws IOException {
        StringBuilder responseBodyString = new StringBuilder();
        try (Reader reader = new InputStreamReader(input, StandardCharsets.UTF_8)) {
            char[] buffer = new char[1024];
            int len;
            while ((len = reader.read(buffer)) != -1) {
                responseBodyString.append(buffer, 0, len);
            }
        }
        return responseBodyString.toString();
    }
}