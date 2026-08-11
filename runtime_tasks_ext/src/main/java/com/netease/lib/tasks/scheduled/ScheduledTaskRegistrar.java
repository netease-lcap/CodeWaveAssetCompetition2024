package com.netease.lib.tasks.scheduled;

import com.alibaba.fastjson2.JSONObject;
import com.netease.lib.tasks.scheduled.model.TaskModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

/**
 * @Author baojz
 * @Date: 2024/05/06
 * @description 运行中任务注册器
 */
@Component
public class ScheduledTaskRegistrar {

    private static final Logger logger = LoggerFactory.getLogger(ScheduledTaskRegistrar.class);
    private final Map<String, ScheduledTaskHolder> register = new ConcurrentHashMap<>();
    /**
     * 任务调度器
     */
    private ThreadPoolTaskScheduler scheduler;

    public ScheduledTaskRegistrar(ThreadPoolTaskScheduler scheduler) {
        this.scheduler = scheduler;
    }

    public Boolean isExist(TaskModel taskModel) {
        Iterator<Map.Entry<String, ScheduledTaskHolder>> iterator = register.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, ScheduledTaskHolder> entry = iterator.next();
            ScheduledTaskHolder v = entry.getValue();
            if (v.getLogicName().equals(taskModel.getLogicName())
                    && v.getRequest().equals(taskModel.getRequest())
                    && v.getCronExpression().equals(taskModel.getCron())) {
                logger.info("Task-{} is exist", JSONObject.toJSONString(taskModel));
                return true;
            }
        }
        return false;
    }

    /**
     * 注册任务
     */
    public void register(TaskModel taskModel) {
        Runnable runnable = () -> {
            String result = taskModel.getFunction().apply(taskModel.getRequest());
            logger.info("Task-{} executed at:{},result:{} ",
                    JSONObject.toJSONString(taskModel), System.currentTimeMillis(), result);
        };
        ScheduledFuture<?> future;
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
            Date date = format.parse(taskModel.getCron());
            future = this.scheduler.schedule(runnable, date);
        } catch (Exception e) {
            future = this.scheduler.schedule(runnable, new CronTrigger(taskModel.getCron()));
        }
        ScheduledTaskHolder holder = new ScheduledTaskHolder();
        holder.setScheduledFuture(future);
        holder.setCronExpression(taskModel.getCron());
        holder.setRequest(taskModel.getRequest());
        holder.setLogicName(taskModel.getLogicName());
        holder.setTaskId(taskModel.getTaskId());
        holder.setIsPaused(taskModel.getIsPaused());
        register.put(holder.getTaskId(), holder);
        logger.info("Task-{} register complete", JSONObject.toJSONString(taskModel));
    }

    /**
     * 查询所有任务
     *
     * @return
     */
    public Collection<ScheduledTaskHolder> list() {
        return register.values();
    }

    /**
     * 暂停任务
     *
     * @param taskId
     */
    public void pause(String taskId) {
        ScheduledTaskHolder holder = register.get(taskId);
        if (holder != null) {
            if (holder.terminate()) {
                return;
            }
            ScheduledFuture<?> future = holder.getScheduledFuture();
            future.cancel(true);
        }
        register.remove(taskId);
    }
}