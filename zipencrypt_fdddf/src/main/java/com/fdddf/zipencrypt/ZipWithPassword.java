package com.fdddf.zipencrypt;

import com.netease.lowcode.core.annotation.NaslLogic;
import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.ZipParameters;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Component
public class ZipWithPassword {

    // Map MIME types to common file extensions
    private static final Map<String, String> mimeToExtensionMap = new HashMap<>();

    static {
        mimeToExtensionMap.put("application/pdf", ".pdf");
        mimeToExtensionMap.put("image/jpeg", ".jpg");
        mimeToExtensionMap.put("image/png", ".png");
        mimeToExtensionMap.put("text/plain", ".txt");
        mimeToExtensionMap.put("application/zip", ".zip");
        mimeToExtensionMap.put("application/msword", ".doc");
        mimeToExtensionMap.put("application/vnd.openxmlformats-officedocument.wordprocessingml.document", ".docx");
        mimeToExtensionMap.put("application/vnd.ms-excel", ".xls");
        mimeToExtensionMap.put("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", ".xlsx");
        mimeToExtensionMap.put("application/json", ".json");
        mimeToExtensionMap.put("text/html", ".html");
    }

    @Resource
    private ZipConfig cfg;

    public void setCfg(ZipConfig cfg) {
        this.cfg = cfg;
    }

    private static final Logger LCAP_LOGGER = LoggerFactory.getLogger("LCAP_EXTENSION_LOGGER");

    public static String printLog(String str) {
        LCAP_LOGGER.info(str);
        return str;
    }

    /**
     * 异步执行压缩文件
     *
     * @param password        自定义密码
     * @param destPath        压缩文件路径名，传文件名，如demo.zip
     * @param filesToCompress 文件路径列表,
     * @param needSplit       是否需要分割压缩文件
     * @param callback        回调函数
     * @return Boolean
     */
    @NaslLogic
    public Boolean asyncCompressFiles(String password, String destPath, List<String> filesToCompress, Boolean needSplit,
                                      Function<FileUploadResponse, Boolean> callback
    ) {
        Thread compressThread = new Thread(() -> {
            try {
                FileUploadResponse response = doCompressFiles(password, destPath, filesToCompress, needSplit);
                printLog("File uploaded successfully: " + response.filePath);
                callback.apply(response);
            } catch (Exception e) {
                printLog("Error occurred during compression: " + e.getMessage());
            }
        });
        compressThread.start();
        return true;
    }

    protected FileUploadResponse doCompressFiles(String password, String destPath, List<String> filesToCompress, Boolean needSplit) {
        try {
            if (filesToCompress == null || filesToCompress.isEmpty()) {
                throw new IllegalArgumentException("No files need to zip");
            }

            // check the destPath if it exists
            if (destPath == null || destPath.isEmpty()) {
                throw new IllegalArgumentException("Destination path cannot be empty.");
            }
            // throw error if destPath exist
            if (new File(destPath).exists()) {
                // delete
                Files.delete(new File(destPath).toPath());
            }
            if (!destPath.endsWith(".zip")) {
                destPath += ".zip";
            }
            String uploadUrl = cfg.appUrl + "/upload";

            // Use try-with-resources to ensure the ZipFile is properly closed
            try (ZipFile zipFile = createZipFile(password, destPath, cfg)) {
                // Set the parameters for compression
                ZipParameters zipParameters = new ZipParameters();
                zipParameters.setEncryptFiles(true);
                zipParameters.setEncryptionMethod(cfg.getEncryptionMethod());
                zipParameters.setCompressionLevel(cfg.getCompressLevel());
                zipParameters.setCompressionMethod(cfg.getCompressMethod());

                // Add files to the ZIP archive
                List<File> files = new ArrayList<>();
                List<File> folders = new ArrayList<>();
                for (String filePath : filesToCompress) {
                    File file = new File(filePath);
                    if (file.isDirectory()) {
                        folders.add(file);
                    } else {
                        file = downloadFileFromUrlIfNeeded(filePath);
                        files.add(file);
                    }
                }

                // call calculateTotalSize
                long totalSize = calculateTotalSize(filesToCompress);
                printLog("Total size of files to compress: " + totalSize);

                if (needSplit && totalSize > cfg.splitSize) {
                    printLog("Zip file size exceeds split size, splitting zip file");
                    deleteExistingSplitFiles(destPath);

                    if (folders.size() == 1) {
                        zipFile.createSplitZipFileFromFolder(folders.get(0), zipParameters, true, cfg.splitSize);
                        printLog("Split file in folder");
                    } else if (folders.size() > 1) {
                        printLog("Split zip file is not supported for multiple folders");
                    } else {
                        if (files.isEmpty()) {
                            throw new IllegalArgumentException("No files need to zip");
                        }
                        zipFile.createSplitZipFile(files, zipParameters, true, cfg.splitSize);
                        printLog("Split zip files");
                    }
                    List<File> splitZipFiles = zipFile.getSplitZipFiles();
                    FileUploadResponse result = new FileUploadResponse();
                    for (File splitZipFile : splitZipFiles) {
                        String path = splitZipFile.getAbsolutePath();
                        printLog("Uploading split zip file: " + path);
                        try {
                            FileUploadResponse resp = FileUploader.doUpload(uploadUrl, path);
                            result.filePaths.add(resp.result);
                            result.results.add(resp.result);
                        } catch (Exception e) {
                            printLog("Error uploading split zip file: " + e.getMessage());
                        }
                    }
                    if (!result.filePaths.isEmpty()) {
                        result.code = 200;
                        result.success = true;
                        result.msg = "Split zip files uploaded successfully";
                    }
                    return result;

                } else {
                    if (files.isEmpty() && folders.isEmpty()) {
                        throw new IllegalArgumentException("No files or folders need to zip");
                    }
                    if (!files.isEmpty()) {
                        zipFile.addFiles(files, zipParameters);
                    }
                    for (File folder : folders) {
                        zipFile.addFolder(folder, zipParameters);
                    }
                    printLog("Files successfully compressed to " + destPath + " with password");
                    // upload the zipfile to backend
                    return FileUploader.doUpload(uploadUrl, destPath);
                }

            } catch (ZipException e) {
                printLog("Error compressing files: " + e.getMessage());
                e.printStackTrace();
                throw e;
            }
        } catch (Exception e) {
            throw new ZipRuntimeException(e);
        }
    }

    /**
     * 压缩文件并加密
     *
     * @param password        自定义密码
     * @param destPath        压缩文件名 如 demo.zip
     * @param filesToCompress 需要压缩的文件列表
     * @param needSplit       是否需要分割文件, 默认为false
     * @return response
     */
    @NaslLogic
    public FileUploadResponse compressFiles(String password, String destPath, List<String> filesToCompress, Boolean needSplit) {
        return doCompressFiles(password, destPath, filesToCompress, needSplit);
    }

    private void deleteExistingSplitFiles(String destPath) {
        File destFile = new File(destPath);
        File parentDir = destFile.getParentFile();

        // If parentDir is null, use the current directory as the parent
        if (parentDir == null) {
            parentDir = new File(".");
        }

        String baseName = new File(destPath).getName().replaceAll("\\.zip$", "");

        if (parentDir.exists()) {
            File[] existingFiles = parentDir.listFiles((dir, name) -> name.matches(baseName + "\\.z\\d{2}"));

            if (existingFiles != null) {
                for (File file : existingFiles) {
                    if (!file.delete()) {
                        printLog("Could not delete existing split file: " + file.getAbsolutePath());
                    }
                }
            }
        }
    }

    private static ZipFile createZipFile(String password, String destPath, ZipConfig config) {
        if (destPath == null || destPath.isEmpty()) {
            throw new IllegalArgumentException("Destination path cannot be empty.");
        }
        if (config.defaultPassword == null) {
            throw new IllegalArgumentException("Default password cannot be empty.");
        }
        String pwd = config.defaultPassword;
        if (password != null && !password.isEmpty()) {
            pwd = password;
        }
        return new ZipFile(destPath, pwd.toCharArray());
    }

    private static File downloadFileFromUrlIfNeeded(String filePath) throws IOException, NoSuchAlgorithmException {
        // Check if the input is a URL
        if (filePath.startsWith("http://") || filePath.startsWith("https://")) {
            // Determine the file extension from the URL, if present
            String fileExtension = getFileExtensionFromMimeType(filePath);
            String hashedFileName = hashString(filePath) + fileExtension;

            File tempFile = new File(System.getProperty("java.io.tmpdir"), hashedFileName);
            // check the file if it exists
            if (tempFile.exists()) {
                return tempFile;
            }
            // Download the file only if it doesn't exist in the cache
            try (InputStream in = new URL(filePath).openStream()) {
                Files.copy(in, tempFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            }
            return tempFile;
        } else {
            return new File(filePath);
        }
    }

    private static String getFileExtensionFromMimeType(String fileUrl) {
        try {
            URL url = new URL(fileUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("HEAD");
            String contentType = connection.getContentType();
            connection.disconnect();

            if (contentType != null && mimeToExtensionMap.containsKey(contentType)) {
                return mimeToExtensionMap.get(contentType);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        String fileName = fileUrl.substring(fileUrl.lastIndexOf('/') + 1);
        if (fileName.contains(".") && !fileName.endsWith(".")) {
            return fileName.substring(fileName.lastIndexOf('.'));
        }
        return ".tmp"; // Default extension if MIME type cannot be determined
    }

    private static long calculateTotalSize(List<String> pathsToCompress) throws IOException, NoSuchAlgorithmException {
        long totalSize = 0;

        for (String path : pathsToCompress) {
            File file = new File(path);
            if (file.isDirectory()) {
                totalSize += calculateFolderSize(file);
            } else {
                file = downloadFileFromUrlIfNeeded(path);
                totalSize += file.length();
            }
        }

        return totalSize;
    }

    private static long calculateFolderSize(File folder) {
        long size = 0;
        File[] files = folder.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    size += calculateFolderSize(file);
                } else {
                    size += file.length();
                }
            }
        }
        return size;
    }

    private static String hashString(String input) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] hashBytes = md.digest(input.getBytes());
        StringBuilder sb = new StringBuilder();

        for (byte b : hashBytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    public static void main(String[] args) {
        // Example usage
        ZipWithPassword zp = new ZipWithPassword();
        ZipConfig config = new ZipConfig();
        config.appUrl = "http://localhost:8080";
        config.defaultPassword = "111111";
        config.encryptionMethod = 0;
        zp.setCfg(config);

        List<String> filesToCompress = new ArrayList<>();
        filesToCompress.add("/Users/xxx/Downloads/fdad6921014a267474df2fd9858edb96.JPG");
//        filesToCompress.add("/Users/xxx/Downloads/0c09a1e392aa4e452cf219a4ddc7a357860900ba_upscayl_4x_realesrgan-x4fast.png");
        filesToCompress.add("/Users/xxx/Downloads/AVG人物-1576226974445.rar");
//        filesToCompress.add("https://nimg.ws.126.net/?url=http%3A%2F%2Fdingyue.ws.126.net%2F2024%2F0904%2Fc7caf6eaj00sja59o00abd000p003gxg.jpg&thumbnail=660x2147483647&quality=80&type=jpg");
//        filesToCompress.add("https://www.zixi.org/assets/uploads/202409/20240904160104-I7rC9Gtg.jpg");
//        filesToCompress.add("/Users/xxx/Downloads/zipencrypt_fdddf/");

        zp.compressFiles("222222", "compressed.zip", filesToCompress, true);
    }
}
