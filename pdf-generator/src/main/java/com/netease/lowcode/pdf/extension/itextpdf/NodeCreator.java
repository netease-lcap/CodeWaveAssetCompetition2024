package com.netease.lowcode.pdf.extension.itextpdf;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.itextpdf.forms.fields.properties.CheckBoxType;
import com.itextpdf.forms.form.element.CheckBox;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.CalRgb;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.events.PdfDocumentEvent;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.draw.SolidLine;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.netease.lowcode.pdf.extension.structures.NodeTypeEnum;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.commons.lang3.StringUtils;
import sun.misc.BASE64Decoder;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;
import java.util.Objects;

public class NodeCreator {

    public static ByteArrayOutputStream node(JSONObject jsonObject) throws IOException {

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        String fileName = jsonObject.getString("fileName");
        if (StringUtils.isBlank(fileName) || !fileName.endsWith(".pdf")) {
            throw new RuntimeException("fileName必须以 *.pdf 结尾");
        }
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(byteArrayOutputStream));

        // 设置纸张大小和方向: 默认纵向,rotate为横向
        PageSize pageSize = PdfUtils.getPageSize(jsonObject.getString("pageSize"));
        if (jsonObject.containsKey("rotate") && jsonObject.getBoolean("rotate")) {
            pageSize = pageSize.rotate();
        }
        pdfDocument.setDefaultPageSize(pageSize);
        // 添加水印,页眉页脚
        pdfDocument.addEventHandler(PdfDocumentEvent.END_PAGE, new WaterMaskIEventHandler(jsonObject));
        pdfDocument.addEventHandler(PdfDocumentEvent.END_PAGE, new HeaderIEventHandler(jsonObject));
        pdfDocument.addEventHandler(PdfDocumentEvent.END_PAGE,new FooterIEventHandler(jsonObject));
        pdfDocument.addEventHandler(PdfDocumentEvent.END_PAGE,new PageNumberIEventHandler(jsonObject));

        Document document = new Document(pdfDocument);

        JSONObject fontJSONObject = jsonObject.getJSONObject("font");
        document.setFont(PdfFontFactory.createFont(fontJSONObject.getString("fontProgram"),fontJSONObject.getString("encoding")));
        if (jsonObject.containsKey("fontSize")) {
            document.setFontSize(jsonObject.getFloat("fontSize"));
        } else {
            document.setFontSize(7);
        }

        // 设置页边距
        if(jsonObject.containsKey("marginLeft")){
            document.setLeftMargin(jsonObject.getFloat("marginLeft"));
        }
        if(jsonObject.containsKey("marginRight")){
            document.setRightMargin(jsonObject.getFloat("marginRight"));
        }
        if(jsonObject.containsKey("marginTop")){
            document.setTopMargin(jsonObject.getFloat("marginTop"));
        }
        if(jsonObject.containsKey("marginBottom")){
            document.setBottomMargin(jsonObject.getFloat("marginBottom"));
        }

        JSONArray nodes = jsonObject.getJSONArray("nodes");

        if (Objects.isNull(nodes)) {
            document.close();
            return byteArrayOutputStream;
        }

        nodes.toJavaList(JSONObject.class).forEach(nodeObj -> {
            if ("Image".equalsIgnoreCase(nodeObj.getString("type"))) {
                // 文档插入图片
                document.add(image(nodeObj));
            } else if ("AreaBreak".equalsIgnoreCase(nodeObj.getString("type"))) {
                // 文档分页
                document.add(areaBreak());
            } else {
                document.add(NodeTypeEnum.valueOf(nodeObj.getString("type")).exec(nodeObj));
            }
        });

        document.close();
        return byteArrayOutputStream;
    }

    public static Paragraph paragraph(JSONObject jsonObject) {
        if(Objects.isNull(jsonObject)){
            return null;
        }
        if(!jsonObject.containsKey("type")){
            return null;
        }
        Paragraph paragraph = new Paragraph();
        if (jsonObject.containsKey("text")) {
            paragraph.add(jsonObject.getString("text"));
        } else {
            paragraph.add("");
        }
        if(jsonObject.containsKey("textAlignment")){
            paragraph.setTextAlignment(TextAlignment.valueOf(jsonObject.getString("textAlignment")));
        }
        if(jsonObject.containsKey("fontSize")){
            paragraph.setFontSize(jsonObject.getInteger("fontSize"));
        }
        if (jsonObject.containsKey("bold") && jsonObject.getBoolean("bold")) {
            paragraph.setBold();
        }
        if(jsonObject.containsKey("underline")){
            paragraph.setUnderline();
        }
        if(jsonObject.containsKey("fontColor")){
            paragraph.setFontColor(PdfUtils.getColor(jsonObject.getString("fontColor")));
        }
        // 如果有rgb,将覆盖上面的color
        if (jsonObject.containsKey("rgb")) {
            JSONObject rgb = jsonObject.getJSONObject("rgb");
            paragraph.setFontColor(new DeviceRgb(rgb.getInteger("red"), rgb.getInteger("green"), rgb.getInteger("blue")));
        }

        if(jsonObject.containsKey("marginLeft")){
            paragraph.setMarginLeft(jsonObject.getInteger("marginLeft"));
        }
        if(jsonObject.containsKey("marginRight")) {
            paragraph.setMarginRight(jsonObject.getInteger("marginRight"));
        }

        if (jsonObject.containsKey("elements")) {
            JSONArray elements = jsonObject.getJSONArray("elements");
            elements.toJavaList(JSONObject.class).forEach(obj -> {
                if("Image".equalsIgnoreCase(obj.getString("type"))){
                    paragraph.add(image(obj));
                }else{
                    paragraph.add(NodeTypeEnum.valueOf(obj.getString("type")).exec(obj));
                }
            });
        }

        return paragraph;
    }

    public static CheckBox checkBox(JSONObject jsonObject) {
        if(Objects.isNull(jsonObject)){
            return null;
        }
        if(!jsonObject.containsKey("type")){
            return null;
        }

        CheckBox checkBox = new CheckBox(String.valueOf(System.currentTimeMillis()));
        if (jsonObject.containsKey("checked")) {
            checkBox.setChecked(jsonObject.getBoolean("checked"));
        }
        if (jsonObject.containsKey("checkBoxType")) {
            checkBox.setCheckBoxType(CheckBoxType.valueOf(jsonObject.getString("checkBoxType")));
        }
        if (jsonObject.containsKey("border")) {
            checkBox.setBorder(new SolidBorder(jsonObject.getJSONObject("border").getInteger("width")));
        }
        if (jsonObject.containsKey("size")) {
            checkBox.setSize(jsonObject.getInteger("size"));
        }
        return checkBox;
    }

    public static Table table(JSONObject jsonObject){
        if(Objects.isNull(jsonObject)){
            return null;
        }
        Integer width = jsonObject.getInteger("width");
        // 列的数量
        Integer columnSize = jsonObject.getInteger("columnSize");
        JSONArray cellArray = jsonObject.getJSONArray("cells");
        List<JSONObject> cellList = cellArray.toJavaList(JSONObject.class);
        // 获取分块数量,表格将在水平方向上扩展
        if (jsonObject.containsKey("chunkSize")) {

            // 进行分块时，cell的填充方向，默认水平顺序填充
            if(jsonObject.containsKey("chunkVertical")){
                // 进行竖直顺序填充
                // TODO: 暂不支持
            }

            Integer chunkSize = jsonObject.getInteger("chunkSize");
            columnSize = columnSize * chunkSize;

            // 末尾对齐
            int cellListSize = cellList.size();
            if (cellListSize % columnSize != 0) {
                for (int i = cellListSize % columnSize; i < columnSize; i++) {
                    // 复制上方表格样式
                    JSONObject clone = SerializationUtils.clone(cellList.get(i % jsonObject.getInteger("columnSize")));
                    JSONArray cellElements = clone.getJSONArray("elements");
                    List<JSONObject> elementList = cellElements.toJavaList(JSONObject.class);
                    for (JSONObject elementObj : elementList) {
                        String elementType = elementObj.getString("type");
                        if (NodeTypeEnum.Paragraph.equals(NodeTypeEnum.valueOf(elementType))) {
                            elementObj.put("text", "");
                        }
                    }
                    cellList.add(clone);
                }
            }
        }

        Table table = new Table(columnSize);
        table.setWidth(UnitValue.createPercentValue(width));
        cellList.forEach(obj -> table.addCell(NodeCreator.cell(obj)));
        return table;
    }

    public static Cell cell(JSONObject jsonObject){
        if(Objects.isNull(jsonObject)){
            return null;
        }

        if(!jsonObject.containsKey("width")){
            throw new RuntimeException("cell缺少width");
        }

        Cell cell;
        if (jsonObject.containsKey("rowspan") && jsonObject.containsKey("colspan")) {
            cell = new Cell(jsonObject.getInteger("rowspan"), jsonObject.getInteger("colspan"));
        } else {
            cell = new Cell();
        }
        cell.setWidth(UnitValue.createPercentValue(jsonObject.getInteger("width")));

        if (jsonObject.containsKey("textAlignment")) {
            cell.setTextAlignment(TextAlignment.valueOf(jsonObject.getString("textAlignment")));
        }

        if (!jsonObject.containsKey("elements")) {
            return cell;
        }

        // 去除默认边框
        if (jsonObject.containsKey("noBorder") && jsonObject.getBoolean("noBorder")) {
            cell.setBorder(Border.NO_BORDER);
        }

        // 设置单元格边框
        if (jsonObject.containsKey("borderBottom")) {
            cell.setBorderBottom(new SolidBorder(jsonObject.getJSONObject("borderBottom").getInteger("width")));
        }
        if (jsonObject.containsKey("borderTop")) {
            cell.setBorderTop(new SolidBorder(jsonObject.getJSONObject("borderTop").getInteger("width")));
        }
        if (jsonObject.containsKey("borderLeft")) {
            cell.setBorderLeft(new SolidBorder(jsonObject.getJSONObject("borderLeft").getInteger("width")));
        }
        if (jsonObject.containsKey("borderRight")) {
            cell.setBorderRight(new SolidBorder(jsonObject.getJSONObject("borderRight").getInteger("width")));
        }

        JSONArray elements = jsonObject.getJSONArray("elements");
        elements.toJavaList(JSONObject.class).forEach(obj -> {
            if("Image".equalsIgnoreCase(obj.getString("type"))){
                cell.add(image(obj));
            }else{
                cell.add(NodeTypeEnum.valueOf(obj.getString("type")).exec(obj));
            }
        });

        return cell;
    }

    public static LineSeparator lineSeparator(JSONObject jsonObject){
        SolidLine lineDrawer;
        if (jsonObject.containsKey("lineWidth")) {
            lineDrawer = new SolidLine(jsonObject.getFloat("lineWidth"));
        } else {
            lineDrawer = new SolidLine();
        }
        LineSeparator lineSeparator = new LineSeparator(lineDrawer);
        if(jsonObject.containsKey("width")){
            lineSeparator.setWidth(UnitValue.createPercentValue(jsonObject.getInteger("width")));
        }
        if(jsonObject.containsKey("marginLeft")){
            lineSeparator.setMarginLeft(jsonObject.getInteger("marginLeft"));
        }

        return lineSeparator;
    }

    public static Image image(JSONObject jsonObject) {
        try {
            BASE64Decoder decoder = new BASE64Decoder();
            if(!jsonObject.containsKey("base64")){
                throw new RuntimeException("图片缺少base64编码");
            }
            String base64 = jsonObject.getString("base64");
            if (base64.contains("base64,")) {
                base64 = base64.substring(base64.indexOf("base64,") + 7);
            }
            byte[] bytes = decoder.decodeBuffer(base64);
            Image image = new Image(ImageDataFactory.create(bytes));
            float fitWidth = 100, fitHeight = 100;
            if (jsonObject.containsKey("fitWidth")) {
                fitWidth = jsonObject.getFloat("fitWidth");
            }
            if (jsonObject.containsKey("fitHeight")) {
                fitHeight = jsonObject.getFloat("fitHeight");
            }
            image.scaleToFit(fitWidth, fitHeight);
            if (jsonObject.containsKey("marginLeft")) {
                image.setMarginLeft(jsonObject.getInteger("marginLeft"));
            }
            if (jsonObject.containsKey("marginTop")) {
                image.setMarginTop(jsonObject.getInteger("marginTop"));
            }
            if (jsonObject.containsKey("opacity")) {
                // 透明度 0完全透明 1完全不透明
                image.setOpacity(jsonObject.getFloat("opacity"));
            }
            return image;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static AreaBreak areaBreak() {
        return new AreaBreak();
    }
}
