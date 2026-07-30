package com.yourcompany.ftp.file.utils.api;

import com.netease.lowcode.core.annotation.NaslLogic;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.util.Base64;

/**
 * FTP文件下载工具
 */
@Service
public class FtpDownloadUtilsService {

    private static final Logger LCAP_LOGGER = LoggerFactory.getLogger("LCAP_EXTENSION_LOGGER");

    /**
     * 从指定FTP服务器下载文件并返回Base64编码的内容
     *
     * @param ftpUrl   完整FTP地址 (包含用户名、密码、主机、端口和路径)
     * @param fileName 文件名
     * @return 成功返回文件的Base64编码字符串，失败返回null
     */
    @NaslLogic
    public String downloadFileFromFtp(String ftpUrl, String fileName) {
        if (ftpUrl == null || fileName == null) {
            LCAP_LOGGER.error("输入参数不能为空");
            return null;
        }

        FTPClient ftpClient = new FTPClient();
        // 设置字符集为UTF-8，防止中文文件名乱码
        ftpClient.setControlEncoding("UTF-8");

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
                    LCAP_LOGGER.warn("切换工作目录失败: " + path + "。尝试在当前根目录下载。");
                }
            }

            // 下载文件
            try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
                boolean downloaded = ftpClient.retrieveFile(fileName, outputStream);
                if (downloaded) {
                    LCAP_LOGGER.info("文件下载成功: " + fileName);
                    byte[] fileBytes = outputStream.toByteArray();
                    return Base64.getEncoder().encodeToString(fileBytes);
                } else {
                    LCAP_LOGGER.error("文件下载失败: " + fileName + "，FTP响应: " + ftpClient.getReplyString());
                    return null;
                }
            }

        } catch (Exception e) {
            LCAP_LOGGER.error("FTP下载异常: " + e.getMessage(), e);
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
