package com.wgx.lib.util;

// 移除 @NaslLogic 注解
public class ThreadUtilLocalTest {

    /**
     * 暂停线程
     *
     * @param millisecond 毫秒
     * @return 操作结果布尔值（是否暂停成功后）
     */
    public static Boolean threadSleep(Integer millisecond) {
        if (millisecond == null || millisecond <= 0) {
            System.out.println("无效的毫秒数值，无法使线程进入休眠状态: " + millisecond);
            return false;
        }
        try {
            Thread.sleep(millisecond);
            System.out.println("线程休眠 " + millisecond + " 毫秒");
            return true;
        } catch (InterruptedException e) {
            System.out.println("线程休眠被中断");
            Thread.currentThread().interrupt();
            return false;
        }
    }

    /**
     * 等待线程
     *
     * @param lock 锁对象
     * @return 是否成功唤醒
     */
    public static Boolean waitThread(String lock) {
        if (lock == null || lock.isEmpty()) {
            System.out.println("锁对象不能为空或空字符串");
            return false;
        }
        synchronized (lock.intern()) {
            try {
                lock.intern().wait();
                System.out.println("线程成功唤醒");
                return true;
            } catch (InterruptedException e) {
                System.out.println("等待线程被中断");
                Thread.currentThread().interrupt();
                return false;
            }
        }
    }

    /**
     * 带超时的等待线程（避免死锁）
     *
     * @param lock     锁对象
     * @param timeout  超时时间
     * @return 是否成功唤醒
     */
    public static Boolean waitThreadWithTimeout(String lock, Long timeout) {
        if (lock == null || lock.isEmpty()) {
            System.out.println("锁对象不能为空或空字符串");
            return false;
        }
        synchronized (lock.intern()) {
            try {
                if (timeout <= 0) {
                    System.out.println("超时时间必须大于0");
                    return false;
                }
                lock.intern().wait(timeout);
                System.out.println("线程在超时前被成功唤醒");
                return true;
            } catch (InterruptedException e) {
                System.out.println("带超时的等待线程被中断");
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
    public static Boolean notifyThread(String lock) {
        if (lock == null || lock.isEmpty()) {
            System.out.println("锁对象不能为空或空字符串");
            return false;
        }
        synchronized (lock.intern()) {
            try {
                lock.intern().notify();
                System.out.println("唤醒了正在等待的线程");
                return true;
            } catch (IllegalMonitorStateException e) {
                System.out.println("唤醒线程时监控状态不合法");
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
    public static Boolean notifyAllThreads(String lock) {
        if (lock == null || lock.isEmpty()) {
            System.out.println("锁对象不能为空或空字符串");
            return false;
        }
        synchronized (lock.intern()) {
            try {
                lock.intern().notifyAll();
                System.out.println("唤醒了所有正在等待的线程");
                return true;
            } catch (IllegalMonitorStateException e) {
                System.out.println("唤醒所有线程时监控状态不合法");
                return false;
            }
        }
    }

}