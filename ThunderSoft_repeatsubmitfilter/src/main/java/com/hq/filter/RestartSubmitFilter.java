package com.hq.filter;

import com.cache.CacheManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.servlet.*;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;


@Component
public class RestartSubmitFilter implements Filter {

    public static final String LOGIC_IDENTIFIER_SEPARATOR = ":";
    private Logger logger = LoggerFactory.getLogger(RestartSubmitFilter.class);


    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        String requestURI = request.getRequestURI();
        logger.warn("requestURI========" + requestURI);
        if (!StringUtils.isEmpty(requestURI) && requestURI.contains("/management") || requestURI.contains("/api/lcplogics/LoginUser")
                || requestURI.contains("/upload/")||requestURI.contains("/system/getUser")
                ||requestURI.contains("/nuims/nuims")||requestURI.contains("/system/config")||requestURI.contains("/m/login")||requestURI.contains("/m")
                ||requestURI.contains("api/lcplogics/")) {
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }
        requestURI = request.getRemoteHost() + request.getRequestURI();
        Cookie[] cookies = request.getCookies();
        String value = null;
        logger.warn("cookies========" + cookies.toString());
        for (Cookie cookie : cookies) {
            String name = cookie.getName();
            if ("authorization".equals(name)) {
                value = cookie.getValue();
                break;
            }
        }
        logger.warn("authorization========" + value);
        String method = request.getMethod();
        logger.warn("method========" + method);
        RequestWrapper requestWrapper = new RequestWrapper(request);
        String bodyString = requestWrapper.getBodyString();
        logger.warn("bodyString========" + bodyString);
        String logicIdentifier = requestURI + LOGIC_IDENTIFIER_SEPARATOR + method + LOGIC_IDENTIFIER_SEPARATOR + value + LOGIC_IDENTIFIER_SEPARATOR + bodyString;
        boolean bool = CacheManager.setSimpleFlag(logicIdentifier, 1);
        if (bool) {
            logger.info("filter in");
            filterChain.doFilter(requestWrapper, servletResponse);
            CacheManager.clearOnly(logicIdentifier);
            logger.info("filter out");
            return;
        }
        logger.warn("filter ===============Repeated submission intercepted");
    }

    //获取Request的body数据
    private String getBody(ServletRequest request) {
        StringBuilder stringBuilder = new StringBuilder();
        BufferedReader bufferedReader = null;
        InputStream inputStream = null;
        try {
            inputStream = request.getInputStream();
            if (inputStream != null) {
                bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                char[] charBuffer = new char[128];
                int bytesRead = -1;
                while ((bytesRead = bufferedReader.read(charBuffer)) > 0) {
                    stringBuilder.append(charBuffer, 0, bytesRead);
                }
            } else {
                stringBuilder.append("");
            }
        } catch (IOException ex) {

        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return stringBuilder.toString();
    }
}
