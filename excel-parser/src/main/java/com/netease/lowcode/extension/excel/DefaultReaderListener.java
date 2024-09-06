package com.netease.lowcode.extension.excel;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.excel.metadata.CellExtra;
import com.netease.lowcode.extension.excel.dto.ExcelParseError;
import com.netease.lowcode.extension.excel.dto.ExcelParseRect;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

/**
 * 根据泛型自动反射为指定类型列表
 * @param <T>
 */
@Slf4j
public class DefaultReaderListener<T> extends AnalysisEventListener<Map<Integer, String>> {
    private Class<T> tClass;
    private ExcelParseRect rect;
    private Map<Integer, String> headMap;
    private int headRowIndex = 0;

    private List<T> data = new ArrayList<>();
    // 空行数据，用来过滤空行
    private List<T> emptyRowData = new ArrayList<>();
    private List<Map<String, String>> unParsedData = new ArrayList<>();
    private List<ExcelParseError> parseErrors = new ArrayList<>();
    private List<CellExtra> mergeCells = new ArrayList<>();
    // 当前数据行和实体字段的对应关系
    private Map<String, String> columnFieldMap;

    public DefaultReaderListener(Class<T> tClass) {
        this.tClass = tClass;
        if (!ReflectionUtils.isCollectionType(tClass)) {
            ReflectionUtils.register(tClass);
        }
    }

    public DefaultReaderListener(Class<T> tClass, ExcelParseRect rect, Map<String, String> columnFieldMap) {
        this.tClass = tClass;
        this.rect = rect;
        this.columnFieldMap = columnFieldMap;
        if (!ReflectionUtils.isCollectionType(tClass)) {
            ReflectionUtils.register(tClass);
        }
    }

    public List<T> getData() {
        return data;
    }

    public List<ExcelParseError> getParseErrors() {
        return parseErrors;
    }

    public List<Map<String, String>> getUnParsedData() {
        return unParsedData;
    }

    @Override
    public void invoke(Map<Integer, String> integerStringMap, AnalysisContext analysisContext) {
        int rowIndex = analysisContext.readRowHolder().getRowIndex();
        int[] startCellIndex = ExcelCommonUtils.cellLocateToIndex(null == rect ? null : rect.getStartCell());
        int[] endCellIndex = ExcelCommonUtils.cellLocateToIndex(null == rect ? null : rect.getEndCell());
        if (!ExcelCommonUtils.isInRect(rowIndex, startCellIndex[0], endCellIndex[0])) {
            log.info("不在指定的读取范围内，当前{}行，起始行{},结束行{}", rowIndex, startCellIndex[0], endCellIndex[0]);
            return;
        }

        if (null != headMap && !headMap.isEmpty()) {
            T t = ReflectionUtils.newInstance(tClass);
            Map<String, String> unParsed = null;
            boolean isAllColumnNull = true;

            for (Map.Entry<Integer, String> entry : integerStringMap.entrySet()) {
                try {
                    int columnIndex = entry.getKey();
                    isAllColumnNull = isAllColumnNull && null == entry.getValue();

                    if (!ExcelCommonUtils.isInRect(columnIndex, startCellIndex[1], endCellIndex[1])) {
                        log.info("不在指定的读取范围内，当前{}列，起始列{},结束列{}", rowIndex, startCellIndex[1], endCellIndex[1]);
                        continue;
                    }

                    // map不为null，说明为复合表头
                    String column = getColumnFiled(columnIndex);

                    if (null == column) {
                        // 没有表头的列不处理
                        log.info("当前{}列没有表头将被忽略", ExcelCommonUtils.indexToCellColumn(entry.getKey()));
                        continue;
                    }

                    boolean isSetValueSuccess = ReflectionUtils.setValue(t, column, entry.getValue());
                    if (!isSetValueSuccess) {
                        unParsed = null == unParsed ? new HashMap<>(integerStringMap.size()) : unParsed;
                        unParsed.put(column, entry.getValue());
                    }
                } catch (Exception e) {
                    ExcelParseError parseError = new ExcelParseError();
                    parseError.setSheetName(analysisContext.readSheetHolder().getSheetName());
                    parseError.setCellName(ExcelCommonUtils.indexToCellLocate(rowIndex, entry.getKey()));
                    parseError.setCellValue(entry.getValue());
                    parseError.setErrorMsg(String.format("解析%s单元格%s时发生错误:%s",
                            parseError.getSheetName(), parseError.getCellName(), e.getMessage()));
                    parseErrors.add(parseError);
                    log.warn(String.format("解析%s单元格%s时发生错误",
                            parseError.getSheetName(), parseError.getCellName()), e);
                }
            }

            if (isAllColumnNull && null != t) {
                emptyRowData.add(t);
            } else if (!isAllColumnNull && !CollectionUtils.isEmpty(emptyRowData) && null != t) {
                data.addAll(emptyRowData);
                emptyRowData.clear();
                data.add(t);
            } else if (null != t) {
                data.add(t);
            }

            if (null != unParsed && !unParsed.isEmpty()) {
                unParsedData.add(unParsed);
            }
        }
    }

    /**
     * 读取额外信息,在invoke方法执行完成后,doAfterAllAnalysed方法前执行
     * 这里主要读取合并单元格信息
     */
    @Override
    public void extra(CellExtra extra, AnalysisContext context) {
        switch (extra.getType()) {
            case MERGE: {
                mergeCells.add(extra);
                break;
            }
            default:
        }
    }


    @Override
    public void invokeHeadMap(Map<Integer, String> headMap, AnalysisContext context) {
        log.info("当前第{}行为标题行", context.readRowHolder().getRowIndex());
        if (null == headMap) {
            this.headMap = Collections.emptyMap();
        } else {
            this.headRowIndex = context.readRowHolder().getRowIndex();
            this.headMap = headMap;
        }
        // 版本缺陷 最后一列如果数据为空导致head.size != data.size https://github.com/alibaba/easyexcel/issues/3108
        context.readSheetHolder().setMaxNotEmptyDataHeadSize(this.headMap.size());
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {
        if (CollectionUtils.isNotEmpty(mergeCells)) {
            for (CellExtra extra : mergeCells) {
                // 过滤合并标题，启始行应该大于表头行
                if (extra.getFirstRowIndex() <= headRowIndex) {
                    continue;
                }
                if (data.size() < extra.getFirstRowIndex()) {
                    continue;
                }

                // 因为可以指定区域，所以有时候不一定会全部读取到
                int maxRowIndex = Math.min(extra.getLastRowIndex() - headRowIndex, data.size());
                // 需要-1扣除标题行
                int firstDataIndex = extra.getFirstRowIndex() - headRowIndex - 1;
                T firstRowObj = data.get(firstDataIndex);
                String firstColumnHead = getColumnFiled(extra.getFirstColumnIndex());
                for (int i = firstDataIndex; i < maxRowIndex; i++) {
                    T rowData = data.get(i);

                    for (int j = extra.getFirstColumnIndex(); j <= extra.getLastColumnIndex(); j++) {
                        String columnHead = getColumnFiled(j);
                        if (unParsedData.contains(columnHead)) {
                            //  这里没办法直接拿到字符串值
//                            unParsedData.get(i).put(columnHead, )
                        } else {
                            ReflectionUtils.copyProperty(firstRowObj, firstColumnHead, rowData, columnHead);
                        }
                    }
                }
            }
        }

        log.info("excel解析完成!");
    }

    /**
     * 获取当前列对应的实体字段，老逻辑表头列名即为字段名，需要兼容
     *
     * @param columnIndex 列下标
     * @return 当前列对应的实体字段
     */
    private String getColumnFiled(int columnIndex) {
        String columnFiled;
        if (MapUtils.isNotEmpty(columnFieldMap)) {
            columnFiled = columnFieldMap.get(ExcelCommonUtils.indexToCellColumn(columnIndex));
        } else {
            columnFiled = headMap.get(columnIndex);
        }
        return columnFiled;
    }
}
