package com.yourcompany.ftp.file.utils.api;

import com.netease.lowcode.core.annotation.NaslLogic;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.util.Base64;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 * FTP文件前缀匹配下载最新文件工具
 */
@Service
public class FtpDownloadLatestByPrefixUtilsService {

    private static final Logger LCAP_LOGGER = LoggerFactory.getLogger("LCAP_EXTENSION_LOGGER");

    /**
     * 根据文件名前缀下载创建时间最新的文件并返回Base64编码的内容
     *
     * @param ftpUrl     完整FTP地址 (包含用户名、密码、主机、端口和路径)
     * @param filePrefix 文件名前缀
     * @return 成功返回Map，Key为文件名，Value为文件的Base64编码字符串；如果未找到文件、连接失败或发生异常返回null
     */
    @NaslLogic
    public Map<String, String> downloadLatestFileByPrefix(String ftpUrl, String filePrefix) {
        if (ftpUrl == null || filePrefix == null) {
            LCAP_LOGGER.error("输入参数不能为空");
            return null;
        }

        FTPClient ftpClient = new FTPClient();
        // 设置字符集为UTF-8，防止中文文件名乱码
        ftpClient.setControlEncoding("UTF-8");
        Map<String, String> resultMap = new HashMap<>();

        try {
            URI uri = new URI(ftpUrl);
            String host = uri.getHost();
            int port = uri.getPort() == -1 ? 21 : uri.getPort();
            String userInfo = uri.getUserInfo();
            String username = null;
            String password = null;
            if (userInfo != null && userInfo.contains(":")) {
                String[] parts = userInfo.split(":");
                username = parts[0];
                password = parts[1];
            }

            String path = uri.getPath();

            LCAP_LOGGER.info("正在连接到FTP服务器: " + host + ":" + port);
            ftpClient.connect(host, port);
            int reply = ftpClient.getReplyCode();
            if (!FTPReply.isPositiveCompletion(reply)) {
                ftpClient.disconnect();
                LCAP_LOGGER.error("FTP服务器拒绝连接。响应码: " + reply);
                return null;
            }

            if (username != null && password != null) {
                if (!ftpClient.login(username, password)) {
                    LCAP_LOGGER.error("FTP登录失败，用户: " + username);
                    return null;
                }
            }

            ftpClient.enterLocalPassiveMode();
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);

            // 切换目录
            if (path != null && !path.isEmpty() && !"/".equals(path)) {
                if (!ftpClient.changeWorkingDirectory(path)) {
                    LCAP_LOGGER.warn("切换工作目录失败: " + path + "。将在当前根目录查找文件。");
                }
            }

            // 获取文件列表并筛选最新文件
            FTPFile[] files = ftpClient.listFiles();
            FTPFile latestFile = null;

            if (files != null) {
                for (FTPFile file : files) {
                    if (file.isFile() && file.getName().startsWith(filePrefix)) {
                        if (latestFile == null) {
                            latestFile = file;
                        } else {
                            Calendar t1 = file.getTimestamp();
                            Calendar t2 = latestFile.getTimestamp();
                            if (t1 != null && t2 != null && t1.after(t2)) {
                                latestFile = file;
                            }
                        }
                    }
                }
            }

            if (latestFile != null) {
                String fileName = latestFile.getName();
                LCAP_LOGGER.info("找到最新文件: " + fileName + ", 时间: " + (latestFile.getTimestamp() != null ? latestFile.getTimestamp().getTime() : "未知"));

                // 下载文件
                try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
                    boolean downloaded = ftpClient.retrieveFile(fileName, outputStream);
                    if (downloaded) {
                        LCAP_LOGGER.info("文件下载成功: " + fileName);
                        byte[] fileBytes = outputStream.toByteArray();
                        String base64Content = Base64.getEncoder().encodeToString(fileBytes);
                        resultMap.put(fileName, base64Content);
                    } else {
                        LCAP_LOGGER.error("文件下载失败: " + fileName + "，FTP响应: " + ftpClient.getReplyString());
                    }
                } catch (IOException e) {
                    LCAP_LOGGER.error("下载文件 " + fileName + " 时发生IO异常: " + e.getMessage());
                }
            } else {
                 LCAP_LOGGER.warn("未找到前缀为 " + filePrefix + " 的文件");
            }

            return resultMap;

        } catch (Exception e) {
            LCAP_LOGGER.error("FTP下载最新文件异常: " + e.getMessage(), e);
            return null;
        } finally {
            if (ftpClient.isConnected()) {
                try {
                    ftpClient.logout();
                    ftpClient.disconnect();
                } catch (IOException ex) {
                    // 忽略
                }
            }
        }
    }
}
