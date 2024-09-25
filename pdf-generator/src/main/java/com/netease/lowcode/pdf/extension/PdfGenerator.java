package com.netease.lowcode.pdf.extension;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.itextpdf.forms.PdfAcroForm;
import com.itextpdf.forms.fields.PdfFormField;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import com.netease.lowcode.pdf.extension.itextpdf.NodeCreator;
import com.netease.lowcode.pdf.extension.itextpdf.PdfUtils;
import com.netease.lowcode.pdf.extension.structures.BaseResponse;
import com.netease.lowcode.pdf.extension.structures.CreateByTemplateRequest;
import com.netease.lowcode.pdf.extension.structures.CreateRequest;
import com.netease.lowcode.pdf.extension.utils.FileUtils;
import com.netease.lowcode.pdf.extension.utils.FreemarkerUtils;
import com.netease.lowcode.pdf.extension.utils.UploadResponseDTO;
import com.netease.lowcode.core.annotation.NaslLogic;
import freemarker.template.TemplateException;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;

// Press Shift twice to open the Search Everywhere dialog and type `show whitespaces`,
// then press Enter. You can now see whitespace characters in your code.
@Component("libraryPdfGenerator")
public class PdfGenerator {

    private static FileUtils fileUtils;

    @Autowired
    @Qualifier("pdfGeneratorFileUtils")
    public void setFileUtils(FileUtils fileUtils) {
        PdfGenerator.fileUtils = fileUtils;
    }

    @NaslLogic
    public static BaseResponse createPDFByTemplate(CreateByTemplateRequest request) {
        String outPath = "data/" + System.currentTimeMillis() + "/";
        try {
            // 下载模板文件
            File file = FileUtils.downloadFile(request.templateUrl);

            // 创建暂存目录
            File dir = new File(outPath);
            dir.mkdirs();

            com.itextpdf.kernel.pdf.PdfDocument pdfDocument =
                    new com.itextpdf.kernel.pdf.PdfDocument(
                            new com.itextpdf.kernel.pdf.PdfReader(file),
                            new com.itextpdf.kernel.pdf.PdfWriter(outPath+request.exportFileName)
                    );
            PdfAcroForm pdfAcroForm = PdfAcroForm.getAcroForm(pdfDocument,true);

            // 获取模板字段
            Map<String, PdfFormField> formFields = pdfAcroForm.getAllFormFields();
            // 获取填充值
            Map<String,String> dataMap = JSON.parseObject(request.jsonData, Map.class);

            formFields.forEach((key,value)->{
                try {
                    // 处理中文乱码
                    PdfFont font = PdfFontFactory.createFont("STSong-Light","UniGB-UCS2-H");
                    value.setFont(font);
                    value.setValue(dataMap.get(key));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });

            // 设置不可编辑
            pdfAcroForm.flattenFields();
            pdfDocument.close();

            // 上传文件
            UploadResponseDTO uploadResponseDTO = fileUtils.uploadFileV2(new File(outPath + request.exportFileName));

            return BaseResponse.OK(uploadResponseDTO.getFilePath(), uploadResponseDTO.getResult());

        } catch (IOException | InvocationTargetException | NoSuchMethodException | IllegalAccessException e) {
            return BaseResponse.FAIL(Arrays.toString(e.getStackTrace()), e.getMessage());
        }

    }

    /**
     * 传入的是模板文件url
     *
     * @param jsonData
     * @param templateUrl
     * @return
     */
    @NaslLogic
    public static BaseResponse createPDFV2(String jsonData, String templateUrl) {
        try {

            ByteArrayInputStream byteArrayInputStream = FreemarkerUtils.getFreemarkerContentInputStreamV2(jsonData, templateUrl);

            BufferedReader br = new BufferedReader(new InputStreamReader(byteArrayInputStream));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            JSONObject jsonObject = JSONObject.parseObject(sb.toString());
//            JSONObject jsonObject = JSONObject.parseObject(PdfUtils.readJson("pdf-generator/src/main/resources/result.json"));
            ByteArrayOutputStream byteArrayOutputStream = NodeCreator.node(jsonObject);
            ByteArrayInputStream uploadStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());

            UploadResponseDTO uploadResponseDTO = FileUtils.uploadStream(uploadStream, jsonObject.getString("fileName"));

            return BaseResponse.OK(uploadResponseDTO.getFilePath(), uploadResponseDTO.getResult());
        } catch (Exception e) {
            return BaseResponse.FAIL(Arrays.toString(e.getStackTrace()), e.getMessage());
        }
    }

    /**
     * 传入的是模板文件json字符串
     *
     * @param jsonData
     * @param jsonTemplate
     * @return
     */
    @NaslLogic
    public static BaseResponse createPDFV2ByStr(String jsonData, String jsonTemplate) {
        try {
            ByteArrayInputStream byteArrayInputStream = FreemarkerUtils.getFreemarkerContentInputStream(jsonData, jsonTemplate);
            BufferedReader br = new BufferedReader(new InputStreamReader(byteArrayInputStream));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            JSONObject jsonObject = JSONObject.parseObject(sb.toString());
            ByteArrayOutputStream byteArrayOutputStream = NodeCreator.node(jsonObject);
            ByteArrayInputStream uploadStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());

//             TODO:上传文件
            UploadResponseDTO uploadResponseDTO = FileUtils.uploadStream(uploadStream, jsonObject.getString("fileName"));

            return BaseResponse.OK(uploadResponseDTO.getFilePath(), uploadResponseDTO.getResult());

//            FileOutputStream fos = new FileOutputStream(jsonObject.getString("fileName"));
//            byte[] buffer = new byte[1024];
//            int read;
//            while ((read = uploadStream.read(buffer)) != -1) {
//                fos.write(buffer, 0, read);
//            }
//            return BaseResponse.OK("","");
        } catch (Exception e) {
            return BaseResponse.FAIL(Arrays.toString(e.getStackTrace()), e.getMessage());
        }
    }

    @NaslLogic
    public static BaseResponse createPDF(CreateRequest request) {

        try{
            // 按模块填充内容
            if(CollectionUtils.isEmpty(request.data)) {
                return BaseResponse.FAIL("数据为空！");
            }
            if(StringUtils.isBlank(request.fileName)){
                return BaseResponse.FAIL("文件名称不能为空,格式 *.pdf");
            }

            Document document = new Document();
            PdfWriter writer = PdfWriter.getInstance(document,new FileOutputStream(request.fileName));
            document.open();

            // 支持中文字体 TODO:后续考虑通过参数传入字体名称
            BaseFont chinese = BaseFont.createFont("STSong-Light","UniGB-UCS2-H",BaseFont.NOT_EMBEDDED);
            Font chineseFont = new Font(chinese);

            for (Map<String, List<String>> item : request.data) {

                if(MapUtils.isEmpty(item)){
                    continue;
                }

                for (Map.Entry<String, List<String>> entry : item.entrySet()) {

                    if(CollectionUtils.isEmpty(entry.getValue())){
                        continue;
                    }

                    switch (entry.getKey()) {
                        case "pdf-table":
                            fillTable(document,entry.getValue(),chineseFont);
                            break;
                        case "pdf-image":
                            fillImage(document,entry.getValue(),chineseFont);
                            break;
                        case "pdf-paragraph":
                            fillParagraph(document,entry.getValue(),chineseFont);
                            break;
                        case "pdf-title":
                            fillTitle(document,entry.getValue(),chineseFont);
                            break;
                        default:
                            return BaseResponse.FAIL("不支持的类型:" + entry.getKey());
                    }
                }
            }
            document.close();
            writer.close();

            // 上传到oss
            UploadResponseDTO uploadResponseDTO = fileUtils.uploadFileV2(new File(request.fileName));

            return BaseResponse.OK(uploadResponseDTO.getFilePath(), uploadResponseDTO.getResult());
        } catch (DocumentException | IOException | InvocationTargetException | NoSuchMethodException |
                 IllegalAccessException e) {
            return BaseResponse.FAIL(Arrays.toString(e.getStackTrace()),e.getMessage());
        }
    }

    private static void fillParagraph(Document document,List<String> data,Font font) throws DocumentException {
        for (String item : data) {
            Paragraph paragraph = new Paragraph(new Paragraph(item,font));
            document.add(paragraph);
        }
    }

    private static void fillTitle(Document document,List<String> data,Font font) throws DocumentException {
        for (String item : data) {
            Paragraph paragraph = new Paragraph(item,font);
            paragraph.setAlignment(Element.ALIGN_CENTER);
            document.add(paragraph);
        }
    }

    private static void fillImage(Document document,List<String> data,Font font) throws DocumentException, IOException {
        for (String item : data) {
            // 先下载图片
            File file = FileUtils.downloadFile(item);

            Image image = Image.getInstance(file.getAbsolutePath());
            image.scaleAbsolute(100,100);
            document.add(image);

            file.delete();
        }
    }

    private static void fillTable(Document document,List<String> data,Font font) throws DocumentException {

        PdfPTable table = null;
        List<PdfPRow> rows = null;

        // 表头取第一行数据,每行数据用,分隔
        // 用户务必控制每行数据的个数统一，空数据同样需要进行占位
        for (String row : data) {
            if(StringUtils.isBlank(row)){
                continue;
            }
            String[] splits = StringUtils.split(row, ",");
            PdfPCell cells[] = new PdfPCell[splits.length];
            for (int i = 0; i < splits.length; i++) {
                if("-".equals(splits[i])){
                    cells[i] = new PdfPCell(new Paragraph(" ",font));
                }else {
                    cells[i] = new PdfPCell(new Paragraph(splits[i], font));
                }
            }

            PdfPRow pRow = new PdfPRow(cells);

            if(Objects.isNull(table)){
                table = new PdfPTable(splits.length);
                table.setWidthPercentage(100);
                table.setSpacingBefore(10f);
                table.setSpacingAfter(10f);
            }
            if (Objects.isNull(rows)) {
                rows = table.getRows();
            }
            rows.add(pRow);
        }

        if(Objects.nonNull(table)){
            document.add(table);
        }
    }
}