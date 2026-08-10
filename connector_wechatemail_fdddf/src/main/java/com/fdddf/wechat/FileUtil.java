package com.fdddf.wechat;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Base64;
import java.util.UUID;

public class FileUtil {

    private static final Logger LCAP_LOGGER = LoggerFactory.getLogger("LCAP_EXTENSION_LOGGER");

    public static File downloadFile(String fileUrl, String destinationDirectory) throws IOException {
        URL url = new URL(fileUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        String extension = fileUrl.substring(fileUrl.lastIndexOf("."));

        // Generate a random file name
        String randomFileName = UUID.randomUUID() + extension;

        // Construct the full destination path
        String destinationPath = destinationDirectory + File.separator + randomFileName;

        try (InputStream inputStream = connection.getInputStream();
                FileOutputStream outputStream = new FileOutputStream(destinationPath)) {

            byte[] buffer = new byte[4096];
            int bytesRead;

            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
        }

        return new File(destinationPath);
    }

    public static String fileBase64Content(String fileUrl) {
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(fileUrl)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }

            ResponseBody responseBody = response.body();
            if (responseBody != null) {
                byte[] fileBytes = responseBody.bytes();
                return Base64.getEncoder().encodeToString(fileBytes);
            } else {
                throw new IOException("Response body is null");
            }
        } catch (IOException e) {
            LCAP_LOGGER.error("Error while sending HTTP request: {}", e.getMessage());
            throw new RuntimeException("Error while sending HTTP request");
        }
    }

    public static String getFileName(String url) {
        String name = url.substring(url.lastIndexOf("/") + 1);
        if (name.contains("?")) {
            name = name.substring(0, name.indexOf("?"));
        }
        return name;
    }
}
