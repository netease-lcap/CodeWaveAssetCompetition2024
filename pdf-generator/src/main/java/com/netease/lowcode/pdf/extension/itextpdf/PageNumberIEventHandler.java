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
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;

public class PageNumberIEventHandler implements IEventHandler {

    private JSONObject jsonObject;

    public PageNumberIEventHandler(JSONObject jsonObject){
        this.jsonObject = jsonObject;
    }

    @Override
    public void handleEvent(Event event) {
        // 判断是否需要页码
        if(!jsonObject.containsKey("pageNumber")){
            return;
        }

        JSONObject pageNumber = jsonObject.getJSONObject("pageNumber");

        if(event instanceof PdfDocumentEvent){
            PdfDocumentEvent documentEvent = (PdfDocumentEvent) event;
            com.itextpdf.kernel.pdf.PdfDocument document = documentEvent.getDocument();
            // 获取当前处理的页码
            int pageNo = document.getPageNumber(documentEvent.getPage());
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

            if (pageNumber.containsKey("fontColor")) {
                canvas.setFontColor(PdfUtils.getColor(pageNumber.getString("fontColor")));
            } else {
                canvas.setFontColor(ColorConstants.LIGHT_GRAY);
            }
            if (pageNumber.containsKey("fontSize")) {
                canvas.setFontSize(pageNumber.getInteger("fontSize"));
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

            float x = pageSize.getWidth(), y = pageSize.getBottom();
            if (pageNumber.containsKey("marginRight")) {
                x = pageSize.getWidth() - pageNumber.getFloat("marginRight");
            }
            if (pageNumber.containsKey("marginBottom")) {
                y = pageNumber.getFloat("marginBottom");
            }

            TextAlignment textAlignment = TextAlignment.RIGHT;
            if (pageNumber.containsKey("textAlignment")) {
                textAlignment = TextAlignment.valueOf(pageNumber.getString("textAlignment"));
            }

            String text = pageNumber.getString("text");
            String format = text;
            if(StringUtils.isNotBlank(text)){
                String[] split = text.split("%s");
                // 只显示页码
                if (split.length - 1 == 1) {
                    format = String.format(text, pageNo);
                }
                // 显示页码+总页数
                else if (split.length - 1 == 2) {
                    format = String.format(text, pageNo, numberOfPages);
                }
                else {
                    throw new RuntimeException("页码占位符%s数量只能为1或2个");
                }
            }

            canvas.showTextAligned(format, x, y, textAlignment);
            canvas.close();
        }
    }
}
