package com.netease.http.web.controller;

import com.netease.http.dto.UploadResponseDTO;
import com.netease.http.httpclient.LCAPHttpClient;
import com.netease.http.util.FileUtil;
import com.netease.http.web.dto.ApiReturn;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.util.Objects;

@RestController
@RequestMapping("/expand/file")
public class ExtHttpFileUploadController {
    @Autowired
    private FileUtil httpClientFileUtils;

    @Autowired
    private LCAPHttpClient lcapHttpClient;

    /**
     * 上传文件
     *
     * @param file
     * @return 文件url
     */
    @PostMapping("/upload")
    public ApiReturn<String> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            UploadResponseDTO uploadResponseDTO = httpClientFileUtils.uploadStream(new ByteArrayInputStream(file.getBytes()), Objects.requireNonNull(file.getOriginalFilename()));
            if (uploadResponseDTO != null) {
                return ApiReturn.of(uploadResponseDTO.getResult());
            }
            throw new Exception("上传失败");
        } catch (Exception e) {
            return ApiReturn.of(null, -1, e.getMessage());
        }
    }


    /**
     * 下载文件
     */
    @PostMapping("/download")
    public void downloadFile(@RequestParam("fileUrl") String fileUrl, @RequestParam(required = false) String fileName) {
        lcapHttpClient.downloadFileToResponse(fileUrl, fileName);
    }

}