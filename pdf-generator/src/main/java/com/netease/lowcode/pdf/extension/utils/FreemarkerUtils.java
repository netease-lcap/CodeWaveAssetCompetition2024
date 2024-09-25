package com.netease.lowcode.pdf.extension.utils;

import com.alibaba.fastjson2.JSON;
import freemarker.cache.StringTemplateLoader;
import freemarker.cache.URLTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class FreemarkerUtils {

    private static final String TEMPLATE_NAME = "default";

    public static ByteArrayInputStream getFreemarkerContentInputStreamV2(String jsonData, String url) throws IOException, TemplateException {
        Configuration cfg = new Configuration(Configuration.VERSION_2_3_28);
        cfg.setTemplateLoader(new URLTemplateLoader() {
            @Override
            protected URL getURL(String name) {
                try {
                    return new URL(url);
                } catch (MalformedURLException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        // 获取模板 这里的test无任何含义，任意字符串
        Template template = cfg.getTemplate("test");
        StringWriter swriter = new StringWriter();
        Object jsonNode = JSON.parseObject(jsonData, Object.class);
        template.process(jsonNode, swriter);

        return new ByteArrayInputStream(swriter.toString().getBytes(StandardCharsets.UTF_8));
    }

    public static ByteArrayInputStream getFreemarkerContentInputStream(String jsonData, String jsonTemplate) throws IOException, TemplateException {
        Configuration cfg = new Configuration(Configuration.VERSION_2_3_28);
        StringTemplateLoader templateLoader = new StringTemplateLoader();
        templateLoader.putTemplate(TEMPLATE_NAME, jsonTemplate);
        cfg.setTemplateLoader(templateLoader);

        Template template = cfg.getTemplate(TEMPLATE_NAME);
        StringWriter swriter = new StringWriter();
        Object jsonNode = JSON.parseObject(jsonData, Object.class);
        template.process(jsonNode, swriter);

        return new ByteArrayInputStream(swriter.toString().getBytes(StandardCharsets.UTF_8));
    }

}
