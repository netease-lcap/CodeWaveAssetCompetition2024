package com.netease.lowcode.pdf.extension.itextpdf;

import com.alibaba.fastjson2.JSONObject;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.events.Event;
import com.itextpdf.kernel.events.IEventHandler;
import com.itextpdf.kernel.events.PdfDocumentEvent;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.layout.properties.TextAlignment;

import java.io.IOException;

public class FooterIEventHandler implements IEventHandler {

    private JSONObject jsonObject;

    public FooterIEventHandler(JSONObject jsonObject){
        this.jsonObject = jsonObject;
    }

    @Override
    public void handleEvent(Event event) {
        // 判断是否需要页脚
        if(!jsonObject.containsKey("footer")) {
            return;
        }

        JSONObject footer = jsonObject.getJSONObject("footer");

        if(event instanceof PdfDocumentEvent){
            PdfDocumentEvent documentEvent = (PdfDocumentEvent) event;
            com.itextpdf.kernel.pdf.PdfDocument document = documentEvent.getDocument();
            // 获取当前处理的页码
            int pageNumber = document.getPageNumber(documentEvent.getPage());
            // 这里已经可以获取所有页数,可在页脚写入
            int numberOfPages = document.getNumberOfPages();

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

            if (footer.containsKey("fontColor")) {
                canvas.setFontColor(PdfUtils.getColor(footer.getString("fontColor")));
            } else {
                canvas.setFontColor(ColorConstants.LIGHT_GRAY);
            }
            if (footer.containsKey("fontSize")) {
                canvas.setFontSize(footer.getInteger("fontSize"));
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

            float x = 0, y = 0;
            if (footer.containsKey("marginLeft")) {
                x = footer.getFloat("marginLeft");
            }
            if (footer.containsKey("marginBottom")) {
                y = footer.getFloat("marginBottom");
            }

            TextAlignment textAlignment = TextAlignment.LEFT;
            if (footer.containsKey("textAlignment")) {
                textAlignment = TextAlignment.valueOf(footer.getString("textAlignment"));
            }

            canvas.showTextAligned(footer.getString("text"), x, y, textAlignment);
            canvas.close();
        }
    }
}
