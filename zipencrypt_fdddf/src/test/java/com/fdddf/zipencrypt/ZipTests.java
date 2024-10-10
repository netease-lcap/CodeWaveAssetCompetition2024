package com.fdddf.zipencrypt;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ZipTests {

    public ZipConfig getConfig() {
        ZipConfig config = new ZipConfig();
        config.appUrl = "https://dev-testdiff-qa.app.codewave.163.com";
        config.defaultPassword = "111111";
        config.encryptionMethod = 0;
        return config;
    }

    @Test
    public void zipLocalFiles() {
        List<String> filesToCompress = new ArrayList<>();
        filesToCompress.add("/Users/hijack/Downloads/icon-256x256.png");
        filesToCompress.add("/Users/hijack/Downloads/积分.xlsx");
        ZipWithPassword zp = new ZipWithPassword();
        zp.setCfg(getConfig());
        FileUploadResponse resp = zp.compressFiles("222222", "compressed.zip", filesToCompress, true);
        System.out.println(resp);
        assertEquals(true, resp.success);
    }

    @Test
    public void zipRemoteFiles() {
        List<String> filesToCompress = new ArrayList<>();
        filesToCompress.add("https://nimg.ws.126.net/?url=http%3A%2F%2Fdingyue.ws.126.net%2F2024%2F0904%2Fc7caf6eaj00sja59o00abd000p003gxg.jpg&thumbnail=660x2147483647&quality=80&type=jpg");
        filesToCompress.add("https://www.zixi.org/assets/uploads/202409/20240904160104-I7rC9Gtg.jpg");
        ZipWithPassword zp = new ZipWithPassword();
        zp.setCfg(getConfig());
        FileUploadResponse resp = zp.compressFiles(null, "compressed.zip", filesToCompress, true);
        System.out.println(resp);
        assertEquals(true, resp.success);
    }

    @Test
    public void zipAsChinese() {
        List<String> filesToCompress = new ArrayList<>();
        filesToCompress.add("https://www.zixi.org/assets/uploads/202409/20240904160104-I7rC9Gtg.jpg");
        ZipWithPassword zp = new ZipWithPassword();
        zp.setCfg(getConfig());
        FileUploadResponse resp = zp.compressFiles(null, "中文压缩包测试.zip", filesToCompress, true);
        System.out.println(resp);
        assertEquals(true, resp.success);
    }


}
