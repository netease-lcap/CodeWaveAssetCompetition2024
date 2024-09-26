package com.wgx.lib.delayqueue.modal;

import com.netease.lowcode.core.annotation.NaslStructure;

import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

@NaslStructure
public class DelayedTask implements Delayed {

    // 延迟时间
    public Long delay = 0L;

    // 任务开始执行时间
    public Long startTime = System.currentTimeMillis();

    // 任务ID
    public String taskId;

    // 任务状态
    public String status;

    // 任务参数
    public String parameters;

    // 任务结果
    public String result;

    // 任务名称
    public String taskName;

    // 无参构造
    public DelayedTask() {
    }

    // 构造函数
    public DelayedTask(String taskId, long delay, long startTime, String status, String parameters, String result, String taskName) {
        this.taskId = taskId;
        this.delay = delay;
        this.startTime = startTime;
        this.status = status;
        this.parameters = parameters;
        this.result = result;
        this.taskName = taskName;
    }

    @Override
    public long getDelay(TimeUnit unit) {
        long now = System.currentTimeMillis();
        long delayInMillis = startTime - now;
        if (delayInMillis < 0) {
            delayInMillis = 0;
        }
        // 计算当前时间与起始时间startTime的时间差，并根据给定的时间单位unit进行转换后返回。
        return unit.convert(delayInMillis, TimeUnit.MILLISECONDS);
    }

    @Override
    public int compareTo(Delayed o) {
        long diff = getDelay(TimeUnit.MILLISECONDS) - o.getDelay(TimeUnit.MILLISECONDS);
        // 比较当前对象与另一个Delayed对象的时间差，若当前对象延迟更短则返回负数，相等返回0，更长返回正数。如果时间小的，就会优先被队列提取出来
        return diff == 0 ? 0 : (diff < 0 ? -1 : 1);
    }

    public Long getDelay() {
        return delay;
    }

    public void setDelay(Long delay) {
        this.delay = delay;
    }

    public Long getStartTime() {
        return startTime;
    }

    public void setStartTime(Long startTime) {
        this.startTime = startTime;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getParameters() {
        return parameters;
    }

    public void setParameters(String parameters) {
        this.parameters = parameters;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    @Override
    public String toString() {
        return "DelayedTask{" +
                "delay=" + delay +
                ", startTime=" + startTime +
                ", taskId='" + taskId + '\'' +
                ", status='" + status + '\'' +
                ", parameters='" + parameters + '\'' +
                ", result='" + result + '\'' +
                ", taskName='" + taskName + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof DelayedTask)) {
            return false;
        }
        DelayedTask other = (DelayedTask) obj;
        return taskId.equals(other.taskId);
    }

    @Override
    public int hashCode() {
        return taskId.hashCode();
    }
}
