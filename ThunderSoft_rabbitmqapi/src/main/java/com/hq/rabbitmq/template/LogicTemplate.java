package com.hq.rabbitmq.template;

import com.alibaba.fastjson2.JSON;
import com.hq.rabbitmq.model.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.function.Supplier;

public class LogicTemplate {

    private static final Logger log = LoggerFactory.getLogger(LogicTemplate.class);

    public static Response execute(Supplier<Response> supplier, String logic, Object... arg) {

        try {
            log.info(String.format("[%s]逻辑开始执行,参数信息:%s", logic, Arrays.toString(arg)));
            Response response = supplier.get();
            log.info(String.format("[%s]逻辑执行成功,结果:%s", logic, JSON.toJSONString(response)));
            return response;
        } catch (Throwable e) {
            log.error(String.format("[%s]逻辑执行失败！", logic), e);
            return Response.FAIL("逻辑执行失败！" + e.getMessage(), Arrays.toString(e.getStackTrace()));
        }
    }
}
