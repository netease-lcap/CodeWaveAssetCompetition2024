package com.netease.http.httpclient;

import com.netease.http.dto.DtoConvert;
import com.netease.http.dto.LocalFileCacheDto;
import com.netease.http.dto.RequestParam;
import com.netease.http.dto.RequestParamAllBodyTypeInner;
import com.netease.http.exception.TransferCommonException;
import com.netease.http.util.FileNameValidator;
import com.netease.http.util.JsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RequestCallback;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.*;
import java.net.URI;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class HttpClientService {
    private static final Logger logger = LoggerFactory.getLogger("LCAP_EXTENSION_LOGGER");
    private final Map<String, LocalFileCacheDto> fileCache = new ConcurrentHashMap<>();

    public LocalFileCacheDto getFileCache(String fileKey) {
        return fileCache.get(fileKey);
    }

    public void removeFileCache(String fileKey) {
        if (fileCache.containsKey(fileKey)) {
            fileCache.remove(fileKey);
        }
    }

    public LocalFileCacheDto asyncUploadFileExchangeCommon(String fileTimeMillisKey, RestTemplate restTemplateFinal, RequestParam requestParam, String fileKey, File file) {
        LocalFileCacheDto localFileCacheDto = new LocalFileCacheDto(null, file.getName(), 3);
        fileCache.put(fileTimeMillisKey, localFileCacheDto);
        CompletableFuture.runAsync(() -> {
            logger.info("开始上传文件");
            try {
                String resBody = uploadFileExchangeCommon(restTemplateFinal, requestParam, fileKey, file);
                localFileCacheDto.setResBody(resBody);
                localFileCacheDto.setDownloadStatus(4);
                localFileCacheDto.setFileName(file.getName());
                fileCache.put(fileTimeMillisKey, localFileCacheDto);
            } catch (Exception e) {
                logger.error("上传文件失败Exception", e);
                localFileCacheDto.setDownloadStatus(6);
                localFileCacheDto.setFileName(file.getName());
                localFileCacheDto.setResBody("上传文件失败");
                fileCache.put(fileTimeMillisKey, localFileCacheDto);
            } catch (Throwable t) { // 捕获所有Throwable包括Error
                logger.error("上传文件失败Throwable", t);
                localFileCacheDto.setDownloadStatus(6);
                localFileCacheDto.setFileName(file.getName());
                localFileCacheDto.setResBody("上传文件失败");
                fileCache.put(fileTimeMillisKey, localFileCacheDto);
            } finally {
                file.delete();
            }
        });
        return localFileCacheDto;
    }

    /**
     * 上传文件到第三方系统通用方法
     *
     * @param requestParam
     * @param fileKey
     * @param file
     * @return
     */
    public String uploadFileExchangeCommon(RestTemplate restTemplateFinal, RequestParam requestParam, String fileKey, File file) {
        RequestParamAllBodyTypeInner requestParamInner = new RequestParamAllBodyTypeInner();
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        if (requestParam.getBody() != null) {
            requestParam.getBody().forEach(body::add);
        }
        if (StringUtils.isEmpty(fileKey)) {
            fileKey = "file";
        }
        body.add(fileKey, new FileSystemResource(file));
        requestParamInner.setBody(body);
        if (StringUtils.isEmpty(requestParam.getHttpMethod())) {
            requestParam.setHttpMethod(HttpMethod.GET.name());
        }
        requestParamInner.setHttpMethod(requestParam.getHttpMethod());
        requestParamInner.setUrl(requestParam.getUrl());
        requestParamInner.setHeader(requestParam.getHeader());
        ResponseEntity<String> exchange = this.exchangeInner(requestParamInner, restTemplateFinal, String.class);
        if (exchange.getStatusCode() == HttpStatus.OK) {
            return exchange.getBody();
        } else {
            throw new TransferCommonException(exchange.getStatusCodeValue(), JsonUtil.toJson(exchange));
        }
    }

    public <T> ResponseEntity<T> exchangeInner(RequestParamAllBodyTypeInner requestParam, RestTemplate restTemplateFinal, Class<T> responseType) {
        try {
            if (Objects.isNull(requestParam.getHeader())) {
                requestParam.setHeader(new HashMap<>());
            }
            if (Objects.isNull(requestParam.getHttpMethod())) {
                requestParam.setHttpMethod("");
            }
            if (Objects.isNull(requestParam.getUrl())) {
                requestParam.setUrl("");
            }
            URI uri = UriComponentsBuilder.fromUriString(requestParam.getUrl()).build().encode().toUri();
            HttpMethod requestMethod = HttpMethod.resolve(requestParam.getHttpMethod().toUpperCase());
            HttpHeaders httpHeaders = new HttpHeaders();
            requestParam.getHeader().forEach((headerName, headerValue) -> httpHeaders.add((String) headerName, (String) headerValue));
            HttpEntity<T> httpEntity = new HttpEntity<>(DtoConvert.convertToGeneric(requestParam.getBody()), httpHeaders);
            if (requestParam.getBody() == null) {
                httpEntity = new HttpEntity<>(httpHeaders);
            }
            return restTemplateFinal.exchange(uri, Objects.requireNonNull(requestMethod), httpEntity, responseType);
        } catch (Exception e) {
            logger.error("请求http失败", e);
            throw e;
        }
    }

    /**
     * 下载文件
     *
     * @param requestParam
     * @param restTemplateFinal
     * @param fileName
     * @return 文件路径
     */
    public String asyncDownloadFile(RequestParamAllBodyTypeInner requestParam, RestTemplate restTemplateFinal, String fileName) throws IOException {
        String fileKey = System.currentTimeMillis() + "d";
        fileCache.put(fileKey, new LocalFileCacheDto(null, fileName, 1));
//  流式下载
        Path parentFile = Paths.get("./local_file").toAbsolutePath().normalize();
        File file = new File(parentFile.toUri());
        if (!file.exists()) {
            Files.createDirectories(parentFile);
        }
        CompletableFuture.runAsync(() -> {
            try {
                downloadFileBigFile(requestParam, restTemplateFinal, fileName, fileKey, parentFile);
            } catch (Exception e) {
                logger.error("下载文件失败", e);
                fileCache.put(fileKey, new LocalFileCacheDto(null, fileName, 5));
            }
        });
        return fileKey;
    }

    public void downloadFileBigFile(RequestParamAllBodyTypeInner requestParam, RestTemplate restTemplateFinal, String fileName, String fileKey, Path parentFile) {
        RequestCallback requestCallback = request ->
                request.getHeaders().setAccept(Collections.singletonList(MediaType.APPLICATION_OCTET_STREAM));
        AtomicInteger count = new AtomicInteger();
        restTemplateFinal.execute(requestParam.getUrl(), HttpMethod.resolve(requestParam.getHttpMethod().toUpperCase()), requestCallback, response -> {
            String fileNameFinal = fileName;
            if (count.get() == 0) {
                if (StringUtils.isEmpty(fileName)) {
                    // 解析文件名
                    fileNameFinal = Optional.ofNullable(response.getHeaders().getContentDisposition())
                            .map(ContentDisposition::getFilename)
                            .orElseGet(() -> {
                                String path = requestParam.getUrl().split("\\?")[0];
                                return path.substring(path.lastIndexOf('/') + 1);
                            });
                }
                if (StringUtils.isEmpty(fileNameFinal)) {
                    fileNameFinal = System.currentTimeMillis() + ".xlsx";
                }
                fileCache.put(fileKey, new LocalFileCacheDto(null, fileNameFinal, 1));
            } else {
                fileNameFinal = fileCache.get(fileKey).getFileName();
            }
            count.getAndIncrement();

            Path targetFile = parentFile.resolve(fileNameFinal);
            // 初始8KB，根据下载速度动态扩大（上限256KB）
            int bufferSize = 8 * 1024;
            long lastTime = System.currentTimeMillis();
            long totalRead = 0;
            try (InputStream is = response.getBody();
                 OutputStream os = Files.newOutputStream(targetFile, StandardOpenOption.CREATE)) {
                byte[] buffer = new byte[bufferSize];
                int bytesRead;
                while ((bytesRead = is.read(buffer)) != -1) {
                    os.write(buffer, 0, bytesRead);
                    // 每1MB数据评估一次
                    totalRead += bytesRead;
                    if (totalRead % (1024 * 1024) == 0) {
                        long timeSpent = System.currentTimeMillis() - lastTime;
                        double speedKBps = 1024 / (timeSpent / 1000.0);
                        // 动态调整缓冲区（范围8KB~256KB）
                        bufferSize = (int) Math.min(256 * 1024, Math.max(8 * 1024, speedKBps * 10));
                        buffer = new byte[bufferSize];
                        lastTime = System.currentTimeMillis();
                    }
                }
            }
            fileCache.put(fileKey, new LocalFileCacheDto(null, fileNameFinal, 2));
            return null;
        });
    }


    /**
     * 下载文件
     *
     * @param requestParam
     * @param restTemplateFinal
     * @param fileName          仅取后缀名
     * @return
     */
    public File downloadFile(RequestParamAllBodyTypeInner requestParam, RestTemplate restTemplateFinal, String fileName) {
        ResponseEntity<byte[]> response = exchangeInner(requestParam, restTemplateFinal, byte[].class);
        if (response.getStatusCode() == HttpStatus.OK) {
            byte[] fileData = response.getBody();
            if (fileData == null) {
                logger.error(requestParam.getUrl() + "请求返回文件为空");
                return null;
            }
            fileName = getFileNameFromResponse(fileName, response);
            File file = null;
            try {
                String fileExt = "";
                if (fileName.contains(".")) {
                    int i = fileName.lastIndexOf(".");
                    fileExt = fileName.substring(i);
                    fileName = fileName.substring(0, i);
                }
                if (fileName.length() < 3) {
                    fileName = fileName + "-" + System.currentTimeMillis();
                }
                file = File.createTempFile(fileName, fileExt);
                try (FileOutputStream outputStream = new FileOutputStream(file)) {
                    outputStream.write(fileData);
                }
            } catch (IOException e) {
                logger.error("文件操作异常:", e);
            }
            return file;
        } else {
            logger.error("请求http失败,返回：{}", JsonUtil.toJson(response));
        }
        return null;
    }

    public <T> String getFileNameFromResponse(String fileName, ResponseEntity<T> response) {
        List<String> resHeaders = response.getHeaders().get("Content-Disposition");
        if (StringUtils.isEmpty(fileName)) {
            fileName = System.currentTimeMillis() + ".xlsx";
            if (resHeaders != null) {
                for (String resHeader : resHeaders) {
                    if (resHeader.startsWith("filename") || resHeader.startsWith("attachment")) {
                        String fileNameTmp = resHeader.split("filename=")[1].replace("\"", "");
                        if (FileNameValidator.isValidFilename(fileNameTmp, 0)) {
                            fileName = URLDecoder.decode(fileNameTmp);
                        }
                    }
                }
            }
        }
        return fileName;
    }

    public <T> ResponseEntity<T> exchangeWithoutUriEncode(RequestParamAllBodyTypeInner requestParam, RestTemplate restTemplateFinal, Class<T> responseType) {
        if (Objects.isNull(requestParam.getHeader())) {
            requestParam.setHeader(new HashMap<>());
        }
        if (Objects.isNull(requestParam.getHttpMethod())) {
            requestParam.setHttpMethod("");
        }
        if (Objects.isNull(requestParam.getUrl())) {
            requestParam.setUrl("");
        }
        URI uri = URI.create(requestParam.getUrl());
        HttpMethod requestMethod = HttpMethod.resolve(requestParam.getHttpMethod().toUpperCase());
        HttpHeaders httpHeaders = new HttpHeaders();
        requestParam.getHeader().forEach((headerName, headerValue) -> httpHeaders.add((String) headerName, (String) headerValue));
        HttpEntity<T> httpEntity = new HttpEntity<>(DtoConvert.convertToGeneric(requestParam.getBody()), httpHeaders);
        if (requestParam.getBody() == null) {
            httpEntity = new HttpEntity<>(httpHeaders);
        }
        return restTemplateFinal.exchange(uri, Objects.requireNonNull(requestMethod), httpEntity, responseType);
    }
}
