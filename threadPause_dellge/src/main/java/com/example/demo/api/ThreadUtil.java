package com.example.demo.api;


import com.netease.lowcode.core.annotation.NaslLogic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ThreadUtil {

    //参数使用LCAP_EXTENSION_LOGGER后日志会显示在平台日志功能中
    private static final Logger log = LoggerFactory.getLogger("LCAP_EXTENSION_LOGGER");


    /**
     * 暂停线程
     *
     * @param millisecond 毫秒
     */
    @NaslLogic
    public static String threadSleep(Integer millisecond) {
        if (millisecond == null) {
            return "输入不可以为空";
        } else if (millisecond <= 0) {
            return "输入不可以为0或负数";
        }
            try {
                Thread.sleep(millisecond);
                return "success sleep" + millisecond + "ms";
            } catch (InterruptedException e) {
                log.error("暂停线程失败:"+e);
                return "false"+e;
            }
        }

        /**
         * 等待线程
         *
         * @param str
         * @param timeoutMillis
         * @return
         */
        @NaslLogic
        public static String wait(String str, Long timeoutMillis) {
            if (str == null || str.equals("")) {
                str = "default"; // 为null时设置默认字符串
            }
            String internedString = str.intern();
            synchronized (internedString) {
                try {
                    internedString.wait(timeoutMillis);
                    return "true";
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    log.error("等待线程失败:" + e);
                    return "false"+ e;
                }
            }
        }

        /**
         * notify 唤醒线程
         *
         * @param str
         * @return
         */
        @NaslLogic
        public static String notify (String str){
            if (str == null || str.equals("")) {
                str = "default";
            }
            String internedString = str.intern();
            synchronized (internedString) {
                try {
                    internedString.notify();
                } catch (IllegalMonitorStateException e) {
                    log.error("notify 唤醒线程失败:"+e);
                    return "false"+e;
                }
                return "success";
            }
        }


        /**
         * notifyAll 唤醒线程
         *
         * @param str
         * @return
         */
        @NaslLogic
        public static String notifyAll (String str){
            if (str == null || str.equals("")) {
                str = "default";
            }
            String internedString = str.intern();
            synchronized (internedString) {
                try {
                    internedString.notifyAll();
                } catch (IllegalMonitorStateException e) {
                    log.error("notifyAll 唤醒线程失败"+e);
                    return "false"+e;
                }
                return "success";
            }
        }
    }
