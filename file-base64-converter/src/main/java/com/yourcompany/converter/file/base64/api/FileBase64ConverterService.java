package com.yourcompany.converter.file.base64.api;

import com.netease.lowcode.core.annotation.NaslLogic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

/**
 * 文件Base64转换工具库
 */
@Service
public class FileBase64ConverterService {

    private static final Logger LCAP_LOGGER = LoggerFactory.getLogger("LCAP_EXTENSION_LOGGER");

    /**
     * 批量将网络文件URL转换为Base64编码字符串
     *
     * @param fileUrls 文件URL地址列表
     * @return Base64编码后的字符串列表
     */
    @NaslLogic
    public List<String> convertUrlListToBase64(List<String> fileUrls) {
        if (fileUrls == null) {
            return new ArrayList<>();
        }
        
        List<String> result = new ArrayList<>();
        for (String url : fileUrls) {
            result.add(convertUrlToBase64(url));
        }
        return result;
    }

    public String convertUrlToBase64(String fileUrl) {
        if (fileUrl == null || fileUrl.isEmpty()) {
            return null;
        }

        try {
            URL url = new URL(fileUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(10000);
            connection.setReadTimeout(30000);
            connection.setRequestMethod("GET");

            int responseCode = connection.getResponseCode();
            if (responseCode != HttpURLConnection.HTTP_OK) {
                LCAP_LOGGER.error("从URL下载文件失败: " + fileUrl + ", 响应码: " + responseCode);
                return null;
            }

            try (InputStream inputStream = connection.getInputStream();
                 ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }

                byte[] fileBytes = outputStream.toByteArray();
                return Base64.getEncoder().encodeToString(fileBytes);
            }

        } catch (Exception e) {
            LCAP_LOGGER.error("将URL转换为Base64时发生异常: " + e.getMessage(), e);
            return null;
        }
    }
}
