package com.netease.lowcode.custonapifilter.filter;

import com.netease.lowcode.custonapifilter.sign.CheckService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.CommonsRequestLoggingFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class SecurityFilter extends CommonsRequestLoggingFilter {
    public static final String LOGIC_IDENTIFIER_SEPARATOR = ":";
    private Logger logger = LoggerFactory.getLogger(SecurityFilter.class);
    @Autowired
    private CheckService checkService;
    @Autowired
    private FilterConfig filterConfig;

    private List<String> apiBlackList() {
        //后端依赖库逻辑
        List<String> defaultApis = Arrays.asList("/api/lcplogics");
        List<String> otherApis = new ArrayList<>();
        if (!StringUtils.isEmpty(filterConfig.filterUrlList)) {
            try {
                otherApis = Arrays.asList(filterConfig.filterUrlList.split(","));
            } catch (Exception e) {
                logger.warn("filterUrlList配置错误,{}", filterConfig.filterUrlList);
            }
        }
        return Stream.concat(defaultApis.stream(), otherApis.stream()).collect(Collectors.toList());
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String requestURI = request.getRequestURI();
        String method = request.getMethod();
        String logicIdentifier = requestURI + LOGIC_IDENTIFIER_SEPARATOR + method;
        //过滤黑名单
        boolean isFilter = false;
        for (String api : apiBlackList()) {
            if (logicIdentifier.startsWith(api)) {
                isFilter = true;
                break;
            }
        }
        if (!isFilter) {
            filterChain.doFilter(request, response);
            return;
        }
        logger.info("请求地址,{}", logicIdentifier);
        if (!checkService.check(request)) {
            response.setContentType("application/json");
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(checkService + "校验请求拦截");
            return;
        }
        logger.info("SecurityFilter check success");
        filterChain.doFilter(request, response);
    }
}
