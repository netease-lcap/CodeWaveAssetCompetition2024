package com.netease.lowcode.extensions.listeners;

import cn.hutool.core.util.StrUtil;
import cn.hutool.poi.excel.ExcelPicUtil;
import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.metadata.CellExtra;
import com.alibaba.excel.metadata.data.ReadCellData;
import com.alibaba.excel.read.listener.ReadListener;
import com.alibaba.excel.util.ListUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.netease.lowcode.extensions.FileUtils;
import com.netease.lowcode.extensions.UploadResponseDTO;
import com.netease.lowcode.extensions.jackson.serializers.LocalDateSerializer;
import com.netease.lowcode.extensions.jackson.serializers.LocalTimeSerializer;
import com.netease.lowcode.extensions.jackson.serializers.ZonedDateTimeSerializer;
import com.netease.lowcode.extensions.model.ParseRequest;
import org.apache.poi.ss.usermodel.PictureData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.netease.lowcode.extensions.EasyExcelTools.openUrlStream;


public class LibraryReadListener<T> implements ReadListener<T> {
    private static final Logger log = LoggerFactory.getLogger(LibraryReadListener.class);

    @Target(ElementType.FIELD)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface Picture {
        int columnIndex() default 0;
    }

    /**
     * 每读取100条数据进行一次处理,防止oom
     */
    private static final int BATCH_COUNT = 100;
    private static ObjectMapper objectMapper;

    static {
        Jackson2ObjectMapperBuilder builder = Jackson2ObjectMapperBuilder
                .json()
                .indentOutput(false)
                .failOnEmptyBeans(false)
                .failOnUnknownProperties(false)
                .serializerByType(LocalTime.class, new LocalTimeSerializer())
                .serializerByType(ZonedDateTime.class, new ZonedDateTimeSerializer())
                .serializerByType(LocalDate.class, new LocalDateSerializer());
        objectMapper = builder.build();
    }

    /**
     * 缓存读取的数据
     */
    private List<T> cachedDataList = ListUtils.newArrayListWithExpectedSize(BATCH_COUNT);
    private Map<Integer, Map<String, PictureData>> excelPictureMap = new HashMap<>();
    private Class<T> type;
    private ParseRequest request;
    private Function<List<String>, String> handle;
    private FileUtils fileUtils;
    /**
     * 统计导入数量
     */
    private Long total = 0L;

    public LibraryReadListener(Function<List<String>, String> handle, Class<T> type, ParseRequest request, FileUtils fileUtils) {
        this.handle = handle;
        this.type = type;
        this.request = request;
        this.fileUtils = fileUtils;
    }


    private List<String> toJsonString(List<T> data) {
        // https://blog.csdn.net/fenglllle/article/details/114293427
        // 使用fastjson2会出现classloader问题(cast to exception)

        return data.stream().map(item -> {
            try {
                return objectMapper.writeValueAsString(item);
            } catch (JsonProcessingException e) {
                log.error("json序列化失败", e);
                throw new RuntimeException(e);
            }
        }).collect(Collectors.toList());
    }

    @Override
    public void invoke(T data, AnalysisContext context) {

        // 额外处理，校验空白行
        Field[] fields = data.getClass().getDeclaredFields();
        boolean blankRow = true;
        for (Field field : fields) {
            field.setAccessible(true);
            try {
                Object o = field.get(data);
                if (Objects.nonNull(o)) {
                    blankRow = false;
                    break;
                }
            } catch (IllegalAccessException e) {
                log.error("读取数据失败", e);
            }
            field.setAccessible(false);
        }
        // 跳过空白行
        if (blankRow) {
            return;
        }

        // 填充图片url
        fillPicture(data, context);
        // 统计导入数量
        total++;

        // 解析到一条数据，加入缓存
        cachedDataList.add(data);
        if (cachedDataList.size() >= BATCH_COUNT) {
            // 调用function处理数据
            handle.apply(toJsonString(cachedDataList));
            // 处理完清理list
            cachedDataList = ListUtils.newArrayListWithExpectedSize(BATCH_COUNT);
        }
    }

    /**
     * 解析excel中的图片，填充到对象中
     * TODO: wps插入图片时可以选择“浮动图片”和“嵌入式图片”，目前暂不支持wps的嵌入式图片，使用Poi无法读取
     * 如需支持，可参考：https://blog.csdn.net/maudboy/article/details/133145278
     *
     * @param data
     * @param context
     */
    private void fillPicture(T data, AnalysisContext context) {

        // 这里判断下是否需要解析图片，默认不解析，提高性能
        if (!request.hasImageColumn) {
            return;
        }

        // 读取图片数据 TODO: 暂不支持大型excel图片列解析，可能会造成内存溢出
        Map<String, PictureData> picMap;
        Integer sheetNo = context.readSheetHolder().getSheetNo();
        if (!excelPictureMap.containsKey(sheetNo)) {
            InputStream inputStream = openUrlStream(request.getUrl());
            ExcelReader reader = ExcelUtil.getReader(inputStream);
            picMap = ExcelPicUtil.getPicMap(reader.getWorkbook(), sheetNo);
            reader.close();
            excelPictureMap.put(sheetNo, picMap);
        } else {
            picMap = excelPictureMap.get(sheetNo);
        }

        Field[] fields = data.getClass().getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            try {
                // TODO: 目前只支持一列为图片，未做多列识别
                Picture picture = field.getAnnotation(Picture.class);
                if (Objects.isNull(picture)) {
                    continue;
                }

                Integer rowIndex = context.readRowHolder().getRowIndex();
                Integer columnIndex = picture.columnIndex();

                String key = StrUtil.format("{}_{}", rowIndex, columnIndex);
                PictureData pictureData = picMap.get(key);
                // 判断一下图片是否为null
                if (Objects.isNull(pictureData)) {
                    continue;
                }

                // 上传图片拿到url
                String[] mineTypes = pictureData.getMimeType().split(StrUtil.SLASH);
                String mineType = mineTypes[1];
                // 上传

                String sheetName = context.readSheetHolder().getSheetName();
                String imageName = StrUtil.format("{}-{}.{}", sheetName, key, mineType);
                FileOutputStream fos = new FileOutputStream(imageName);
                fos.write(pictureData.getData());
                fos.close();

                File file = new File(imageName);
                UploadResponseDTO uploadResponseDTO = fileUtils.uploadFileV2(file);
                // 删除文件
                file.delete();
                field.set(data, uploadResponseDTO.getResult());
                return;
            } catch (IllegalAccessException e) {
                log.error("IllegalAccessException:", e);
                // do nothing
            } catch (InvocationTargetException | NoSuchMethodException | IOException e) {
                log.error("InvocationTargetException | NoSuchMethodException | IOException e:", e);
                throw new RuntimeException(e);
            }
            field.setAccessible(false);
        }
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        // 调用function处理数据
        handle.apply(toJsonString(cachedDataList));
        // 数据处理完成
    }

    @Override
    public void onException(Exception exception, AnalysisContext context) throws Exception {
        ReadListener.super.onException(exception, context);
    }

    @Override
    public void invokeHead(Map<Integer, ReadCellData<?>> headMap, AnalysisContext context) {
        ReadListener.super.invokeHead(headMap, context);
    }

    @Override
    public void extra(CellExtra extra, AnalysisContext context) {
        ReadListener.super.extra(extra, context);
    }

    @Override
    public boolean hasNext(AnalysisContext context) {
        return ReadListener.super.hasNext(context);
    }

    /**
     * 获取导入数量
     *
     * @return
     */
    public Long getTotal() {
        return total;
    }
}

