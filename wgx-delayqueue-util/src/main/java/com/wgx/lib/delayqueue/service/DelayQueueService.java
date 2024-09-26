package com.wgx.lib.delayqueue.service;

import com.netease.lowcode.core.annotation.NaslLogic;
import com.wgx.lib.delayqueue.modal.DelayedTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.TimeUnit;

@Service
public class DelayQueueService {

    //getLogger参数统一使用LCAP_EXTENSION_LOGGER
    private static final Logger logger = LoggerFactory.getLogger("LCAP_EXTENSION_LOGGER");

    @Autowired
    public DelayQueue<DelayedTask> delayQueue;

    /**
     * 添加元素到延迟队列
     * @param task 任务
     * @return 是否添加成功
     */
    @NaslLogic
    public Boolean  addTask(DelayedTask task) {
        // 检查延迟队列是否已初始化
        checkDelayQueueInitialized();
        // 检查任务是否为 null
        if (task == null) {
            logger.error("Attempted to add a null task to the delay queue");
            return false;
        }
        try {
            delayQueue.put(task);
            logger.info("Task added: {}", task);
            return true;
        } catch (Exception e) {
            Thread.currentThread().interrupt();
            logger.error("Error adding task to delay queue", e);
            return false;
        }
    }

    /**
     * 添加元素到延迟队列，添加成功则返回true，如果队列已满则返回false
     * @param task 任务
     * @return 是否添加成功
     */
    @NaslLogic
    public Boolean offer(DelayedTask task) {
        // 检查延迟队列是否已初始化
        checkDelayQueueInitialized();
        // 检查任务是否为 null
        if (task == null) {
            logger.error("Attempted to add a null task to the delay queue");
            return false;
        }
        boolean success = delayQueue.offer(task);
        if (success) {
            logger.info("Task offered: {}", task);
        } else {
            logger.info("Failed to offer task: {}", task);
        }
        return success;
    }

    /**
     * 获取队列最早过期的元素，并从队列中移除该元素，如果队列为空则阻塞等待（无限期）
     * @return 最早过期的元素
     */
    @NaslLogic
    public DelayedTask take(){
        // 检查延迟队列是否已初始化
        checkDelayQueueInitialized();
        try {
            DelayedTask task = delayQueue.take();
            logger.info("Task taken: {}", task);
            return task;
        } catch (InterruptedException e) {
            logger.error("Thread interrupted while waiting for the next task", e);
            Thread.currentThread().interrupt();
            return null;
        }
    }

    /**
     * 立即获取队列中最早过期的元素，如果队列为空或所有元素都未到期，则返回null（不阻塞）
     * @return 最早过期的元素，或者在队列为空或所有元素都未到期时返回null
     */
    @NaslLogic
    public DelayedTask pollNow() {
        // 检查延迟队列是否已初始化
        checkDelayQueueInitialized();
        DelayedTask task = delayQueue.poll();
        if (task != null) {
            logger.info("Task polled: {}", task);
        }
        return task;
    }

    /**
     * 尝试在指定的等待时间内获取队列头部元素。如果头部元素在指定时间内到期，则移除并返回该元素；如果超时或队列为空，则返回null。
     * @param timeout 等待时间，单位为毫秒
     * @return 获取到的元素，如果超时或队列为空则返回null
     */
    @NaslLogic
    public DelayedTask pollWithTimeout(Long timeout) {
        // 检查延迟队列是否已初始化
        checkDelayQueueInitialized();
        timeout = timeout == null ? 0 : timeout;
        try {
            // 尝试从延迟队列中获取一个元素
            DelayedTask task = delayQueue.poll(timeout, TimeUnit.MILLISECONDS);
            if (task != null) {
                logger.info("Task polled with timeout: {}", task);
            }
            return task;
        } catch (InterruptedException e) {
            // 处理 InterruptedException
            Thread.currentThread().interrupt(); // 恢复线程中断状态
            logger.error("Thread was interrupted while waiting for a task", e);
            return null; // 或者根据业务需求抛出自定义异常
        }
    }

    /**
     * 立即查看但不删除队列头部元素，如果头部元素未到期则返回null
     * @return 头部元素，如果头部元素未到期则返回null
     */
    @NaslLogic
    public DelayedTask peek() {
        // 检查延迟队列是否已初始化
        checkDelayQueueInitialized();
        DelayedTask task = delayQueue.peek();
        if (task != null) {
            logger.info("Task peeked: {}", task);
        }
        return task;
    }

    /**
     * 查询并返回延迟队列中的所有任务。
     * @return 任务列表。
     */
    @NaslLogic
    public List<DelayedTask> findAllTasks() {
        // 检查延迟队列是否已初始化
        checkDelayQueueInitialized();
        // 创建一个列表来存储所有任务
        List<DelayedTask> allTasks = new ArrayList<>();
        // 将延迟队列中的所有任务添加到列表中
        for (DelayedTask task : delayQueue) {
            allTasks.add(task);
        }
        // 返回包含所有任务的列表
        return Collections.unmodifiableList(allTasks);
    }

    /**
     * 根据任务标题模糊匹配延迟队列中的所有任务。
     * @param query 用于匹配任务标题的查询字符串。
     * @return 匹配查询字符串的任务列表。
     */
    @NaslLogic
    public List<DelayedTask> searchTasksByTaskName(String query) {
        // 检查延迟队列是否已初始化
        checkDelayQueueInitialized();
        if (query == null || query.trim().isEmpty()) {
            return Collections.emptyList();
        }
        List<DelayedTask> matchingTasks = new ArrayList<>();
        // 判断延迟队列是否为空
        if (delayQueue.isEmpty()) {
            return Collections.emptyList();
        }
        for (DelayedTask task : delayQueue) {
            if (task!= null && task.getTaskName()!= null && task.getTaskName().contains(query)) {
                matchingTasks.add(task);
            }
        }
        return Collections.unmodifiableList(matchingTasks);
    }

    /**
     * 根据任务ID查询延迟队列中的特定任务。
     * @param taskId 要查询的任务ID。
     * @return 匹配任务ID的任务，如果未找到则返回null。
     */
    @NaslLogic
    public DelayedTask getTaskByTaskId(String taskId) {
        // 检查延迟队列是否已初始化
        checkDelayQueueInitialized();
        // 确保 delayQueue 不为空
        if (delayQueue.isEmpty()) {
            return null;
        }
        // 确保 taskId 不为空
        if (taskId == null || taskId.trim().isEmpty()) {
            return null;
        }
        // 使用迭代器避免在遍历时修改集合导致的问题
        Iterator<DelayedTask> iterator = delayQueue.iterator();
        while (iterator.hasNext()) {
            DelayedTask task = iterator.next();
            if (task.getTaskId().equals(taskId)) {
                return task;
            }
        }
        return null;
    }

    /**
     * 清除延迟队列中的所有任务。
     * @return 清除操作成功返回true，否则返回false。
     */
    @NaslLogic
    public Boolean clearAllTasks() {
        // 检查延迟队列是否已初始化
        checkDelayQueueInitialized();
        try {
            delayQueue.clear(); // 清除延迟队列中的所有任务
            logger.info("All tasks have been cleared from the delay queue.");
            return true; // 返回 true 表示清除操作成功
        } catch (Exception e) {
            logger.error("Failed to clear all tasks from the delay queue.", e);
            return false; // 返回 false 表示清除操作失败
        }
    }

    /**
     * 从延迟队列中删除指定的任务。
     * @param o 要删除的任务对象。
     * @return 删除操作成功返回true，否则返回false。
     */
    @NaslLogic
    public Boolean remove(DelayedTask o) {
        // 检查延迟队列是否已初始化
        checkDelayQueueInitialized();
        // 确保要删除的任务对象不为空
        if (o == null) {
            logger.warn("Cannot remove null task from the delay queue.");
            return false;
        }
        boolean success = delayQueue.remove(o);
        if (success) {
            logger.info("Task removed: {}", o);
        }
        return success;
    }

    /**
     * 返回延迟队列中所有元素的数量，包括已过期和未过期的元素。
     * @return 队列中的元素数量。
     */
    @NaslLogic
    public Integer size() {
        // 检查延迟队列是否已初始化
        checkDelayQueueInitialized();
        return delayQueue.size();
    }

    //检查延迟队列是否已初始化
    private void checkDelayQueueInitialized() {
        if (delayQueue == null) {
            logger.error("Delay queue is not initialized");
            throw new IllegalStateException("Delay queue is not initialized");
        }
    }
}
