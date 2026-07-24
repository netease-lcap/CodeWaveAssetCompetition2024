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

import java.io.File;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
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
}
