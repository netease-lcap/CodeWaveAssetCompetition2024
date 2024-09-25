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
import com.itextpdf.layout.properties.VerticalAlignment;

import java.io.IOException;

public class WaterMaskIEventHandler implements IEventHandler {

    private JSONObject jsonObject;

    public WaterMaskIEventHandler(JSONObject jsonObject){
        this.jsonObject = jsonObject;
    }

    @Override
    public void handleEvent(Event event) {

        // 判断是否需要水印
        if(!jsonObject.containsKey("waterMask")) {
            return;
        }
        JSONObject waterMask = jsonObject.getJSONObject("waterMask");

        if(event instanceof PdfDocumentEvent){
            PdfDocumentEvent documentEvent = (PdfDocumentEvent) event;
            com.itextpdf.kernel.pdf.PdfDocument document = documentEvent.getDocument();
            // 获取当前处理的页码
            int pageNumber = document.getPageNumber(documentEvent.getPage());

            // 获取页面尺寸
            PageSize pageSize = PdfUtils.getPageSize(jsonObject.getString("pageSize"));
            if (jsonObject.containsKey("rotate") && jsonObject.getBoolean("rotate")) {
                pageSize = pageSize.rotate();
            }
            // 创建一个Canvas对象，用于添加水印
            com.itextpdf.layout.Canvas canvas = new com.itextpdf.layout.Canvas(
                    new PdfCanvas(documentEvent.getPage()),
                    new PageSize(pageSize)// 需要判断方向
            );

            if (waterMask.containsKey("fontColor")) {
                canvas.setFontColor(PdfUtils.getColor(waterMask.getString("fontColor")));
            } else {
                canvas.setFontColor(ColorConstants.LIGHT_GRAY);
            }
            if (waterMask.containsKey("fontSize")) {
                canvas.setFontSize(waterMask.getInteger("fontSize"));
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

            // 获取水印起始坐标
            Float x = 0f;
            if (waterMask.containsKey("x")) {
                x = waterMask.getFloat("x");
            }
            Float y = 0f;
            if (waterMask.containsKey("y")) {
                y = waterMask.getFloat("y");
            }
            // 获取水印旋转角度 （0~360°,逆时针方向）
            Double angle = 30.0;
            if (waterMask.containsKey("angle")) {
                angle = waterMask.getDouble("angle");
            }

            float x_space = pageSize.getWidth(), y_space = pageSize.getHeight();
            if (waterMask.containsKey("xAxisElementSpacing")) {
                x_space = waterMask.getFloat("xAxisElementSpacing");
            }
            if (waterMask.containsKey("yAxisElementSpacing")) {
                y_space = waterMask.getFloat("yAxisElementSpacing");
            }

            // 重复多次，调整坐标即可实现重复水印填充满页
            // x轴方向重复
            for (float xi = x; xi <= pageSize.getWidth() + x_space; xi += x_space) {
                // y轴方向重复
                for (float yi = y; yi <= pageSize.getHeight() + y_space; yi += y_space) {
                    // x 水印的横坐标
                    // y 水印的纵坐标
                    // radAngle 水印倾斜角度,直接输入0~360°即可，会自动转换为弧度
                    canvas.showTextAligned(new com.itextpdf.layout.element.Paragraph(waterMask.getString("text")),
                            xi, yi, pageNumber, TextAlignment.CENTER,
                            VerticalAlignment.TOP, (float) Math.toRadians(angle));
                }
            }
            canvas.close();
        }
    }
}
