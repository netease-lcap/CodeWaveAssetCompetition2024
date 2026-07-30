package com.yourcompany.ftp.file.utils.api;

import com.netease.lowcode.core.annotation.NaslLogic;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Base64;

/**
 * FTP文件上传工具库
 */
@Service
public class FtpUploadUtilsService {

    private static final Logger LCAP_LOGGER = LoggerFactory.getLogger("LCAP_EXTENSION_LOGGER");

    /**
     * 将Base64编码的文件上传到指定FTP服务器
     *
     * @param base64Content Base64编码的文件内容
     * @param ftpUrl        完整FTP地址
     * @param fileName      文件名 (如: 海关产地证.xml)
     * @return 上传成功返回true, 失败返回false
     */
    @NaslLogic
    public Boolean uploadFileToFtp(String base64Content, String ftpUrl, String fileName) {
        if (base64Content == null || ftpUrl == null || fileName == null) {
            LCAP_LOGGER.error("输入参数不能为空");
            return false;
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
                return false;
            }

            if (username != null && password != null) {
                if (!ftpClient.login(username, password)) {
                    LCAP_LOGGER.error("FTP登录失败，用户: " + username);
                    return false;
                }
            }

            ftpClient.enterLocalPassiveMode();
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);

            // 切换目录
            if (path != null && !path.isEmpty() && !"/".equals(path)) {
                if (!ftpClient.changeWorkingDirectory(path)) {
                    LCAP_LOGGER.warn("切换工作目录失败: " + path + "。尝试上传到当前根目录。");
                }
            }

            // 解码Base64
            byte[] fileBytes = Base64.getDecoder().decode(base64Content);
            try (InputStream inputStream = new ByteArrayInputStream(fileBytes)) {
                boolean uploaded = ftpClient.storeFile(fileName, inputStream);
                if (uploaded) {
                    LCAP_LOGGER.info("文件上传成功: " + fileName);
                    return true;
                } else {
                    LCAP_LOGGER.error("文件上传失败: " + fileName + "，FTP响应: " + ftpClient.getReplyString());
                    return false;
                }
            }

        } catch (Exception e) {
            LCAP_LOGGER.error("FTP上传异常: " + e.getMessage(), e);
            return false;
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
