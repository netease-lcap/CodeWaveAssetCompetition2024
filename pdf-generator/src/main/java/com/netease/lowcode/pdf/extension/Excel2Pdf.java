package com.netease.lowcode.pdf.extension;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.netease.lowcode.core.annotation.NaslLogic;
import com.netease.lowcode.pdf.extension.structures.BaseResponse;
import com.netease.lowcode.pdf.extension.structures.CreateByXlsxRequest;
import com.netease.lowcode.pdf.extension.utils.FileUtils;
import com.netease.lowcode.pdf.extension.utils.JSONObjectUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class Excel2Pdf {


    @NaslLogic
    public static BaseResponse xlsx2pdf(CreateByXlsxRequest request) {

        try {
            if (StringUtils.isBlank(request.getExportFileName()) || !request.getExportFileName().endsWith(".pdf")) {
                return BaseResponse.FAIL("exportFileName必须以 .pdf 结尾");
            }

            // 下载模板文件
            File templateFile = FileUtils.downloadFile(request.getTemplateUrl());

            String fileName = templateFile.getName();
            InputStream inputStream = new FileInputStream(templateFile);

            Workbook wb = null;
            if (fileName.endsWith(".xlsx")) {
                // 解析 *.xlsx
                wb = new XSSFWorkbook(inputStream);
            } else {
                return BaseResponse.FAIL("仅支持 *.xlsx 文件");
            }

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("fileName",request.getExportFileName());
            // 字体设置
            jsonObject.put("font",new HashMap<String,String>(){{
                put("fontProgram","STSong-Light");
                put("encoding","UniGB-UCS2-H");
            }});
            // 纸张大小A4
            jsonObject.put("pageSize",request.getPageSize());
            // 纸张方向
            jsonObject.put("rotate",request.getRotate());
            JSONArray nodes = new JSONArray();
            jsonObject.put("nodes",nodes);

            // 读取第0个sheet
            Sheet sheet0 = wb.getSheetAt(0);
            if (Objects.isNull(sheet0)) {
                return BaseResponse.FAIL("读取sheet0为空");
            }

            JSONObject table = new JSONObject();
            table.put("type","Table");
            table.put("width",100);

            JSONArray cells = new JSONArray();
            table.put("cells", cells);
            nodes.add(table);

            // 记录整个sheet列宽度
            List<Integer> sheetColWidthList = new ArrayList<>();
            // 暂存单元格
            List<List<JSONObject>> tmpCells = new ArrayList<>();

            // 遍历sheet行
            for (int i = 0; i <= sheet0.getLastRowNum(); i++) {
                Row row = sheet0.getRow(i);
                // 暂存该行单元格
                List<JSONObject> curRowTmpCells = new ArrayList<>();
                tmpCells.add(curRowTmpCells);

                if(Objects.isNull(row)){
                    // 保留空行
                    continue;
                }

                List<Integer> currentRowColWidths = new ArrayList<>();
                // 遍历列，注意模板不要超过A4的宽度
                for (int j = 0; j < row.getLastCellNum(); j++) {

                    JSONObject jsonCell = new JSONObject();
                    JSONArray elements = new JSONArray();
                    JSONObject paragraph = new JSONObject();
                    paragraph.put("type","Paragraph");
                    elements.add(paragraph);
                    jsonCell.put("elements", elements);

                    curRowTmpCells.add(jsonCell);

                    // 获取单元格
                    Cell cell = row.getCell(j);

                    if (Objects.isNull(cell)) {
                        currentRowColWidths.add(0);
                        continue;
                    }

                    currentRowColWidths.add(sheet0.getColumnWidth(j));

                    // 单元格字体
                    Font font = wb.getFontAt(cell.getCellStyle().getFontIndexAsInt());
                    // 字体颜色
                    if (font instanceof XSSFFont) {
                        XSSFFont xssfFont = (XSSFFont) font;
                        XSSFColor xssfColor = xssfFont.getXSSFColor();
                        if(Objects.nonNull(xssfColor)) {
                            byte[] rgb = xssfColor.getRGB();
                            paragraph.put("rgb", new HashMap<String, Integer>() {
                                {
                                    put("red", (rgb[0] < 0) ? (rgb[0] + 256) : rgb[0]);
                                    put("green", (rgb[1] < 0) ? (rgb[1] + 256) : rgb[1]);
                                    put("blue", (rgb[2] < 0) ? (rgb[2] + 256) : rgb[2]);
                                }
                            });
                        }
                    }
                    // 字体大小
                    short fontSize = font.getFontHeightInPoints();
                    paragraph.put("fontSize",fontSize);
                    // 字体是否加粗
                    boolean bold = font.getBold();
                    paragraph.put("bold", bold);
                    // 下划线
                    byte underline = font.getUnderline();


                    // 单元格类型
                    CellType cellType = cell.getCellType();
                    if (CellType.STRING.equals(cellType)) {
                        paragraph.put("text", cell.getStringCellValue());
                    }


                    // 判断水平居中
                    HorizontalAlignment alignment = cell.getCellStyle().getAlignment();
                    if (HorizontalAlignment.CENTER.equals(alignment)) {
                        jsonCell.put("textAlignment", "CENTER");
                    }
                    // 判断垂直居中
                    VerticalAlignment verticalAlignment = cell.getCellStyle().getVerticalAlignment();
                    if (VerticalAlignment.CENTER.equals(verticalAlignment)) {

                    }

                    // 表格底部边框
                    BorderStyle borderBottom = cell.getCellStyle().getBorderBottom();
                    if (BorderStyle.THIN.equals(borderBottom)) {
                        JSONObject value = new JSONObject();
                        value.put("width", 1);
                        jsonCell.put("borderBottom", value);
                    }
                    BorderStyle borderTop = cell.getCellStyle().getBorderTop();
                    if (BorderStyle.THIN.equals(borderTop)) {
                        JSONObject value = new JSONObject();
                        value.put("width", 1);
                        jsonCell.put("borderTop", value);
                    }
                    BorderStyle borderLeft = cell.getCellStyle().getBorderLeft();
                    if (BorderStyle.THIN.equals(borderLeft)) {
                        JSONObject value = new JSONObject();
                        value.put("width", 1);
                        jsonCell.put("borderLeft", value);
                    }
                    BorderStyle borderRight = cell.getCellStyle().getBorderRight();
                    if (BorderStyle.THIN.equals(borderRight)) {
                        JSONObject value = new JSONObject();
                        value.put("width", 1);
                        jsonCell.put("borderRight", value);
                    }

                }

                // 设置宽度
                if (currentRowColWidths.size() > sheetColWidthList.size()) {
                    for (int j = 0; j < sheetColWidthList.size(); j++) {
                        if (sheetColWidthList.get(j) == 0) {
                            sheetColWidthList.set(j, currentRowColWidths.get(j));
                        }
                    }
                    int size = sheetColWidthList.size();
                    for (int j = size; j < currentRowColWidths.size(); j++) {
                        sheetColWidthList.add(currentRowColWidths.get(j));
                    }
                } else {
                    for (int j = 0; j < currentRowColWidths.size(); j++) {
                        if (sheetColWidthList.get(j) == 0) {
                            sheetColWidthList.set(j, currentRowColWidths.get(j));
                        }
                    }
                }
            }
            // 单元格末尾对齐填充
            int maxSize = tmpCells.stream().mapToInt(List::size).max().orElse(0);
            for (List<JSONObject> tmpRow : tmpCells) {
                if (tmpRow.size() < maxSize) {
                    int diff = maxSize - tmpRow.size();
                    for (int i = 0; i < diff; i++) {
                        JSONObject jsonCell = new JSONObject();
                        JSONArray elements = new JSONArray();
                        JSONObject paragraph = new JSONObject();
                        paragraph.put("type", "Paragraph");
                        elements.add(paragraph);
                        jsonCell.put("elements", elements);
                        tmpRow.add(jsonCell);
                    }
                }
            }

            // 标记合并区域
            List<CellRangeAddress> mergedRegions = sheet0.getMergedRegions();
            if(CollectionUtils.isNotEmpty(mergedRegions)){
                for (CellRangeAddress mergedRegion : mergedRegions) {
                    // 得到的单元格坐标是按照合并前的坐标来计算，因此使用cloneTmpCells
                    int firstRow = mergedRegion.getFirstRow();
                    int lastRow = mergedRegion.getLastRow();
                    int firstColumn = mergedRegion.getFirstColumn();
                    int lastColumn = mergedRegion.getLastColumn();

                    // 记录合并单元格
                    JSONObject mergeCell = tmpCells.get(firstRow).get(firstColumn);
                    mergeCell.put("rowspan", lastRow - firstRow + 1);
                    mergeCell.put("colspan", lastColumn - firstColumn + 1);

                    // 处理合并单元格边框,仅需处理右侧、底部
                    if (tmpCells.get(firstRow).get(lastColumn).containsKey("borderRight")) {
                        mergeCell.put("borderRight", tmpCells.get(firstRow).get(lastColumn).getJSONObject("borderRight"));
                    }
                    if (tmpCells.get(lastRow).get(firstColumn).containsKey("borderLeft")) {
                        mergeCell.put("borderLeft", tmpCells.get(lastRow).get(firstColumn).getJSONObject("borderLeft"));
                    }
                    if (tmpCells.get(lastRow).get(lastColumn).containsKey("borderBottom")) {
                        mergeCell.put("borderBottom", tmpCells.get(lastRow).get(lastColumn).getJSONObject("borderBottom"));
                    }

                    // 处理合并区域
                    for (int i = firstRow; i <= lastRow; i++) {
                        List<JSONObject> list = tmpCells.get(i);
                        List<JSONObject> newList = new ArrayList<>();

                        for (int j = 0; j < list.size(); j++) {
                            // 当前行 合并区域处理
                            if (j >= firstColumn && j <= lastColumn) {
                                // 合并区域 首行 第一个单元格 填充mergeCell
                                if (i == firstRow && j == firstColumn) {
                                    newList.add(mergeCell);
                                    continue;
                                }
                                // 合并区域 其他单元格 用占位标记，后续统一删除
                                JSONObject e = new JSONObject();
                                e.put("mergeTagWillBeDeleted", "");
                                newList.add(e);
                                continue;
                            }

                            // 非合并区域 用原始单元格填充
                            newList.add(list.get(j));
                        }
                        // 替换新行
                        tmpCells.set(i, newList);
                    }
                }
            }
            // 移除被合并单元格
            tmpCells.removeIf(curRow -> {
                curRow.removeIf(next -> next.containsKey("mergeTagWillBeDeleted"));
                return curRow.isEmpty();
            });

            // 从全局设置cell宽度
            int totalColWidth = sheetColWidthList.stream().mapToInt(Integer::intValue).sum();
            for (int i = 0; i < tmpCells.size(); i++) {
                List<JSONObject> list = tmpCells.get(i);
                // 处理该行
                for (int j = 0; j < list.size(); j++) {
                    JSONObject currentCell = list.get(j);
                    if (currentCell.containsKey("rowspan") && currentCell.containsKey("colspan")) {
                        Integer colspan = currentCell.getInteger("colspan");
                        float value = 0.0f;
                        for (int k = j; k < j + colspan; k++) {
                            value += ((sheetColWidthList.get(k) / (float) totalColWidth) * 100);
                        }
                        currentCell.put("width", Math.round(value));
                    } else {
                        float value = (sheetColWidthList.get(j) / (float) totalColWidth) * 100;
                        currentCell.put("width", Math.round(value));
                    }
                }
            }

            // 处理freemarker list
            handleFreemarkerList(tmpCells,request.getJsonData());

            for (int i = 0; i < tmpCells.size(); i++) {
                List<JSONObject> list = tmpCells.get(i);
                for (JSONObject object : list) {
                    // 去除单元格默认边框
                    object.put("noBorder", true);
                }
                cells.addAll(list);
            }

            // 设置表格列数
            table.put("columnSize",maxSize);
            // 由于将整个sheet解析为一个完整的table，因此不再设置chunkSize
            // table.put("chunkSize",2);

            BaseResponse response = PdfGenerator.createPDFV2ByStr(request.getJsonData(), jsonObject.toJSONString());
            if (response.getSuccess()) {
                return BaseResponse.OK(response.filePath, response.result);
            }

            return BaseResponse.FAIL(response.trace, "pdf创建失败:" + response.msg);
        } catch (IOException e) {
            return BaseResponse.FAIL(Arrays.toString(e.getStackTrace()), e.getMessage());
        }
    }

    /**
     * 处理freemarker list标签
     *
     * @param tmpCells
     */
    public static void handleFreemarkerList(List<List<JSONObject>> tmpCells, String jsonData) {
        if (CollectionUtils.isEmpty(tmpCells)) {
            return;
        }

        // 解析参数
        JSONObject requestJsonData = null;

        int i = 0;
        while (i < tmpCells.size()) {
            // 当前行
            List<JSONObject> originRow = tmpCells.get(i);
            if (CollectionUtils.isEmpty(originRow)) {
                i++;
                continue;
            }

            // 判断当前行是否包含freemarker list标签 ${list.arr}
            boolean hasFreemarkerListTag = false;
            for (int j = 0; j < originRow.size(); j++) {
                JSONObject originCell = originRow.get(j);
                if (isFreemarkerListTag(originCell)) {
                    hasFreemarkerListTag = true;
                    break;
                }
            }

            // 包含list标签，开始处理
            if (hasFreemarkerListTag) {
                if (Objects.isNull(requestJsonData)) {
                    requestJsonData = JSONObject.parseObject(jsonData);
                }
                List<List<JSONObject>> newRows = JSONObjectUtil.fillListData(originRow, requestJsonData);
                // 将originRow 替换为 newRows
                for (int j = 0; j < newRows.size(); j++) {
                    tmpCells.add(i + j, newRows.get(j));
                }
                // 移除originRow
                tmpCells.remove(i + newRows.size());
                i += newRows.size();
            } else {
                i++;
            }
        }
    }

    /**
     * 获取cell内paragraph的text字段值
     *
     * @param cell
     * @return
     */
    public static String getCellText(JSONObject cell) {
        if (Objects.isNull(cell)) {
            return null;
        }
        if (!cell.containsKey("elements")) {
            return null;
        }
        JSONArray elements = cell.getJSONArray("elements");
        if (Objects.isNull(elements) || elements.isEmpty()) {
            return null;
        }
        JSONObject paragraph = elements.getJSONObject(0);
        if (!paragraph.containsKey("text")) {
            return null;
        }
        return paragraph.getString("text");
    }

    /**
     * 判断一个cell是否为freemarker 的list 标签
     * 匹配 ${xx.xxx} xx为list变量名，xxx为item属性名
     *
     * @param cell
     * @return
     */
    public static boolean isFreemarkerListTag(JSONObject cell) {
        if (Objects.isNull(cell)) {
            return false;
        }
        if (!cell.containsKey("elements")) {
            return false;
        }
        JSONArray elements = cell.getJSONArray("elements");
        if (Objects.isNull(elements) || elements.isEmpty()) {
            return false;
        }
        JSONObject paragraph = elements.getJSONObject(0);
        if (!paragraph.containsKey("text")) {
            return false;
        }
        String text = paragraph.getString("text");
        if (StringUtils.isNotBlank(text) && text.startsWith("${") && text.endsWith("}") && text.contains(".")) {
            return true;
        }

        return false;
    }
}
