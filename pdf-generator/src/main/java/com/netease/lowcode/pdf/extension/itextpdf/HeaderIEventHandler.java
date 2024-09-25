package com.netease.lowcode.pdf.extension.itextpdf;

import com.alibaba.fastjson2.JSONObject;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.events.Event;
import com.itextpdf.kernel.events.IEventHandler;
import com.itextpdf.kernel.events.PdfDocumentEvent;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.layout.properties.TextAlignment;

import java.io.IOException;
import java.net.MalformedURLException;

public class HeaderIEventHandler implements IEventHandler {

    private JSONObject jsonObject;

    public HeaderIEventHandler(JSONObject jsonObject){
        this.jsonObject = jsonObject;
    }

    @Override
    public void handleEvent(Event event) {

        // 判断是否需要页眉
        if(!jsonObject.containsKey("header")) {
            return;
        }

        JSONObject header = jsonObject.getJSONObject("header");

        if(event instanceof PdfDocumentEvent){
            PdfDocumentEvent documentEvent = (PdfDocumentEvent) event;
            com.itextpdf.kernel.pdf.PdfDocument document = documentEvent.getDocument();

            // 获取页面尺寸
            PageSize pageSize = PdfUtils.getPageSize(jsonObject.getString("pageSize"));
            if (jsonObject.containsKey("rotate") && jsonObject.getBoolean("rotate")) {
                pageSize = pageSize.rotate();
            }

            // 创建一个Canvas对象，用于添加水印
            com.itextpdf.layout.Canvas canvas = new com.itextpdf.layout.Canvas(
                    new PdfCanvas(documentEvent.getPage()),
                    new PageSize(pageSize)
            );

            if (header.containsKey("fontColor")) {
                canvas.setFontColor(PdfUtils.getColor(header.getString("fontColor")));
            } else {
                canvas.setFontColor(ColorConstants.LIGHT_GRAY);
            }
            if (header.containsKey("fontSize")) {
                canvas.setFontSize(header.getInteger("fontSize"));
            } else {
                canvas.setFontSize(20);
            }
            // 为了支持中文
            try {
                // 目前统一从全局配置取，暂不考虑水印单独配置字体
                if (jsonObject.containsKey("font")) {
                    String fontProgram = jsonObject.getJSONObject("font").getString("fontProgram");
                    String encoding = jsonObject.getJSONObject("font").getString("encoding");
                    canvas.setFont(PdfFontFactory.createFont(fontProgram, encoding));
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            float x = 0, y = pageSize.getTop();
            if (header.containsKey("marginLeft")) {
                x = header.getFloat("marginLeft");
            }
            if (header.containsKey("marginTop")) {
                y = pageSize.getTop() - header.getFloat("marginTop");
            }

            TextAlignment textAlignment = TextAlignment.LEFT;
            if (header.containsKey("textAlignment")) {
                textAlignment = TextAlignment.valueOf(header.getString("textAlignment"));
            }

            canvas.showTextAligned(header.getString("text"), x, y, textAlignment);

            // 设置页眉图标
            if(header.containsKey("image")){
                JSONObject image = header.getJSONObject("image");

                com.itextpdf.layout.element.Image imageElement = NodeCreator.image(image);
                canvas.add(imageElement);

            }
            canvas.close();
        }
    }
}
