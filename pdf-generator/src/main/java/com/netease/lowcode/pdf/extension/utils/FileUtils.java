package com.netease.lowcode.pdf.extension.utils;

import com.alibaba.fastjson2.JSON;
import okhttp3.*;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Component("pdfGeneratorFileUtils")
public class FileUtils {

    @Value("${lcp.upload.sinkType}")
    private String sinkType;
    @Value("${lcp.upload.sinkPath}")
    private String sinkPath;
    @Value("${lcp.upload.access}")
    private String access;

    @Autowired
    private ApplicationContext applicationContext;

    public static String DEFAULT_TEMPLATE_DIR = "/data/template";

    /**
     * 文件上传
     *
     * @param file
     * @return
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     * @throws FileNotFoundException
     */
    public UploadResponseDTO uploadFileV2(File file) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, FileNotFoundException {

        FileInputStream fis = new FileInputStream(file);

        Object clientManager = applicationContext.getBean("fileStorageClientManager");
        Method getFileSystemSpi = clientManager.getClass().getMethod("getFileSystemSpi", String.class);
        Object fileStorageClient = getFileSystemSpi.invoke(clientManager, sinkType);

        Method upload = fileStorageClient.getClass().getMethod("upload", InputStream.class, String.class, Map.class);
        // http://dev.exporttest.defaulttenant.lcap.codewave-dev.163yun.com/upload/app/%E5%A4%A7%E6%95%B0%E6%8D%AE%E5%AF%BC%E5%87%BA%E6%B5%8B%E8%AF%95_20240106093632186.xlsx

        // 只要拼接 sinkPath+fileName+时间+后缀即可。
        String curTime = DateFormatUtils.format(new Date(), "yyyyMMddHHmmssSSS");

        String fileName = file.getName();
        String fileExt = "";
        if (fileName.contains(".")) {
            int i = fileName.lastIndexOf(".");
            fileExt = fileName.substring(i);
            fileName = fileName.substring(0, i);
        }

        String savePath = String.join("/", sinkPath, fileName + "_" + curTime + fileExt);
        String filePath = (String) upload.invoke(fileStorageClient, fis, savePath, new HashMap<>());

        // 组装链接
        UploadResponseDTO responseDTO = new UploadResponseDTO();
        responseDTO.setFilePath("/upload" + filePath);

        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (Objects.nonNull(requestAttributes)) {
            HttpServletRequest request = requestAttributes.getRequest();
            responseDTO.setResult(request.getScheme() + "://" + request.getServerName() +
                    (80 == request.getServerPort() ? "" : ":" + request.getServerPort()) +
                    "/upload" + filePath);
        } else {
            responseDTO.setResult(responseDTO.getFilePath());
        }

        return responseDTO;
    }

    public static UploadResponseDTO uploadStream(InputStream inputStream, String fileName) throws IOException {
        HttpServletRequest httpServletRequest = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String uploadUrl = httpServletRequest.getScheme() + "://" + httpServletRequest.getServerName() + ":" +
                httpServletRequest.getServerPort() + "/upload";
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
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/octet-stream"),fileBytes);
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
            return JSON.parseObject(response.body().string(), UploadResponseDTO.class);
        }
        throw new RuntimeException(String.format("文件上传失败,%s",response));
    }

    public static File downloadFile(String urlStr) throws IOException {
        URL url = new URL(getTrueUrl(urlStr));
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setConnectTimeout(3 * 1000);
        connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/99.0.4844.82 Safari/537.36");
        InputStream inputStream = url.openStream();
        byte[] getData = readInputStream(inputStream);
        // 文件保存位置
        File saveDir = new File(DEFAULT_TEMPLATE_DIR);
        if (!saveDir.exists()) {
            // 这里可能会应为目录权限 导致无法创建目录。下面逻辑读取文件时报FileNotFoundException
            saveDir.mkdirs();
            // 这里增加校验
            if (!saveDir.exists()) {
                throw new RuntimeException(String.format("目录创建失败%s，请检查目录权限", DEFAULT_TEMPLATE_DIR));
            }
        }

        String fileName = urlStr.substring(urlStr.lastIndexOf("/") + 1, urlStr.indexOf("?") == -1 ? urlStr.length() : urlStr.indexOf("?"));
        File file = new File(saveDir + File.separator + fileName);
        if (file.exists()) file.delete();
        FileOutputStream fos = new FileOutputStream(file);
        fos.write(getData);
        if (fos != null) {
            fos.close();
        }
        if (inputStream != null) {
            inputStream.close();
        }
        return file;
    }

    public static byte[] readInputStream(InputStream inputStream) throws IOException {
        byte[] buffer = new byte[1024];
        int len = 0;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        while ((len = inputStream.read(buffer)) != -1) {
            bos.write(buffer, 0, len);
        }
        bos.close();
        return bos.toByteArray();
    }

    public static String getTrueUrl(String urlStr) throws UnsupportedEncodingException {
        int lastIndexOf = urlStr.lastIndexOf("/");
        int queryIndexOf = urlStr.indexOf("?");
        if (queryIndexOf == -1) queryIndexOf = urlStr.length();

        String prefix = urlStr.substring(0, lastIndexOf);
        String suffix = urlStr.substring(queryIndexOf);
        String fileName = urlStr.substring(lastIndexOf + 1, queryIndexOf);

        String urlFileName = getTrueFileName(fileName);
        String trueUrlStr = prefix + "/" + urlFileName + suffix;
        if (!trueUrlStr.startsWith("http") && trueUrlStr.startsWith("/upload")) {
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
            int port = request.getLocalPort();
            return "http://127.0.0.1:" + port + trueUrlStr;
        }
        return trueUrlStr;
    }

    public static String getTrueFileName(String fileName) throws UnsupportedEncodingException {
        if (fileName.equals(URLDecoder.decode(fileName, "UTF-8"))) {
            return URLEncoder.encode(fileName, "UTF-8");
        }
        return fileName;
    }

    public static void delete(File file) {
        if(file.isFile()){
            file.delete();
            return;
        }
        for (File listFile : file.listFiles()) {
            delete(listFile);
        }
    }
}

