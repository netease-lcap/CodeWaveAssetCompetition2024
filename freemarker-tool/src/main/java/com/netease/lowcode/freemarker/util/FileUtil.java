package com.netease.lowcode.freemarker.util;

import com.netease.lowcode.freemarker.config.UploadConfig;
import com.netease.lowcode.freemarker.dto.UploadResponseDTO;
import okhttp3.*;
import org.apache.poi.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class FileUtil {

    private static final Logger logger = LoggerFactory.getLogger("LCAP_CUSTOMIZE_LOGGER");

    private static final OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(5, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .followRedirects(true)
            .followSslRedirects(true)
            .build();

    public static InputStream getFileInputStream(String urlStr) throws IOException {
        logger.info("urlStr using OkHttp: {}", urlStr);

        // 还是保留你原本的 URL 处理逻辑
        String finalUrl = getTrueUrl(urlStr);

        Request request = new Request.Builder()
                .url(finalUrl)
                .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/99.0.4844.82 Safari/537.36")
                .build();

        // 执行请求
        Response response = client.newCall(request).execute();

        if (!response.isSuccessful()) {
            // 如果失败（比如 404 或 500），必须关闭响应体并抛出异常
            response.close();
            throw new IOException("Unexpected code " + response);
        }

        ResponseBody body = response.body();
        if (body == null) {
            response.close();
            throw new IOException("Response body is null");
        }

        return body.byteStream();
    }

    // 新增方法：检测 URL 是否已经编码
    private static boolean isUrlEncoded(String url) {
        try {
            String decodedUrl = java.net.URLDecoder.decode(url, "UTF-8");
            return !decodedUrl.equals(url);
        } catch (Exception e) {
            return false;
        }
    }

    public static String getTrueUrl(String urlStr) throws UnsupportedEncodingException {
        URI uri = null;
        try {
            uri = new URI(urlStr);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        String path = uri.getPath();
        String query = uri.getQuery();
        // 获取文件名,3.11之前可以根据url中的参数获取文件名，3.11之后需要根据fileName参数获取文件名
        String fileName = query != null ? Arrays.stream(query.split("&"))
                .map(param -> param.split("="))
                .filter(keyValue -> keyValue.length == 2 && keyValue[0].equalsIgnoreCase("fileName"))
                .map(keyValue -> keyValue[1])
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("The provided URL must contain a fileName parameter.")) : path.substring(path.lastIndexOf('/') + 1);
        // 自动检测 URL 是否已经编码
        if (!isUrlEncoded(fileName)) {
            String fileNameEncoded = java.net.URLEncoder.encode(fileName, "UTF-8");
            urlStr = urlStr.replace(fileName, fileNameEncoded);
        }
        try {
            if (!urlStr.startsWith("http") && urlStr.startsWith("/upload")) {
                HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
                int port = request.getLocalPort();

                String uploadPort = UploadConfig.getUploadPort();
                if (isValidPort(uploadPort)) {
                    port = Integer.parseInt(uploadPort);
                }
                return "http://127.0.0.1:" + port + urlStr;
            }
        } catch (Exception e) {
            logger.error("urlStr转本地失败", e);
        }
        return urlStr;
    }

    public static UploadResponseDTO uploadStream(InputStream inputStream, String fileName) throws IOException {
        int port;
        String uploadPort = UploadConfig.getUploadPort();
        if (isValidPort(uploadPort)) {
            port = Integer.parseInt(uploadPort);
        } else {
            try {
                HttpServletRequest httpServletRequest = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
                port = httpServletRequest.getLocalPort();
            } catch (Exception e) {
                logger.error("获取本地port失败，默认获取8080", e);
                //兜底方案
                port = 8080;
            }
        }
        String uploadUrl = "http://127.0.0.1:" + port + "/gateway/lowcode/api/v1/app/upload";

        OkHttpClient client = new OkHttpClient();

        byte[] fileBytes;
        try (ByteArrayOutputStream buffer = new ByteArrayOutputStream()) {
            int read;
            byte[] data = new byte[1024];
            while ((read = inputStream.read(data, 0, data.length)) != -1) {
                buffer.write(data, 0, read);
            }
            buffer.flush();
            fileBytes = buffer.toByteArray();
        }
        // RequestBody requestBody = RequestBody.create(fileBytes, MediaType.parse("application/octet-stream"));
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/octet-stream"), fileBytes);
        MultipartBody multipartBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", fileName, requestBody)
                .build();

        Request request = new Request.Builder()
                .url(uploadUrl)
                .post(multipartBody)
                .build();
        Call call = client.newCall(request);
        Response response = call.execute();
        if (response.isSuccessful()) {
            return JsonUtil.fromJson(response.body().string(), UploadResponseDTO.class);
        }
        logger.error(String.format("文件上传失败,%s", response));
        throw new RuntimeException(String.format("文件上传失败,%s", response));
    }

    private static boolean isValidPort(String str) {
        if (str == null || StringUtil.isBlank(str)) {
            return false;
        }

        try {
            int port = Integer.parseInt(str);
            return port >= 1 && port <= 65535;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
