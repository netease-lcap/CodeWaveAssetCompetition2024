package com.netease.http.httpclient;

import com.netease.http.dto.*;
import com.netease.http.exception.TransferCommonException;
import com.netease.http.util.FileUtil;
import com.netease.http.util.JsonUtil;
import com.netease.http.util.SSLUtil;
import com.netease.lowcode.core.annotation.NaslLogic;
import com.netease.lowcode.core.annotation.Required;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * desc
 *
 * @author jingzhix
 * @date 2023/7/5
 * @since
 */
@Component
@EnableRetry
public class LCAPHttpClient {
    private static final Logger logger = LoggerFactory.getLogger("LCAP_EXTENSION_LOGGER");

    @Autowired
    private HttpClientService httpClientService;
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private FileUtil httpClientFileUtils;

    /**
     * http/https调用（非form使用）
     *
     * @param url
     * @param httpMethod
     * @param header
     * @param body
     * @return
     * @throws URISyntaxException
     */
    @NaslLogic
    @Deprecated
    @Retryable(value = {Exception.class}, maxAttempts = 3, backoff = @Backoff(delay = 1000L))
    public String exchange(@Required String url, @Required String httpMethod, @Required Map<String, String> header, @Required Map<String, String> body) throws TransferCommonException {
        try {
            RequestParamAllBodyTypeInner requestParam = new RequestParamAllBodyTypeInner();
            requestParam.setBody(body);
            //填充requestParam参数
            requestParam.setUrl(url);
            requestParam.setHttpMethod(httpMethod);
            requestParam.setHeader(header);
            ResponseEntity<String> exchange = httpClientService.exchangeInner(requestParam, restTemplate, String.class);
            if (exchange.getStatusCode() == HttpStatus.OK) {
                return exchange.getBody();
            } else {
                throw new TransferCommonException(exchange.getStatusCodeValue(), JsonUtil.toJson(exchange));
            }
        } catch (HttpClientErrorException e) {
            logger.error("", e);
            throw new TransferCommonException(e.getStatusCode().value(), e.getResponseBodyAsString());
        } catch (Exception e) {
            logger.error("", e);
            throw new TransferCommonException(e.getMessage(), e);
        }
    }


    /**
     * http/https调用（非form使用，异常时返回http错误码）
     *
     * @param url
     * @param httpMethod
     * @param header
     * @param body
     * @return
     * @throws URISyntaxException
     */
    @NaslLogic
    @Retryable(value = {Exception.class}, maxAttempts = 3, backoff = @Backoff(delay = 1000L))
    public String exchangeV2(@Required String url, @Required String httpMethod, @Required Map<String, String> header, @Required Map<String, String> body) throws TransferCommonException {
        try {
            RequestParamAllBodyTypeInner requestParam = new RequestParamAllBodyTypeInner();
            requestParam.setBody(body);
            //填充requestParam参数
            requestParam.setUrl(url);
            requestParam.setHttpMethod(httpMethod);
            requestParam.setHeader(header);
            ResponseEntity<String> exchange = httpClientService.exchangeInner(requestParam, restTemplate, String.class);
            return exchange.getBody();
        } catch (HttpClientErrorException e) {
            logger.error("", e);
            return e.getResponseBodyAsString();
        } catch (Exception e) {
            logger.error("", e);
            throw new TransferCommonException(e.getMessage(), e);
        }
    }

    /**
     * http/https调用（非form使用，异常时返回http错误码）
     *
     * @param url
     * @param httpMethod
     * @param header
     * @param body
     * @return
     * @throws URISyntaxException
     */
    @NaslLogic
    @Retryable(value = {Exception.class}, maxAttempts = 3, backoff = @Backoff(delay = 1000L))
    public String exchangeV3(@Required String url, @Required String httpMethod, @Required Map<String, String> header, @Required String body) throws TransferCommonException {
        try {
            RequestParamAllBodyTypeInner requestParam = new RequestParamAllBodyTypeInner();
            requestParam.setBody(body);
            //填充requestParam参数
            requestParam.setUrl(url);
            requestParam.setHttpMethod(httpMethod);
            requestParam.setHeader(header);
            ResponseEntity<String> exchange = httpClientService.exchangeInner(requestParam, restTemplate, String.class);
            return exchange.getBody();
        } catch (HttpClientErrorException e) {
            logger.error("", e);
            return e.getResponseBodyAsString();
        } catch (Exception e) {
            logger.error("", e);
            throw new TransferCommonException(e.getMessage(), e);
        }
    }


    /**
     * http/https调用（非form使用，异常时返回http错误码）
     *
     * @param url
     * @param httpMethod
     * @param header
     * @param body
     * @return
     * @throws URISyntaxException
     */
    @NaslLogic
    @Retryable(value = {Exception.class}, maxAttempts = 3, backoff = @Backoff(delay = 1000L))
    public ExchangeResponseDto exchangeV4(@Required String url, @Required String httpMethod, @Required Map<String, String> header, @Required String body) throws TransferCommonException {
        try {
            RequestParamAllBodyTypeInner requestParam = new RequestParamAllBodyTypeInner();
            requestParam.setBody(body);
            //填充requestParam参数
            requestParam.setUrl(url);
            requestParam.setHttpMethod(httpMethod);
            requestParam.setHeader(header);
            ResponseEntity<String> exchange = httpClientService.exchangeInner(requestParam, restTemplate, String.class);
            return convertToExchangeResponseDto(exchange);
        } catch (HttpClientErrorException e) {
            logger.error("", e);
            throw new TransferCommonException(e.getStatusCode().value(), e.getResponseBodyAsString());
        } catch (Exception e) {
            logger.error("", e);
            throw new TransferCommonException(e.getMessage(), e);
        }
    }

    /**
     * http/https调用（非form使用，异常时返回http错误码）
     *
     * @param url
     * @param httpMethod
     * @param header
     * @param body
     * @return
     * @throws URISyntaxException
     */
    @NaslLogic
    @Retryable(value = {Exception.class}, maxAttempts = 3, backoff = @Backoff(delay = 1000L))
    public ExchangeResponseDto exchangeCrtV4(@Required String url, @Required String httpMethod, @Required Map<String, String> header,
                                             @Required String body, @Required Boolean isIgnoreCrt) throws TransferCommonException {
        try {
            if (isIgnoreCrt == null) {
                isIgnoreCrt = false;
            }
            if (isIgnoreCrt) {
                SSLUtil.turnOffCertificateValidation();
            }
            RequestParamAllBodyTypeInner requestParam = new RequestParamAllBodyTypeInner();
            requestParam.setBody(body);
            //填充requestParam参数
            requestParam.setUrl(url);
            requestParam.setHttpMethod(httpMethod);
            requestParam.setHeader(header);
            ResponseEntity<String> exchange = httpClientService.exchangeInner(requestParam, restTemplate, String.class);
            return convertToExchangeResponseDto(exchange);
        } catch (HttpClientErrorException e) {
            logger.error("", e);
            throw new TransferCommonException(e.getStatusCode().value(), e.getResponseBodyAsString());
        } catch (Exception e) {
            logger.error("", e);
            throw new TransferCommonException(e.getMessage(), e);
        }
    }

    /**
     * 下载文件并上传到nos（默认fileUrl是get请求，默认格式xlsx）
     *
     * @param fileUrl
     * @param fileName 文件名，可空，用于fileUrl无法获取文件名时，指定文件后缀
     * @param header
     * @return
     */
    @NaslLogic
    public String downloadFileUploadNos(String fileUrl, String fileName, Map<String, String> header) throws TransferCommonException {
        File file = null;
        try {
            RequestParamAllBodyTypeInner requestParam = new RequestParamAllBodyTypeInner();
            requestParam.setUrl(fileUrl);
            requestParam.setHeader(header);
            requestParam.setHttpMethod(HttpMethod.GET.name());
            file = httpClientService.downloadFile(requestParam, restTemplate, fileName);
            if (file == null) {
                return null;
            }
            UploadResponseDTO uploadResponseDTO = httpClientFileUtils.uploadStream(Files.newInputStream(file.toPath()), file.getName());
            return uploadResponseDTO.getResult();
        } catch (HttpClientErrorException e) {
            logger.error("", e);
            throw new TransferCommonException(e.getStatusCode().value(), e.getResponseBodyAsString());
        } catch (Exception e) {
            logger.error("", e);
            throw new TransferCommonException(e.getMessage(), e);
        } finally {
            if (file != null && file.exists()) {
                file.delete();
            }
        }
    }

    /**
     * 下载文件并上传到nos（默认fileUrl是get请求，默认格式xlsx）
     *
     * @param fileUrl
     * @param fileName 文件名，可空，用于fileUrl无法获取文件名时，指定文件后缀
     * @param header
     * @return
     */
    @NaslLogic
    public String downloadFileUploadNosExtendHttpMethod(@Required String fileUrl, String fileName, @Required Map<String, String> header,
                                                        @Required String httpMethod, @Required String body) throws TransferCommonException {
        File file = null;
        try {
            RequestParamAllBodyTypeInner requestParam = new RequestParamAllBodyTypeInner();
            requestParam.setUrl(fileUrl);
            requestParam.setHeader(header);
            if (StringUtils.isEmpty(httpMethod)) {
                httpMethod = HttpMethod.GET.name();
            }
            requestParam.setHttpMethod(httpMethod);
            requestParam.setBody(body);
            file = httpClientService.downloadFile(requestParam, restTemplate, fileName);
            if (file == null) {
                return null;
            }
            UploadResponseDTO uploadResponseDTO = httpClientFileUtils.uploadStream(httpClientFileUtils.repeatReadInputStream(file), file.getName());
            return uploadResponseDTO.getResult();
        } catch (HttpClientErrorException e) {
            logger.error("", e);
            throw new TransferCommonException(e.getStatusCode().value(), e.getResponseBodyAsString());
        } catch (Exception e) {
            logger.error("", e);
            throw new TransferCommonException(e.getMessage(), e);
        } finally {
            if (file != null && file.exists()) {
                file.delete();
            }
        }
    }

    /**
     * nos url文件上传到第三方（默认fileUrl是get请求，仅支持xlsx文件）
     *
     * @param fileUrl      文件uri(不带域名）
     * @param requestUrl   当前请求的url
     * @param requestParam 请求信息
     * @return
     */
    @NaslLogic
    public String uploadNosExchange(String fileUrl, String requestUrl, RequestParam requestParam) throws TransferCommonException {
        if (requestParam.getIsIgnoreCrt() == null) {
            requestParam.setIsIgnoreCrt(false);
        }
        if (requestParam.getIsIgnoreCrt()) {
            SSLUtil.turnOffCertificateValidation();
        }
        URL url = null;
        try {
            url = new URL(requestUrl);
        } catch (MalformedURLException e) {
            logger.error("requestUrl必须是一个url", e);
        }
        String protocol = url.getProtocol();
        int port = url.getPort();
        if (port == -1) {
            if ("http".equalsIgnoreCase(protocol)) {
                port = 80;
            } else if ("https".equalsIgnoreCase(protocol)) {
                port = 443;
            }
        }
        fileUrl = protocol + "://" + url.getHost() + ":" + port + fileUrl;
        return uploadNosExchangeCommonFileType(fileUrl, null, requestParam);
    }

    /**
     * nos url文件上传到第三方（支持指定fileName）
     *
     * @param fileUrl      文件url(带域名）
     * @param fileName     文件名
     * @param requestParam 请求信息
     * @return
     */
    @NaslLogic
    public String uploadNosExchangeCommonFileType(String fileUrl, String fileName, RequestParam requestParam) throws TransferCommonException {
        UploadFileParam uploadFileParam = new UploadFileParam();
        uploadFileParam.setFileUrl(fileUrl);
        uploadFileParam.setFileKey("file");
        return uploadNosExchangeCommon(uploadFileParam, fileName, requestParam);
    }

    /**
     * nos url文件上传到第三方（支持指定fileName和form请求时的file key）
     *
     * @param uploadFileParam 文件key,文件url(带域名）
     * @param fileName        文件名
     * @param requestParam    请求信息
     * @return
     */
    @NaslLogic
    public String uploadNosExchangeCommon(UploadFileParam uploadFileParam, String fileName, RequestParam requestParam) throws TransferCommonException {
        File file = null;
        try {
            if (requestParam.getIsIgnoreCrt() == null) {
                requestParam.setIsIgnoreCrt(false);
            }
            if (requestParam.getIsIgnoreCrt()) {
                SSLUtil.turnOffCertificateValidation();
            }
            RequestParamAllBodyTypeInner requestParamGetFile = new RequestParamAllBodyTypeInner();
            requestParamGetFile.setUrl(uploadFileParam.getFileUrl());
            //文件下载一般是get，默认get
            requestParamGetFile.setHttpMethod(HttpMethod.GET.name());
            file = httpClientService.downloadFile(requestParamGetFile, restTemplate, fileName);
            if (file == null) {
                return null;
            }
            return httpClientService.uploadFileExchangeCommon(restTemplate, requestParam, uploadFileParam.getFileKey(), file);
        } catch (HttpClientErrorException e) {
            logger.error("", e);
            throw new TransferCommonException(e.getStatusCode().value(), e.getResponseBodyAsString());
        } catch (Exception e) {
            logger.error("", e);
            throw new TransferCommonException(e.getMessage(), e);
        } finally {
            if (file != null && file.exists()) {
                file.delete();
            }
        }
    }

    /**
     * 证书校验https请求（非form使用）
     *
     * @param requestParam
     * @return
     */
    @NaslLogic
    @Retryable(value = {Exception.class}, maxAttempts = 3, backoff = @Backoff(delay = 1000L))
    public String exchangeCrt(RequestParam requestParam) throws TransferCommonException {
        try {
            if (requestParam.getIsIgnoreCrt() == null) {
                requestParam.setIsIgnoreCrt(false);
            }
            if (requestParam.getIsIgnoreCrt()) {
                SSLUtil.turnOffCertificateValidation();
            }
            ResponseEntity<String> exchange = httpClientService
                    .exchangeInner(DtoConvert.convertToRequestParamAllBodyTypeInner(requestParam), restTemplate, String.class);
            if (exchange.getStatusCode() == HttpStatus.OK) {
                return exchange.getBody();
            } else {
                throw new TransferCommonException(exchange.getStatusCodeValue(), JsonUtil.toJson(exchange));
            }
        } catch (HttpClientErrorException e) {
            logger.error("", e);
            throw new TransferCommonException(e.getStatusCode().value(), e.getResponseBodyAsString());
        } catch (Exception e) {
            logger.error("", e);
            throw new TransferCommonException(e.getMessage(), e);
        }
    }


    /**
     * https请求，body支持String类型
     *
     * @param requestParam
     * @return
     */
    @NaslLogic
    @Retryable(value = {Exception.class}, maxAttempts = 3, backoff = @Backoff(delay = 1000L))
    public String exchangeAllBodyType(RequestParamAllBodyType requestParam) throws TransferCommonException {
        try {
            if (requestParam.getIsIgnoreCrt() == null) {
                requestParam.setIsIgnoreCrt(false);
            }
            if (requestParam.getIsIgnoreCrt()) {
                SSLUtil.turnOffCertificateValidation();
            }

            ResponseEntity<String> exchange = httpClientService
                    .exchangeInner(DtoConvert.convertToRequestParamAllBodyTypeInner(requestParam), restTemplate, String.class);
            if (exchange.getStatusCode() == HttpStatus.OK) {
                return exchange.getBody();
            } else {
                throw new TransferCommonException(exchange.getStatusCodeValue(), JsonUtil.toJson(exchange));
            }
        } catch (HttpClientErrorException e) {
            logger.error("", e);
            throw new TransferCommonException(e.getStatusCode().value(), e.getResponseBodyAsString());
        } catch (Exception e) {
            logger.error("", e);
            throw new TransferCommonException(e.getMessage(), e);
        }
    }

    /**
     * https请求忽略证书，form表单专用body为MultiValueMap
     *
     * @param requestParam
     * @return
     */
    @NaslLogic
    @Retryable(value = {Exception.class}, maxAttempts = 3, backoff = @Backoff(delay = 1000L))
    public String exchangeCrtForm(RequestParam requestParam) throws TransferCommonException {
        try {
            if (requestParam.getIsIgnoreCrt() == null) {
                requestParam.setIsIgnoreCrt(false);
            }
            if (requestParam.getIsIgnoreCrt()) {
                SSLUtil.turnOffCertificateValidation();
            }
            RequestParamAllBodyTypeInner requestParamAllBodyTypeInner = DtoConvert.convertToRequestParamAllBodyTypeInner(requestParam);
            if (requestParam.getBody() != null) {
                MultiValueMap multiValueMap = new LinkedMultiValueMap();
                //map 转MultiValueMap
                requestParam.getBody().forEach(multiValueMap::add);
                requestParamAllBodyTypeInner.setBody(multiValueMap);
            }
            ResponseEntity<String> exchange = httpClientService
                    .exchangeInner(requestParamAllBodyTypeInner, restTemplate, String.class);
            if (exchange.getStatusCode() == HttpStatus.OK) {
                return exchange.getBody();
            } else {
                throw new TransferCommonException(exchange.getStatusCode().value(), JsonUtil.toJson(exchange));
            }
        } catch (HttpClientErrorException e) {
            logger.error("", e);
            throw new TransferCommonException(e.getStatusCode().value(), e.getResponseBodyAsString());
        } catch (Exception e) {
            logger.error("", e);
            throw new TransferCommonException(e.getMessage(), e);
        }
    }

    /**
     * https请求忽略证书，form表单专用body为MultiValueMap。包含返回头信息
     *
     * @param requestParam
     * @return
     */
    @NaslLogic
    @Retryable(value = {Exception.class}, maxAttempts = 3, backoff = @Backoff(delay = 1000L))
    public ExchangeResponseDto exchangeCrtFormResHeader(RequestParam requestParam) throws TransferCommonException {
        try {
            if (requestParam.getIsIgnoreCrt() == null) {
                requestParam.setIsIgnoreCrt(false);
            }
            if (requestParam.getIsIgnoreCrt()) {
                SSLUtil.turnOffCertificateValidation();
            }
            RequestParamAllBodyTypeInner requestParamAllBodyTypeInner = DtoConvert.convertToRequestParamAllBodyTypeInner(requestParam);
            if (requestParam.getBody() != null) {
                MultiValueMap multiValueMap = new LinkedMultiValueMap();
                //map 转MultiValueMap
                requestParam.getBody().forEach(multiValueMap::add);
                requestParamAllBodyTypeInner.setBody(multiValueMap);
            }
            ResponseEntity<String> exchange = httpClientService
                    .exchangeInner(requestParamAllBodyTypeInner, restTemplate, String.class);
            return convertToExchangeResponseDto(exchange);
        } catch (HttpClientErrorException e) {
            logger.error("", e);
            throw new TransferCommonException(e.getStatusCode().value(), e.getResponseBodyAsString());
        } catch (Exception e) {
            logger.error("", e);
            throw new TransferCommonException(e.getMessage(), e);
        }
    }

    private ExchangeResponseDto convertToExchangeResponseDto(ResponseEntity<String> exchange) {
        if (exchange.getStatusCode() == HttpStatus.OK) {
            ExchangeResponseDto exchangeResponseDto = new ExchangeResponseDto();
            Map<String, String> headerMap = exchange.getHeaders()
                    .entrySet()
                    .stream()
                    .collect(Collectors.toMap(
                            Map.Entry::getKey,
                            entry -> String.join(", ", entry.getValue())
                    ));

            exchangeResponseDto.setResponseHeaders(headerMap);
            exchangeResponseDto.setBodyString(exchange.getBody());
            return exchangeResponseDto;
        } else {
            throw new TransferCommonException(exchange.getStatusCode().value(), JsonUtil.toJson(exchange));
        }
    }

    /**
     * http/https调用（非form使用，url不编码）
     *
     * @param url
     * @param httpMethod
     * @param header
     * @param body
     * @return
     * @throws URISyntaxException
     */
    @NaslLogic
    @Retryable(value = {Exception.class}, maxAttempts = 3, backoff = @Backoff(delay = 1000L))
    public String exchangeWithoutUriEncode(@Required String url, @Required String httpMethod, @Required Map<String, String> header, @Required Map<String, String> body) throws TransferCommonException {
        try {
            RequestParamAllBodyTypeInner requestParam = new RequestParamAllBodyTypeInner();
            requestParam.setBody(body);
            //填充requestParam参数
            requestParam.setUrl(url);
            requestParam.setHttpMethod(httpMethod);
            requestParam.setHeader(header);
            ResponseEntity<String> exchange = httpClientService.exchangeWithoutUriEncode(requestParam, restTemplate, String.class);
            if (exchange.getStatusCode() == HttpStatus.OK) {
                return exchange.getBody();
            } else {
                throw new TransferCommonException(exchange.getStatusCodeValue(), JsonUtil.toJson(exchange));
            }
        } catch (HttpClientErrorException e) {
            logger.error("", e);
            throw new TransferCommonException(e.getStatusCode().value(), e.getResponseBodyAsString());
        } catch (Exception e) {
            logger.error("", e);
            throw new TransferCommonException(e.getMessage(), e);
        }
    }


    /**
     * 文件下载（下载后上传到当前应用文件存储nos，返回上传后可直接访问的url）
     *
     * @param url      文件下载地址
     * @param fileName 文件名，若url中未携带文件后缀，则使用该文件名的后缀
     * @return
     */
    @NaslLogic
    public String downloadFile(@Required String url, @Required String fileName) throws TransferCommonException {
        File file = null;
        try {
            String finalFileName = fileName;
            String urlFileExt = getFileExtFromUrl(url);
            if (!StringUtils.isEmpty(urlFileExt)) {
                //url携带文件后缀，优先使用url的后缀
                String fileNameBase = fileName.contains(".") ? fileName.substring(0, fileName.lastIndexOf(".")) : fileName;
                finalFileName = fileNameBase + urlFileExt;
            }
            RequestParamAllBodyTypeInner requestParam = new RequestParamAllBodyTypeInner();
            requestParam.setUrl(url);
            requestParam.setHttpMethod(HttpMethod.GET.name());
            file = httpClientService.downloadFile(requestParam, restTemplate, finalFileName);
            if (file == null) {
                return null;
            }
            UploadResponseDTO uploadResponseDTO = httpClientFileUtils.uploadStream(Files.newInputStream(file.toPath()), file.getName());
            return uploadResponseDTO.getResult();
        } catch (HttpClientErrorException e) {
            logger.error("", e);
            throw new TransferCommonException(e.getStatusCode().value(), e.getResponseBodyAsString());
        } catch (Exception e) {
            logger.error("", e);
            throw new TransferCommonException(e.getMessage(), e);
        } finally {
            if (file != null && file.exists()) {
                file.delete();
            }
        }
    }

    /**
     * 从url的path部分提取文件后缀（含"."），若无后缀返回空字符串
     */
    private String getFileExtFromUrl(String url) {
        try {
            String path = new URL(url).getPath();
            String lastSegment = path.contains("/") ? path.substring(path.lastIndexOf("/") + 1) : path;
            if (lastSegment.contains(".")) {
                return lastSegment.substring(lastSegment.lastIndexOf("."));
            }
        } catch (MalformedURLException e) {
            logger.error("url解析异常", e);
        }
        return "";
    }

    /**
     * 文件下载（下载后不上传，直接将文件流写入响应体，前端需通过页面导航方式访问该接口才能触发浏览器下载）
     *
     * @param url      文件下载地址
     * @param fileName 文件名，可空，优先使用该文件名（含其自身后缀）；为空时才从url中解析文件名
     * @return
     */
    @NaslLogic
    public String downloadFileToResponse(@Required String url, String fileName) throws TransferCommonException {
        try {
            String finalFileName = !StringUtils.isEmpty(fileName) ? fileName : getFileNameFromUrl(url);
            RequestParamAllBodyTypeInner requestParam = new RequestParamAllBodyTypeInner();
            requestParam.setUrl(url);
            requestParam.setHttpMethod(HttpMethod.GET.name());
            ResponseEntity<byte[]> exchange = httpClientService.exchangeInner(requestParam, restTemplate, byte[].class);
            if (exchange.getStatusCode() != HttpStatus.OK || exchange.getBody() == null) {
                throw new TransferCommonException(exchange.getStatusCodeValue(), "文件下载失败");
            }
            ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (requestAttributes == null) {
                throw new TransferCommonException(500, "无法获取当前请求上下文，无法写入响应流");
            }
            HttpServletResponse response = requestAttributes.getResponse();
            response.setContentType("application/octet-stream");
            response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(finalFileName, StandardCharsets.UTF_8.name()));
            try (OutputStream outputStream = response.getOutputStream()) {
                outputStream.write(exchange.getBody());
                outputStream.flush();
            }
            return finalFileName;
        } catch (HttpClientErrorException e) {
            logger.error("", e);
            throw new TransferCommonException(e.getStatusCode().value(), e.getResponseBodyAsString());
        } catch (Exception e) {
            logger.error("", e);
            throw new TransferCommonException(e.getMessage(), e);
        }
    }

    /**
     * 从url的path部分提取文件名（最后一段路径），解析失败或为空时使用时间戳作为文件名
     */
    private String getFileNameFromUrl(String url) {
        try {
            String path = new URL(url).getPath();
            String lastSegment = path.contains("/") ? path.substring(path.lastIndexOf("/") + 1) : path;
            if (!StringUtils.isEmpty(lastSegment)) {
                return lastSegment;
            }
        } catch (MalformedURLException e) {
            logger.error("url解析异常", e);
        }
        return String.valueOf(System.currentTimeMillis());
    }

}
