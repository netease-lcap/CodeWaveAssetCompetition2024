package com.fdddf.zipencrypt;

import com.netease.lowcode.core.annotation.NaslStructure;

import java.util.ArrayList;
import java.util.List;

@NaslStructure
public class FileUploadResponse {
    public Integer code;

    public Boolean success;

    public String msg;

    public String result;

    public String filePath;

    /**
     * 分割文件时，路径列表
     */
    public List<String> filePaths = new ArrayList<>();

    /**
     * 分割文件时，文件URL列表
     */
    public List<String> results = new ArrayList<>();

    @Override
    public String toString() {
        return "FileUploadResponse{" +
                "code=" + code +
                ", success=" + success +
                ", msg='" + msg + '\'' +
                ", result='" + result + '\'' +
                ", filePath='" + filePath + '\'' +
                '}';
    }
}

