package com.netease.lib.tasks.api;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.netease.lib.tasks.api.mode.Constant;
import com.netease.lib.tasks.api.mode.TaskStructure;
import com.netease.lib.tasks.api.util.CronUtil;
import com.netease.lib.tasks.scheduled.ScheduledTaskHolder;
import com.netease.lib.tasks.scheduled.ScheduledTaskRegistrar;
import com.netease.lib.tasks.scheduled.model.TaskModel;
import com.netease.lowcode.core.annotation.NaslLogic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

/**
 * @Author baojz
 * @Date: 2024/05/06
 * @description 待选择的逻辑注册器
 */
@Component
public class TaskManagerApi {

    private static final Logger logger = LoggerFactory.getLogger(TaskManagerApi.class);
    /**
     * 逻辑注册器
     */
    private final Map<String, Function<String, String>> logicRegister = new ConcurrentHashMap<>();
    /**
     * 数据库操作逻辑注册器
     */
    private final Map<String, Function<String, String>> dblogicRegister = new ConcurrentHashMap<>();

    @Autowired
    private ScheduledTaskRegistrar scheduledTaskRegistrar;


    /**
     * 初始化注册任务（持久化）
     *
     * @param logicFunctionList   查询定时任务列表逻辑
     * @param logicFunctionAdd    新增定时任务逻辑
     * @param logicFunctionDelete 根据taskId删除定时任务逻辑
     * @param logicFunctionPause  根据taskId暂停定时任务逻辑
     * @param logicFunctionStart  根据taskId启动定时任务逻辑
     * @return
     */
    @NaslLogic
    public Boolean registerInit(Function<String, String> logicFunctionList, Function<String, String> logicFunctionAdd,
                                Function<String, String> logicFunctionDelete, Function<String, String> logicFunctionPause,
                                Function<String, String> logicFunctionStart) {
        logger.info("初始化注册任务");
        //传入数据库表的列表查询、新增、根据taskId删除三个逻辑放入内存
        if (logicFunctionList == null || logicFunctionAdd == null || logicFunctionDelete == null) {
            logger.info("逻辑注册失败");
            return false;
        }
        dblogicRegister.put(Constant.DB_FUNCTION_LIST, logicFunctionList);
        dblogicRegister.put(Constant.DB_FUNCTION_ADD, logicFunctionAdd);
        dblogicRegister.put(Constant.DB_FUNCTION_DELETE, logicFunctionDelete);
        dblogicRegister.put(Constant.DB_FUNCTION_PAUSE, logicFunctionPause);
        dblogicRegister.put(Constant.DB_FUNCTION_START, logicFunctionStart);

        String listResult = logicFunctionList.apply(null);
        if (StringUtils.isEmpty(listResult)) {
            logger.info("db中无初始化任务");
            return true;
        }
        JSONArray listResultJson = JSONArray.parseArray(listResult);
        logger.info("db中存在{}个初始化任务", listResultJson.size());
        listResultJson.forEach(taskJsonStr -> {
            TaskModel task = JSONObject.parseObject(JSONObject.toJSONString(taskJsonStr), TaskModel.class);
            Function<String, String> logic = logicRegister.get(task.getLogicName());
            if (logic == null) {
                logger.info("逻辑{}不存在,task:{}", task.getLogicName(), JSONObject.toJSONString(task));
                return;
            }
            if ("1".equals(task.getIsPaused())) {
                logger.info("任务{}已暂停", task.getTaskId());
                return;
            }
            task.setFunction(logic);
            try {
                registerSingleTask(task);
            } catch (Exception e) {
                logger.error("init create task error:{}", e.getMessage());
                return;
            }
            logger.info("init create task:{}", JSONObject.toJSONString(task));
        });
        return true;
    }

    /**
     * 注册逻辑
     *
     * @param logicFunction 待配置定时的逻辑
     * @param logicName     逻辑名称
     * @return
     */
    @NaslLogic
    public Boolean addLogic(Function<String, String> logicFunction, String logicName) {
        if (StringUtils.isEmpty(logicName) || logicFunction == null) {
            logger.info("参数错误,logicName:{}，logicFunction:{}", logicName, logicFunction);
            return false;
        }
        if (logicRegister.containsKey(logicName)) {
            logger.info("{}逻辑已存在", logicName);
            return false;
        }
        logicRegister.put(logicName, logicFunction);
        logger.info("注册逻辑：{}", logicRegister.keySet());
        return true;
    }


    /**
     * 查询所有逻辑(右模糊匹配)
     *
     * @param logicName 逻辑名称
     * @return
     */
    @NaslLogic
    public List<String> listLogic(String logicName) {
        List<String> logics;
        Set<String> keys = logicRegister.keySet();
        if (!StringUtils.isEmpty(logicName)) {
            logics = new ArrayList<>();
            keys.forEach(key -> {
                if (key.startsWith(logicName)) {
                    logics.add(key);
                }
            });
        } else {
            logics = new ArrayList<>(keys);
        }
        return logics;
    }


    /**
     * 立即执行逻辑
     *
     * @param logicName 逻辑名称
     * @param request   逻辑请求参数
     * @return 逻辑执行结果
     */
    @NaslLogic
    public String runLogic(String logicName, String request) {
        if (StringUtils.isEmpty(logicName)) {
            logger.info("参数错误,logicName:{}", logicName);
            return null;
        }
        Function<String, String> holder = logicRegister.get(logicName);
        if (holder != null) {
            return holder.apply(request);
        }
        throw new IllegalArgumentException(logicName + "-逻辑不存在");
    }

    /**
     * 创建定时任务
     *
     * @param logicName 逻辑名称
     * @param cron      cron表达式/任务启动时间YYYYMMDDhhmmss
     * @param request   逻辑请求参数
     * @return
     */
    @NaslLogic
    public String createTask(String logicName, String cron, String request) {
        if (StringUtils.isEmpty(logicName) || StringUtils.isEmpty(cron) || StringUtils.isEmpty(request)) {
            logger.info("参数错误,logicName:{},cron:{},request:{}", logicName, cron, request);
            return null;
        }
        if (!CronUtil.checkCron(cron)) {
            logger.info("{}时间表达式格式错误", cron);
            try {
                SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
                format.parse(cron);
            } catch (ParseException e) {
                throw new IllegalArgumentException(cron + "-cron格式错误");
            }
        }
        Function<String, String> holder = logicRegister.get(logicName);
        if (holder == null) {
            throw new IllegalArgumentException(logicName + "-逻辑不存在");
        }
        TaskModel task = new TaskModel();
        task.setLogicName(logicName);
        task.setCron(cron);
        task.setRequest(request);
        task.setFunction(holder);
        task.setTaskId(UUID.randomUUID().toString());
        task.setIsPaused("0");
        Function<String, String> logicFunctionAdd = dblogicRegister.get(Constant.DB_FUNCTION_ADD);
        if (logicFunctionAdd != null) {
            logicFunctionAdd.apply(JSONObject.toJSONString(task));
        } else {
            logger.info("db新增任务逻辑不存在，无法持久化");
        }
        return task.getTaskId();
    }

    private Boolean registerSingleTask(TaskModel task) {
        if (scheduledTaskRegistrar.isExist(task)) {
            throw new IllegalArgumentException(task.getLogicName() +
                    "-" + task.getCron() + "-" + task.getRequest() + "-任务已存在");
        }
        scheduledTaskRegistrar.register(task);
        return true;
    }

    /**
     * 查询已创建的任务列表(右模糊匹配)
     *
     * @return
     */
    @NaslLogic
    public List<TaskStructure> listTask(String logicName) {
        List<TaskStructure> taskStructures = new ArrayList<>();
        Collection<ScheduledTaskHolder> scheduledTasks = scheduledTaskRegistrar.list();
        scheduledTasks.forEach(task -> {
            if (logicName != null) {
                if (!task.getLogicName().startsWith(logicName)) {
                    return;
                }
            }
            TaskStructure taskStructure = new TaskStructure();
            taskStructure.setLogicName(task.getLogicName());
            taskStructure.setCron(task.getCronExpression());
            taskStructure.setRequest(task.getRequest());
            taskStructure.setTaskId(task.getTaskId());
            taskStructure.setIsPaused(task.getIsPaused());
            taskStructures.add(taskStructure);
        });
        return taskStructures;
    }


    /**
     * 删除任务
     *
     * @param taskId 任务id
     */
    @NaslLogic
    public Boolean deleteTask(String taskId) {
        if (StringUtils.isEmpty(taskId)) {
            return false;
        }
        Function<String, String> logicFunctionDelete = dblogicRegister.get(Constant.DB_FUNCTION_DELETE);
        if (logicFunctionDelete != null) {
            logicFunctionDelete.apply(taskId);
        } else {
            logger.info("db删除任务逻辑不存在，无法持久化");
        }
        scheduledTaskRegistrar.pause(taskId);
        return true;
    }

    /**
     * 暂停任务（不删除）
     *
     * @param taskId 任务id
     */
    @NaslLogic
    public Boolean pauseTask(String taskId) {
        if (StringUtils.isEmpty(taskId)) {
            return false;
        }
        Function<String, String> logicFunctionPause = dblogicRegister.get(Constant.DB_FUNCTION_PAUSE);
        if (logicFunctionPause != null) {
            logicFunctionPause.apply(taskId);
        } else {
            logger.info("db暂停任务逻辑不存在，无法持久化");
        }
        scheduledTaskRegistrar.pause(taskId);
        return true;
    }

    /**
     * 启动任务
     *
     * @param taskId 任务id
     */
    @NaslLogic
    public Boolean startTask(String taskId) {
        if (StringUtils.isEmpty(taskId)) {
            return false;
        }
        Function<String, String> logicFunctionStart = dblogicRegister.get(Constant.DB_FUNCTION_START);
        if (logicFunctionStart != null) {
            String res = logicFunctionStart.apply(taskId);
            if (StringUtils.isEmpty(res)) {
                logger.info("db启动任务逻辑返回null，无法获取任务");
                throw new IllegalArgumentException(("db启动任务逻辑返回null，无法获取任务"));
            }
            TaskModel task;
            try {
                task = JSONObject.parseObject(res, TaskModel.class);
            } catch (Exception e) {
                logger.info("db启动任务逻辑返回非Task json，无法获取任务");
                throw new IllegalArgumentException(("db启动任务逻辑返回非Task json，无法获取任务"));
            }
            if (scheduledTaskRegistrar.isExist(task)) {
                throw new IllegalArgumentException(task.getLogicName() +
                        "-" + task.getCron() + "-" + task.getRequest() + "-任务已存在");
            }
            Function<String, String> logic = logicRegister.get(task.getLogicName());
            if (logic == null) {
                logger.info("逻辑{}不存在,task:{}", task.getLogicName(), JSONObject.toJSONString(task));
                throw new IllegalArgumentException(task.getLogicName() + "逻辑不存在,taskId:" + taskId);
            }
            task.setFunction(logic);
            registerSingleTask(task);
            return true;
        } else {
            logger.info("db启动任务逻辑不存在，无法获取任务");
            throw new IllegalArgumentException(("db启动任务逻辑不存在，无法获取任务"));
        }
    }
}