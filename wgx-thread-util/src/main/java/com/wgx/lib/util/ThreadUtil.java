package com.wgx.lib.util;

import com.netease.lowcode.core.annotation.NaslLogic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ThreadUtil {

   //参数使用LCAP_EXTENSION_LOGGER后日志会显示在平台日志功能中
   private static final Logger logger = LoggerFactory.getLogger("LCAP_EXTENSION_LOGGER");
   
    /**
     * 暂停当前线程
     *
     * @param millisecond 毫秒
     * @return 操作结果布尔值（是否暂停成功后）
     */
    @NaslLogic
    public static Boolean threadSleep(Long millisecond) {
        if (millisecond == null || millisecond <= 0) {
            logger.error("无效的毫秒数值，无法使线程进入休眠状态: {}", millisecond);
            return false;
        }
        try {
            Thread.sleep(millisecond);
            logger.info("线程休眠 {} 毫秒", millisecond);
            return true;
        } catch (InterruptedException e) {
            logger.error("线程休眠被中断", e);
            Thread.currentThread().interrupt();
            return false;
        }
    }

    /**
     * 让当前线程等待，直到被其他线程唤醒。
     *
     * @param lock 锁对象
     * @return 是否成功唤醒
     */
    @NaslLogic
    public static Boolean waitThread(String lock) {
        if (lock == null || lock.isEmpty()) {
            logger.error("锁对象不能为空或空字符串");
            return false;
        }
        synchronized (lock.intern()) {
            try {
                lock.intern().wait();
                logger.info("线程成功唤醒");
                return true;
            } catch (InterruptedException e) {
                logger.error("等待线程被中断", e);
                Thread.currentThread().interrupt();
                return false;
            }
        }
    }

    /**
     * 让当前线程等待，直到被其他线程唤醒或达到超时时间。（避免死锁）
     *
     * @param lock     锁对象
     * @param timeout  超时时间
     * @return 是否成功唤醒
     */
    @NaslLogic
    public static Boolean waitThreadWithTimeout(String lock, Long timeout) {
        if (lock == null || lock.isEmpty()) {
            logger.error("锁对象不能为空或空字符串");
            return false;
        }
        if (timeout < 0) {
            logger.error("超时时间不能为负数");
            return false;
        }
        synchronized (lock.intern()) {
            try {
                lock.intern().wait(timeout);
                logger.info("线程等待结束，可能是被唤醒或达到超时时间");
                return true;
            } catch (InterruptedException e) {
                logger.error("带超时的等待线程被中断", e);
                Thread.currentThread().interrupt();
                return false;
            }
        }
    }

    /**
     * 唤醒等待的线程
     *
     * @param lock 锁对象
     * @return 是否成功唤醒
     */
    @NaslLogic
    public static Boolean notifyThread(String lock) {
        if (lock == null || lock.isEmpty()) {
            logger.error("锁对象不能为空或空字符串");
            return false;
        }
        synchronized (lock.intern()) {
            try {
                lock.intern().notify();
                logger.info("唤醒了正在等待的线程");
                return true;
            } catch (IllegalMonitorStateException e) {
                logger.error("唤醒线程时监控状态不合法", e);
                return false;
            }
        }
    }

    /**
     * 唤醒所有等待的线程
     *
     * @param lock 锁对象
     * @return 是否成功唤醒
     */
    @NaslLogic
    public static Boolean notifyAllThreads(String lock) {
        if (lock == null || lock.isEmpty()) {
            logger.error("锁对象不能为空或空字符串");
            return false;
        }
        synchronized (lock.intern()) {
            try {
                lock.intern().notifyAll();
                logger.info("唤醒了所有正在等待的线程");
                return true;
            } catch (IllegalMonitorStateException e) {
                logger.error("唤醒所有线程时监控状态不合法", e);
                return false;
            }
        }
    }

}