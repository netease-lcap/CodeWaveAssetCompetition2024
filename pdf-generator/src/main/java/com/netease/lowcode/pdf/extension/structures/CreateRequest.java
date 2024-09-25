package com.netease.lowcode.pdf.extension.structures;

import com.netease.lowcode.core.annotation.NaslStructure;

import java.util.List;
import java.util.Map;

@NaslStructure
public class CreateRequest {
    public String fileName;
    /**
     * key表示数据类型: 文本、图片、表格
     * value表示具体内容,如果是表格类型，value是包括表头在内的数据集合，
     */
    public List<Map<String, List<String>>> data;
}
