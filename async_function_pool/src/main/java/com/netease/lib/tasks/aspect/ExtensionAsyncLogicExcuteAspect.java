package com.netease.lib.tasks.aspect;

import com.netease.lib.tasks.annotation.AsyncLogicAnnotation;
import com.netease.lib.tasks.api.FunctionManagerApi;
import com.netease.lib.tasks.util.AsyncLogicAnnotationConfigUtil;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Aspect
@Component
public class ExtensionAsyncLogicExcuteAspect {
    private final static Logger log = LoggerFactory.getLogger("LCAP_EXTENSION_LOGGER");
    @Resource
    private FunctionManagerApi functionManagerApi;

    @Resource(name = "libraryCommonTaskExecutor")
    private Executor contextAwareExecutor;

    @Around("execution(* *.*.*.service.*LogicService.*(..))")
    public Object aroundCustomizeServiceMethods(ProceedingJoinPoint joinPoint) throws Throwable {
        List<String> allUseAsyncAnnoLogicNames = AsyncLogicAnnotationConfigUtil
                .listAlluseAnnoLogicNames(AsyncLogicAnnotation.class.getSimpleName());
        if (!allUseAsyncAnnoLogicNames.contains(joinPoint.getSignature().getName())) {
            return joinPoint.proceed();
        }
        CompletableFuture<Object> future = CompletableFuture.supplyAsync(() -> {
            try {
                return joinPoint.proceed();
            } catch (Throwable e) {
                log.error("ExtensionAsyncLogicExcuteAspect error", e);
                return null;
            }
        }, contextAwareExecutor);
        String taskId = UUID.randomUUID().toString();
        functionManagerApi.putRunningTask(taskId, future);
        return taskId;
    }
}


