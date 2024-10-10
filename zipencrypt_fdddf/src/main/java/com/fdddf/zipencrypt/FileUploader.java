package com.fdddf.zipencrypt;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class FileUploader {

    public static FileUploadResponse doUpload(String url, String filePath) throws IOException {
        // 创建URL对象
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        // 设置请求方法为POST
        con.setRequestMethod("POST");

        // 设置允许输出
        con.setDoOutput(true);

        // 设置请求头
        String boundary = "------" + System.currentTimeMillis();
        String contentType = "multipart/form-data; boundary=" + boundary;
        con.setRequestProperty("Content-Type", contentType);
        con.setRequestProperty("Charset", "UTF-8");

        // 写入请求体
        DataOutputStream wr = new DataOutputStream(con.getOutputStream());

        // encode the filename to utf8
        String filename = new File(filePath).getName();
        String header = "Content-Disposition: form-data; name=\"file\"; filename=\"" + filename + "\"\r\n"
                + "Content-Type: application/octet-stream\r\n\r\n";
        wr.writeBytes("--" + boundary + "\r\n");
        wr.write(header.getBytes(StandardCharsets.UTF_8));

        // 读取文件并写入请求体
        FileInputStream fileInputStream = new FileInputStream(filePath);
        byte[] buffer = new byte[4096];
        int bytesRead = -1;
        while ((bytesRead = fileInputStream.read(buffer)) != -1) {
            wr.write(buffer, 0, bytesRead);
        }
        fileInputStream.close();

        // 结束部分
        wr.writeBytes("\r\n--" + boundary + "--\r\n");
        wr.flush();
        wr.close();

        // 获取响应
        int responseCode = con.getResponseCode();
        System.out.println("Response Code : " + responseCode);

        // 读取响应并解析为JSON
        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuilder response = new StringBuilder();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        // 解析JSON响应
        ObjectMapper objectMapper = new ObjectMapper();
        // 假设服务器返回的是一个简单的JSON对象
        FileUploadResponse resp = objectMapper.readValue(response.toString(), FileUploadResponse.class);

        System.out.println("Response: " + resp.filePath);

        return resp;
    }
}
