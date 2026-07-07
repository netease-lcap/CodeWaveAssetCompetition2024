package com.netease.lowcode.extensions;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelReader;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.cache.selector.SimpleReadCacheSelector;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.excel.exception.ExcelAnalysisException;
import com.alibaba.excel.read.metadata.ReadSheet;
import com.alibaba.excel.read.metadata.holder.ReadHolder;
import com.alibaba.excel.read.metadata.holder.csv.CsvReadSheetHolder;
import com.alibaba.excel.read.metadata.holder.xls.XlsReadSheetHolder;
import com.alibaba.excel.read.metadata.holder.xlsx.XlsxReadSheetHolder;
import com.alibaba.excel.util.ListUtils;
import com.alibaba.excel.write.builder.ExcelWriterBuilder;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.alibaba.excel.write.style.column.LongestMatchColumnWidthStyleStrategy;
import com.alibaba.excel.write.style.column.SimpleColumnWidthStyleStrategy;
import com.alibaba.excel.write.style.row.SimpleRowHeightStyleStrategy;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.netease.cloud.codewave.file.connector.AbstractFileConnector;
import com.netease.cloud.codewave.file.connector.FileConnectionManager;
import com.netease.cloud.codewave.file.connector.FileDownloadResult;
import com.netease.cloud.codewave.file.connector.utils.CodeWaveFileUrl;
import com.netease.lowcode.core.annotation.NaslLogic;
import com.netease.lowcode.extensions.extensions.ShowImageConverter;
import com.netease.lowcode.extensions.listeners.LibraryReadListener;
import com.netease.lowcode.extensions.model.HeadData;
import com.netease.lowcode.extensions.model.ParseRequest;
import com.netease.lowcode.extensions.model.QueryCondition;
import com.netease.lowcode.extensions.model.RowData;
import com.netease.lowcode.extensions.response.ExportBigDataResponse;
import com.netease.lowcode.extensions.response.ParseBigDataResponse;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component("libraryEasyExcelTools")
public class EasyExcelTools {
    private static final String UPLOAD_API_PATH_PREFIX = "/upload";
    private static final Logger log = LoggerFactory.getLogger(EasyExcelTools.class);
    private static ExecutorService executor = new ThreadPoolExecutor(8, 32, 5000, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<>(5000), new ThreadPoolExecutor.AbortPolicy());
    private static OkHttpClient okHttpClient = new OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .build();
    private static FileUtils fileUtils;

    /**
     * 解析excel 支持xlsx xls csv
     *
     * @param url
     * @param sheetNames
     * @param headRow
     * @return
     */
    @NaslLogic
    public static ParseExcelResult parseExcel(String url, List<String> sheetNames, Integer headRow) {

        if (StringUtils.isBlank(url)) {
            ParseExcelResult result = new ParseExcelResult();
            result.setSuccess(false);
            result.setErrorMsg("非法的文件下载路径: " + url);

            return result;
        }

        try (InputStream inputStream = openUrlStream(url)) {
            return parseExcelBySheetName(inputStream, sheetNames, headRow);
        } catch (IOException e) {
            log.error("下载excel文件发生异常", e);
            throw new RuntimeException("下载excel文件发生异常", e);
        }
    }

    /**
     * 读取第一个sheet
     *
     * @param url
     * @param headRow
     * @return
     */
    @NaslLogic
    public static ParseSheetResult parseFirstSheet(String url, Integer headRow) {
        if (StringUtils.isBlank(url)) {
            ParseSheetResult result = new ParseSheetResult();
            result.setSuccess(false);
            result.setErrorMsg("非法的文件下载路径: " + url);

            return result;
        }
        try (InputStream inputStream = openUrlStream(url)) {
            ParseExcelResult parseExcelResult = parseExcelBySheetName(inputStream, new ArrayList<>(), headRow);

            List<ParseSheetResult> list = new ArrayList<>(parseExcelResult.getSheetMap().values());

            Integer zero = new Integer(0);

            for (ParseSheetResult sheetResult : list) {
                if (zero.equals(sheetResult.getSheetNo())) {
                    return sheetResult;
                }
            }

            ParseSheetResult result = new ParseSheetResult();
            result.setSuccess(false);
            return result;
        } catch (IOException e) {
            log.error("下载excel文件发生异常", e);
            throw new RuntimeException("下载excel文件发生异常", e);
        }
    }

    public static InputStream openUrlStream(String url) {
        try {
            if (isFileConnectorUrl(url)) {
                return fileDownloadV2(url);
            }

            Request request = new Request.Builder().url(getFileUrl(url)).get().build();
            Response response = okHttpClient.newCall(request).execute();

            if (!response.isSuccessful()) {
                throw new RuntimeException("下载文件发生异常, code: " + response.code() + ", message: " + response.message());
            }

            return response.body().byteStream();
        } catch (IOException e) {
            log.error("下载文件发生异常", e);
            throw new RuntimeException("下载文件发生异常", e);
        } catch (Exception e) {
            log.error("下载文件发生异常", e);
            throw new RuntimeException("下载文件发生异常", e);
        }
    }

    private static boolean isFileConnectorUrl(String url) {
        String normalizedUrl = StringUtils.trimToEmpty(url);
        return StringUtils.startsWith(normalizedUrl, UPLOAD_API_PATH_PREFIX)
                || StringUtils.startsWith(normalizedUrl, "upload");
    }

    private static InputStream fileDownloadV2(String fileUrl) {
        String path = dealPath(fileUrl);
        AbstractFileConnector defaultConnector = FileConnectionManager.getDefaultFileConnector();
        CodeWaveFileUrl fileUrlCodeWaveFileUrl = CodeWaveFileUrl.fromUri(path);
        FileDownloadResult result = defaultConnector.download(fileUrlCodeWaveFileUrl, new HashMap<>());
        InputStream inputStream = result.getInputStream();
        if (Objects.isNull(inputStream)) {
            throw new RuntimeException("下载文件发生异常, 文件流为空: " + fileUrl);
        }
        return inputStream;
    }

    private static String dealPath(String path) {
        path = StringUtils.trimToEmpty(path).replaceAll("/{2,}", "/");
        if (StringUtils.startsWith(path, UPLOAD_API_PATH_PREFIX)) {
            path = path.substring(UPLOAD_API_PATH_PREFIX.length());
        } else if (StringUtils.startsWith(path, "upload")) {
            path = path.substring("upload".length());
        }
        if (path.startsWith("/")) {
            path = path.substring(1);
        }
        return path;
    }

    /**
     * 获取文件下载路径，因为是请求本服务所以替换域名为localhost避免出现一些hostname unknown的情况
     *
     * @param url
     * @return
     */
    private static String getFileUrl(String url) {
        try {
            int index = url.indexOf("/upload/");
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
            int port = request.getLocalPort();
            if (port == -1) {
                return url;
            }
            if (index >= 0) {
                url = "http://localhost:" + port + "/upload/" + url.substring(index + "/upload/".length());
            }
        } catch (Exception e) {
            log.error("url转本地地址异常", e);
        }
        return url;
    }

    /**
     * 读取Excel前第N行作为表头数据
     *
     * @param url            Excel文件url
     * @param sheetName      工作表名称（null表示第一个sheet）
     * @param headerRowCount 需要读取的表头行数（从0开始）
     * @return 表头数据列表，每个元素代表一行表头
     */
    @NaslLogic
    public static List<List<String>> readHeaderRows(String url,
                                                    String sheetName,
                                                    Integer headerRowCount) {
        if (StringUtils.isBlank(url)) {
            ParseSheetResult result = new ParseSheetResult();
            result.setSuccess(false);
            result.setErrorMsg("非法的文件下载路径: " + url);
            throw new IllegalArgumentException("非法的文件下载路径:" + url);
        }
        try (InputStream inputStream = openUrlStream(url)) {
            return readHeaderRows(inputStream, sheetName, headerRowCount);
        } catch (RuntimeException e) {
            log.error("excel解析失败", e);
            throw new IllegalArgumentException("excel解析失败", e);
        } catch (IOException e) {
            log.error("文件下载失败", e);
            throw new IllegalArgumentException("文件下载失败", e);
        }
    }

    public static List<List<String>> readHeaderRows(InputStream inputStream,
                                                    String sheetName,
                                                    int headerRowCount) {
        // 参数校验
        if (inputStream == null) {
            throw new IllegalArgumentException("输入流不能为null");
        }
        if (headerRowCount < 0) {
            throw new IllegalArgumentException("表头行数不能为负数");
        }
        // 使用监听器精确控制读取行数
        final List<List<String>> headerRows = new ArrayList<>(headerRowCount);
        try {
            EasyExcel.read(inputStream, new AnalysisEventListener<Object>() {
                        private int currentRow = -1;

                        @Override
                        public void invokeHeadMap(Map<Integer, String> headMap, AnalysisContext context) {
                            if (currentRow < headerRowCount) {
                                headerRows.add(new ArrayList<>(headMap.values()));
                                currentRow++;
                            }
                            if (currentRow >= headerRowCount) {
                                throw new ExcelAnalysisException("表头读取完成");
                            }
                        }

                        @Override
                        public void invoke(Object data, AnalysisContext context) {
                        }

                        @Override
                        public void doAfterAllAnalysed(AnalysisContext context) {
                        }
                    })
                    .headRowNumber(headerRowCount + 1)
                    .sheet(sheetName)
                    .doRead();
        } catch (ExcelAnalysisException e) {
            // 忽略我们主动抛出的中断异常
            if (!"表头读取完成".equals(e.getMessage())) {
                throw e;
            }
        }
        return headerRows;
    }

    /**
     * 解析excel，支持xls\xlsx
     *
     * @param inputStream
     * @param sheetName
     * @param headRow
     * @return
     */
    public static ParseExcelResult parseExcelBySheetName(InputStream inputStream, List<String> sheetName, Integer headRow) {

        ParseExcelResult parseExcelResult = new ParseExcelResult();
        parseExcelResult.setSheetMap(new HashMap<>());

        ExcelReader reader = EasyExcel.read(inputStream, new AnalysisEventListener<Map<Integer, String>>() {
                    @Override
                    public void invoke(Map<Integer, String> o, AnalysisContext context) {

                        ReadHolder readHolder = context.currentReadHolder();

                        if (readHolder instanceof XlsxReadSheetHolder) {
                            XlsxReadSheetHolder xlsxReadSheetHolder = (XlsxReadSheetHolder) readHolder;
                            ParseSheetResult sheetResult = parseExcelResult.getSheetMap().get(xlsxReadSheetHolder.getSheetName());

                            sheetResult.getDataList().add(new ArrayList<>(o.values()));
                        } else if (readHolder instanceof XlsReadSheetHolder) {
                            XlsReadSheetHolder xlsReadSheetHolder = (XlsReadSheetHolder) readHolder;
                            ParseSheetResult sheetResult = parseExcelResult.getSheetMap().get(xlsReadSheetHolder.getSheetName());
                            sheetResult.getDataList().add(new ArrayList<>(o.values()));
                        } else if (readHolder instanceof CsvReadSheetHolder) {
                            CsvReadSheetHolder csvReadSheetHolder = (CsvReadSheetHolder) readHolder;
                            String sheetName1 = csvReadSheetHolder.getSheetName();
                            if (Objects.isNull(sheetName1)) {
                                sheetName1 = "";
                            }
                            ParseSheetResult sheetResult = parseExcelResult.getSheetMap().get(sheetName1);
                            sheetResult.getDataList().add(new ArrayList<>(o.values()));
                        }

                    }

                    @Override
                    public void invokeHeadMap(Map<Integer, String> headMap, AnalysisContext context) {
                        log.info("读取表头数据");

                        ReadHolder readHolder = context.currentReadHolder();

                        ParseSheetResult sheetResult = null;

                        if (readHolder instanceof XlsxReadSheetHolder) {
                            XlsxReadSheetHolder xlsxReadSheetHolder = (XlsxReadSheetHolder) readHolder;
                            sheetResult = parseExcelResult.getSheetMap().get(xlsxReadSheetHolder.getSheetName());

                            sheetResult.setSheetNo(xlsxReadSheetHolder.getSheetNo());
                            sheetResult.setSheetName(xlsxReadSheetHolder.getSheetName());

                            List<String> hh = new ArrayList<>();
                            sheetResult.getHeadList().add(hh);
                            hh.addAll(headMap.values());
                        } else if (readHolder instanceof XlsReadSheetHolder) {
                            XlsReadSheetHolder xlsReadSheetHolder = (XlsReadSheetHolder) readHolder;
                            sheetResult = parseExcelResult.getSheetMap().get(xlsReadSheetHolder.getSheetName());

                            sheetResult.setSheetNo(xlsReadSheetHolder.getSheetNo());
                            sheetResult.setSheetName(xlsReadSheetHolder.getSheetName());

                            List<String> hh = new ArrayList<>();
                            sheetResult.getHeadList().add(hh);
                            hh.addAll(headMap.values());
                        } else if (readHolder instanceof CsvReadSheetHolder) {
                            CsvReadSheetHolder csvReadSheetHolder = (CsvReadSheetHolder) readHolder;
                            String sheetName1 = csvReadSheetHolder.getSheetName();
                            if (Objects.isNull(sheetName1)) {
                                sheetName1 = "";
                            }
                            sheetResult = parseExcelResult.getSheetMap().get(sheetName1);
                            sheetResult.setSheetNo(csvReadSheetHolder.getSheetNo());
                            sheetResult.setSheetName(csvReadSheetHolder.getSheetName());

                            List<String> hh = new ArrayList<>();
                            sheetResult.getHeadList().add(hh);
                            hh.addAll(headMap.values());
                        }

                        // 处理复杂表头
                        if (sheetResult != null) {
                            List<List<String>> headList = sheetResult.getHeadList();

                            if (CollectionUtils.isNotEmpty(headList)) {
                                List<String> head1 = headList.get(0);
                                // 第一行表头 向前对齐
                                for (int i = 1; i < head1.size(); i++) {
                                    if (Objects.nonNull(head1.get(i))) {
                                        continue;
                                    }
                                    head1.set(i, head1.get(i - 1));
                                }

                            }
                            // 第二行表头，向上对齐
                            if (CollectionUtils.isNotEmpty(headList) && headList.size() > 1) {
                                for (int i = 1; i < headList.size(); i++) {
                                    List<String> headi = headList.get(i);
                                    List<String> parentHead = headList.get(i - 1);
                                    for (int j = 0; j < headi.size(); j++) {
                                        if (Objects.nonNull(headi.get(j))) {
                                            continue;
                                        }
                                        headi.set(j, parentHead.get(j));
                                    }
                                }
                            }
                        }
                    }

                    @Override
                    public void doAfterAllAnalysed(AnalysisContext analysisContext) {
                        log.info("所有数据解析完成");
                    }
                })
                // 指定表头有几行
                .headRowNumber(headRow).build();

        List<ReadSheet> readSheets = reader.excelExecutor().sheetList();
        for (ReadSheet readSheet : readSheets) {
            // 不指定sheet就全部解析
            if (CollectionUtils.isEmpty(sheetName) || sheetName.contains(readSheet.getSheetName())) {
                ParseSheetResult sheet = new ParseSheetResult();

                sheet.setHeadList(new ArrayList<>());
                sheet.setDataList(new ArrayList<>());

                String sheetName1 = readSheet.getSheetName();
                if (Objects.isNull(sheetName1)) {
                    sheetName1 = "";
                }
                parseExcelResult.getSheetMap().put(sheetName1, sheet);
                reader.read(readSheet);
                // 补齐空数据
                if (CollectionUtils.isEmpty(sheet.getHeadList())) {
                    continue;
                }
                int size = sheet.getHeadList().get(0).size();
                for (List<String> dataList : sheet.getDataList()) {
                    if (dataList.size() < size) {
                        int i1 = size - dataList.size();
                        for (int i = 0; i < i1; i++) {
                            dataList.add(null);
                        }
                    }
                }
            }
        }

        parseExcelResult.setSuccess(true);
        return parseExcelResult;
    }

    /**
     * 根据输入生excel。
     * 若数据为图片链接，若要将图片展示在excel中，须给此数据加上codewave_excel_pic:前缀。不支持csv文件。
     *
     * @param excelData
     * @return
     */
    @NaslLogic
    public static ExcelResponse createExcelWithUrl(CustomExcelData excelData) {

        try {
            String path = createExcel(excelData);
            File file = new File(path);

            // 改造为使用本地存储
            UploadResponseDTO uploadResponseDTO = fileUtils.uploadFileV2(file);
            file.delete();
            File parent = new File(file.getParent());
            parent.delete();

            return ExcelResponse.OK(uploadResponseDTO.getResult());
        } catch (Throwable throwable) {
            log.error("生成excel文件失败", throwable);
            return ExcelResponse.FAIL(throwable.toString(), Arrays.toString(throwable.getStackTrace()));
        }
    }

    /**
     * [推荐] 解析excel
     *
     * @param handle
     * @param request
     * @return
     */
    @NaslLogic
    public static ParseBigDataResponse parseBigDataExcel(Function<List<String>, String> handle, ParseRequest request) {

        if (Objects.isNull(request) || StringUtils.isBlank(request.fullClassName)) {
            return ParseBigDataResponse.FAIL("请求参数为空,fullClassName不能为空！");
        }
        if (StringUtils.isBlank(request.url)) {
            return ParseBigDataResponse.FAIL("文件url不能为空！");
        }
        Class<?> clazz;
        try {
            clazz = Class.forName(request.getFullClassName(), false, Thread.currentThread().getContextClassLoader());
        } catch (ClassNotFoundException e) {
            log.error("加载类失败", e);
            return ParseBigDataResponse.FAIL(String.format("加载类[%s]失败！", request.getFullClassName()), e.getMessage() + Arrays.toString(e.getStackTrace()));
        }

        LibraryReadListener<?> readListener = new LibraryReadListener<>(handle, clazz, request, fileUtils);
        SimpleReadCacheSelector simpleReadCacheSelector = new SimpleReadCacheSelector();
        // 放多少批数据在内存，默认20
        if (Objects.nonNull(request.getMaxCacheActivateBatchCount())) {
            simpleReadCacheSelector.setMaxCacheActivateBatchCount(request.getMaxCacheActivateBatchCount());
        }
        try (InputStream inputStream = openUrlStream(request.getUrl())) {
            EasyExcel.read(inputStream, clazz, readListener).readCacheSelector(simpleReadCacheSelector).sheet().doRead();
        } catch (RuntimeException e) {
            log.error("excel解析失败", e);
            return ParseBigDataResponse.FAIL("excel解析失败！", e.getMessage() + Arrays.toString(e.getStackTrace()));
        } catch (IOException e) {
            log.error("文件下载失败", e);
            return ParseBigDataResponse.FAIL("文件下载失败", e.getMessage() + Arrays.toString(e.getStackTrace()));
        }
        return ParseBigDataResponse.OK(readListener.getTotal());
    }

    /**
     * [推荐使用]导出大数据量excel
     *
     * @param queryData
     * @param condition
     * @param saveResult
     * @return
     */
    @NaslLogic
    public static ExportBigDataResponse exportBigDataExcelV2(Function<QueryCondition, List<String>> queryData,
                                                             QueryCondition condition,
                                                             Function<ExportBigDataResponse, Boolean> saveResult) {

        if (Objects.isNull(condition) || Objects.isNull(condition.pageSize) || Objects.isNull(condition.totalPages) || Objects.isNull(condition.pageNum)) {
            // 查询条件为空
            return ExportBigDataResponse.FAIL("查询参数为空,firstPageNum、pageSize、totalPages不能为空！");
        }

        if (condition.pageSize < 0 || condition.totalPages < 0 || condition.pageNum < 0) {
            return ExportBigDataResponse.FAIL("查询条件,firstPageNum、pageSize、totalPages不能为负数！");
        }

        if (condition.pageNum > condition.totalPages) {
            return ExportBigDataResponse.FAIL("查询页码不能大于总页数！请检查firstPageNum、totalPages");
        }

        if (StringUtils.isBlank(condition.getFullClassName())) {
            return ExportBigDataResponse.FAIL("java类全路径名不能为空！");
        }
        Class<?> clazz;
        try {
            clazz = Class.forName(condition.getFullClassName(), false, Thread.currentThread().getContextClassLoader());
        } catch (ClassNotFoundException e) {
            log.error("加载类失败", e);
            return ExportBigDataResponse.FAIL(String.format("加载类[%s]失败！", condition.getFullClassName()), Arrays.toString(e.getStackTrace()));
        }

        if (condition.isAsync) {
            if (Objects.isNull(saveResult)) {
                return ExportBigDataResponse.FAIL("异步执行时，回调逻辑不能为空！");
            }

            try {

                // 异步执行
                executor.execute(() -> {
                    // 通过回调将结果返回
                    saveResult.apply(handleDataV2(queryData, condition, clazz));
                });

                return ExportBigDataResponse.OK("已提交异步执行，请稍后在回调逻辑中处理结果");
            } catch (Throwable throwable) {
                log.error("提交异步执行出错", throwable);
                return ExportBigDataResponse.FAIL("提交异步执行出错," + throwable.getMessage(), Arrays.toString(throwable.getStackTrace()));
            }

        } else {
            return handleDataV2(queryData, condition, clazz);
        }
    }

    public static ExportBigDataResponse handleDataV2(Function<QueryCondition, List<String>> queryData,
                                                     QueryCondition condition, Class<?> clazz) {
        File exportFile = null;

        try {

            long start = System.currentTimeMillis();

            String fileName;

            // 未指定名称，自动设置一个
            if (StringUtils.isBlank(condition.fileName)) {
                fileName = "download_" + System.currentTimeMillis() + "_" + new Random().nextInt(1000) + ".xlsx";
            } else if (!condition.fileName.endsWith(".xlsx")) {
                fileName = condition.fileName + ".xlsx";
            } else {
                fileName = condition.fileName;
            }

            // 创建目录
            String path = String.join("/", "data", String.valueOf(System.currentTimeMillis()));
            File dir = new File(path);
            if (!dir.mkdirs()) {
                return ExportBigDataResponse.FAIL(String.format("创建目录失败:%s", path));
            }

            exportFile = new File(path, fileName);
            FileOutputStream fos = new FileOutputStream(exportFile);

            ExcelWriter excelWriter = EasyExcel.write(fos).build();

            // 每个sheet支持的最大行数（允许用户设置）
            int sheetTotalRows;
            if (Objects.isNull(condition.sheetTotalRows) || condition.sheetTotalRows < 1 || condition.sheetTotalRows > 1048576) {
                sheetTotalRows = 10000;
            } else {
                sheetTotalRows = condition.sheetTotalRows;
            }

            // 记录sheet当前写入的行数
            // (注意headRows可能也占用可用的sheetRows，这里采取的做法是用户计算sheetTotalRows时自己减去表头行数)
            int sheetCurrentRows = 0;
            // 当前sheet编号
            int currentSheetNo = 0;

            // 记录写入总数
            long total = 0;

            // 分页查询 pageNum从0开始
            for (Integer i = condition.pageNum; i < condition.totalPages; i++) {

                QueryCondition query = new QueryCondition();
                BeanUtils.copyProperties(condition, query);
                query.setPageNum(i);

                List<String> dataStr = queryData.apply(query);
                if (CollectionUtils.isEmpty(dataStr)) {
                    continue;
                }

                // 将json序列化的结果转为对象
//                List<Object> data = dataStr.stream().map((item) -> JSON.parseObject(item, clazz)).collect(Collectors.toList());
                List<Object> data = dataStr.stream().map((item) -> {
                    try {
                        return JsonUtil.fromJson(item, clazz);
                    } catch (JsonProcessingException e) {
                        log.error("json转换失败", e);
                        throw new RuntimeException(e);
                    }
                }).collect(Collectors.toList());
                total += data.size();

                // 可直接写入当前sheet
                if (sheetCurrentRows + data.size() <= sheetTotalRows) {
                    writeSheetV2(clazz, excelWriter, currentSheetNo, data);
                    sheetCurrentRows += data.size();
                }
                // 需要将多出的数据自动拆分到下一个sheet
                else {
                    // 需要写入当前sheet的部分
                    int leftRows = sheetTotalRows - sheetCurrentRows;
                    List<Object> leftData = data.subList(0, leftRows);
                    writeSheetV2(clazz, excelWriter, currentSheetNo, leftData);

                    // 注意考虑，pageSize > sheetTotalRows的情况
                    // 需要拆分写入下一个sheet的部分
                    int rightRows = data.size() - leftRows;
                    currentSheetNo += 1;
                    sheetCurrentRows = 0;// 新Sheet 记录重置0

                    int leftIndex = leftRows;
                    while (rightRows > sheetTotalRows) {

                        List<Object> rightData = data.subList(leftIndex, leftIndex + sheetTotalRows);

                        writeSheetV2(clazz, excelWriter, currentSheetNo, rightData);

                        // 计算剩下的rows
                        rightRows -= sheetTotalRows;
                        currentSheetNo += 1;
                        leftIndex += sheetTotalRows;
                    }
                    List<Object> rightData = data.subList(leftIndex, data.size());
                    writeSheetV2(clazz, excelWriter, currentSheetNo, rightData);
                    sheetCurrentRows += rightRows;
                }

            }

            excelWriter.finish();
            fos.flush();
            fos.close();

            log.info("excel创建成功,size={} 字节", exportFile.length());

            // 文件上传
            UploadResponseDTO uploadResponseDTO = fileUtils.uploadFileV2(exportFile);

            return ExportBigDataResponse.OK(uploadResponseDTO.getFilePath(), uploadResponseDTO.getResult(), (double) (System.currentTimeMillis() - start) / 1000, (double) exportFile.length())
                    .total(total);
        } catch (Throwable throwable) {
            log.error("创建excel出错", throwable);
            return ExportBigDataResponse.FAIL("创建excel出错," + throwable.getMessage(), Arrays.toString(throwable.getStackTrace()));
        } finally {
            // 删除文件
            if (Objects.nonNull(exportFile)) {
                FileUtils.delete(exportFile.getParentFile());
            }
        }
    }

    /**
     * 导出大数据量excel
     * 推荐使用exportBigDataExcelV2
     *
     * @param queryData  用户自定义数据查询逻辑
     * @param condition  用户数据查询条件
     * @param saveResult 用户自定义结果处理逻辑
     * @return
     */
    @NaslLogic
    @Deprecated
    public static ExportBigDataResponse exportBigDataExcel(Function<QueryCondition, List<RowData>> queryData,
                                                           QueryCondition condition,
                                                           Function<ExportBigDataResponse, Boolean> saveResult) {

        if (Objects.isNull(condition) || Objects.isNull(condition.pageSize) || Objects.isNull(condition.totalPages) || Objects.isNull(condition.pageNum)) {
            // 查询条件为空
            return ExportBigDataResponse.FAIL("查询参数为空,firstPageNum、pageSize、totalPages不能为空！");
        }

        if (condition.pageSize < 0 || condition.totalPages < 0 || condition.pageNum < 0) {
            return ExportBigDataResponse.FAIL("查询条件,firstPageNum、pageSize、totalPages不能为负数！");
        }

        if (condition.pageNum > condition.totalPages) {
            return ExportBigDataResponse.FAIL("查询页码不能大于总页数！请检查firstPageNum、totalPages");
        }

        if (condition.isAsync) {

            if (Objects.isNull(saveResult)) {
                return ExportBigDataResponse.FAIL("异步执行时，回调逻辑不能为空！");
            }

            try {

                // 异步执行
                executor.execute(() -> {
                    // 通过回调将结果返回
                    saveResult.apply(handleData(queryData, condition));
                });

                return ExportBigDataResponse.OK("已提交异步执行，请稍后在回调逻辑中处理结果");
            } catch (Throwable throwable) {
                log.error("提交异步执行出错", throwable);
                return ExportBigDataResponse.FAIL("提交异步执行出错," + throwable.getMessage(), Arrays.toString(throwable.getStackTrace()));
            }
        } else {

            return handleData(queryData, condition);
        }

    }

    public static ExportBigDataResponse handleData(Function<QueryCondition, List<RowData>> queryData, QueryCondition condition) {

        File exportFile = null;

        try {

            long start = System.currentTimeMillis();

            String fileName;

            // 未指定名称，自动设置一个
            if (StringUtils.isBlank(condition.fileName)) {
                fileName = "download_" + System.currentTimeMillis() + "_" + new Random().nextInt(1000) + ".xlsx";
            } else if (!condition.fileName.endsWith(".xlsx")) {
                fileName = condition.fileName + ".xlsx";
            } else {
                fileName = condition.fileName;
            }

            // 创建目录
            String path = String.join("/", "data", String.valueOf(System.currentTimeMillis()));
            File dir = new File(path);
            if (!dir.mkdirs()) {
                return ExportBigDataResponse.FAIL2(String.format("创建目录失败:%s", path), condition.getCustomQueryCondition());
            }

            exportFile = new File(path, fileName);
            FileOutputStream fos = new FileOutputStream(exportFile);

            ExcelWriter excelWriter = EasyExcel.write(fos).build();

            // 每个sheet支持的最大行数（允许用户设置）
            int sheetTotalRows;
            if (Objects.isNull(condition.sheetTotalRows) || condition.sheetTotalRows < 1 || condition.sheetTotalRows > 1048576) {
                sheetTotalRows = 10000;
            } else {
                sheetTotalRows = condition.sheetTotalRows;
            }

            // 记录sheet当前写入的行数
            // (注意headRows可能也占用可用的sheetRows，这里采取的做法是用户计算sheetTotalRows时自己减去表头行数)
            int sheetCurrentRows = 0;
            // 当前sheet编号
            int currentSheetNo = 0;

            // 分页查询
            for (Integer i = condition.pageNum; i < condition.totalPages; i++) {

                QueryCondition query = new QueryCondition();
                BeanUtils.copyProperties(condition, query);
                query.setPageNum(i);

                List<RowData> data = queryData.apply(query);
                if (CollectionUtils.isEmpty(data)) {
                    continue;
                }

                // 可直接写入当前sheet
                if (sheetCurrentRows + data.size() <= sheetTotalRows) {
                    writeSheet(condition, excelWriter, currentSheetNo, data);
                    sheetCurrentRows += data.size();
                }
                // 需要将多出的数据自动拆分到下一个sheet
                else {
                    // 需要写入当前sheet的部分
                    int leftRows = sheetTotalRows - sheetCurrentRows;
                    List<RowData> leftData = data.subList(0, leftRows);
                    writeSheet(condition, excelWriter, currentSheetNo, leftData);

                    // 注意考虑，pageSize > sheetTotalRows的情况
                    // 需要拆分写入下一个sheet的部分
                    int rightRows = data.size() - leftRows;
                    currentSheetNo += 1;
                    sheetCurrentRows = 0;// 新Sheet 记录重置0

                    int leftIndex = leftRows;
                    while (rightRows > sheetTotalRows) {

                        List<RowData> rightData = data.subList(leftIndex, leftIndex + sheetTotalRows);

                        writeSheet(condition, excelWriter, currentSheetNo, rightData);

                        // 计算剩下的rows
                        rightRows -= sheetTotalRows;
                        currentSheetNo += 1;
                        leftIndex += sheetTotalRows;
                    }
                    List<RowData> rightData = data.subList(leftIndex, data.size());
                    writeSheet(condition, excelWriter, currentSheetNo, rightData);
                    sheetCurrentRows += rightRows;
                }

            }

            excelWriter.finish();
            fos.flush();
            fos.close();

            log.info("excel创建成功,size={} 字节", exportFile.length());

            // 文件上传
            UploadResponseDTO uploadResponseDTO = fileUtils.uploadFileV2(exportFile);

            return ExportBigDataResponse.OK(uploadResponseDTO.getFilePath(), uploadResponseDTO.getResult(), (double) (System.currentTimeMillis() - start) / 1000, (double) exportFile.length(),
                    condition.getCustomQueryCondition());
        } catch (Throwable throwable) {
            log.error("创建excel出错", throwable);
            return ExportBigDataResponse.FAIL("创建excel出错," + throwable.getMessage(), Arrays.toString(throwable.getStackTrace()), condition.getCustomQueryCondition());
        } finally {
            // 删除文件
            if (Objects.nonNull(exportFile)) {
                FileUtils.delete(exportFile.getParentFile());
            }
        }
    }

    private static void writeSheetV2(Class<?> clazz, ExcelWriter excelWriter, int currentSheetNo, List<Object> data) {

        WriteSheet sheet = EasyExcel.writerSheet(currentSheetNo, "Sheet" + (currentSheetNo + 1))
                .registerWriteHandler(new LongestMatchColumnWidthStyleStrategy()).build();
        sheet.setClazz(clazz);
        excelWriter.write(data, sheet);
    }

    private static void writeSheet(QueryCondition condition, ExcelWriter excelWriter, int currentSheetNo, List<RowData> data) {
        WriteSheet sheet = EasyExcel.writerSheet(currentSheetNo, "Sheet" + (currentSheetNo + 1))
                .registerWriteHandler(new SimpleColumnWidthStyleStrategy(condition.getColumnWidth())).build();
        if (CollectionUtils.isNotEmpty(condition.headList)) {
            sheet.setHead(condition.headList.stream().map(HeadData::getTitles).collect(Collectors.toList()));
        }
        excelWriter.write(data.stream().map(RowData::getColumns).collect(Collectors.toList()), sheet);
    }

    /**
     * 创建excel
     *
     * @param excelData
     */
    public static String createExcel(CustomExcelData excelData) {

        if (Objects.isNull(excelData)) {
            throw new RuntimeException("请求对象不能为空");
        }

        if (StringUtils.isBlank(excelData.fileName)) {
            throw new RuntimeException("文件名不能为空");
        }

        if (!((excelData.fileName.endsWith(".csv") || excelData.fileName.endsWith(".xlsx")))) {
            throw new RuntimeException("文件格式只支持 *.csv、*.xlsx");
        }

        String path = "data/" + System.currentTimeMillis() + "/";
        File file = new File(path);
        file.mkdirs();

        ExcelWriterBuilder excelWriterBuilder = EasyExcel.write(path + excelData.fileName);

        if (CollectionUtils.isEmpty(excelData.sheetList)) {
            throw new RuntimeException("sheet个数0");
        }
        SimpleRowHeightStyleStrategy rowHeightStyleStrategy = new SimpleRowHeightStyleStrategy(null, (short) 30); // 设置行高为30
        SimpleColumnWidthStyleStrategy columnWidthStyleStrategy = new SimpleColumnWidthStyleStrategy(10); // 设置列宽为20
        excelWriterBuilder.registerWriteHandler(rowHeightStyleStrategy);
        excelWriterBuilder.registerWriteHandler(columnWidthStyleStrategy);
        excelWriterBuilder.registerConverter(new ShowImageConverter());

        ExcelWriter writer = excelWriterBuilder.build();

        for (int i = 0; i < excelData.sheetList.size(); i++) {
            CustomSheetData customSheetData = excelData.sheetList.get(i);
            createSheet(i, customSheetData.sheetName, writer, customSheetData.head, customSheetData.data);
        }
        writer.finish();
        return path + excelData.fileName;
    }

    private static void createSheet(Integer sheetNo, String sheetName, ExcelWriter excelWriter, List<String> head, List<List<String>> data) {

        WriteSheet sheet = EasyExcel.writerSheet(sheetNo, sheetName).build();

        if (!CollectionUtils.isEmpty(head)) {
            List<List<String>> list = ListUtils.newArrayList();
            head.forEach(item -> list.add(ListUtils.newArrayList(item)));
            sheet.setHead(list);
        }
        excelWriter.write(data, sheet);

    }

    @Autowired
    @Qualifier("easyExcelFileUtils")
    public void setFileUtils(FileUtils fileUtils) {
        EasyExcelTools.fileUtils = fileUtils;
    }
}

