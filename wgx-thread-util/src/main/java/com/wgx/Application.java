package com.wgx;

import com.wgx.lib.util.ThreadUtilLocalTest;

public class Application {
    public static void main(String[] args) {
        String lockObject = "testLock";

        // 创建并启动等待线程
        Thread waitingThread = new Thread(() -> {
            boolean waitResult = ThreadUtilLocalTest.waitThread(lockObject);
            System.out.println("等待线程等待结果: " + waitResult);
        });

        // 创建并启动唤醒线程
        Thread notifyingThread = new Thread(() -> {
            ThreadUtilLocalTest.threadSleep(1000); // 等待一段时间，确保等待线程已经开始等待
            boolean notifyResult = ThreadUtilLocalTest.notifyThread(lockObject);
            System.out.println("唤醒线程唤醒结果: " + notifyResult);
        });

        waitingThread.start();
        notifyingThread.start();

        try {
            waitingThread.join();
            notifyingThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // 测试带超时的等待线程
        boolean waitTimeoutResult = ThreadUtilLocalTest.waitThreadWithTimeout(lockObject, 2000L);
        System.out.println("带超时的线程等待结果: " + waitTimeoutResult);

        // 测试唤醒所有等待的线程
        boolean notifyAllResult = ThreadUtilLocalTest.notifyAllThreads(lockObject);
        System.out.println("所有线程唤醒结果: " + notifyAllResult);

        // 测试带超时的等待线程
        boolean waitTimeoutResult1 = ThreadUtilLocalTest.waitThreadWithTimeout(lockObject, 5000L);
        System.out.println("带超时的线程等待结果，超时情况下: " + waitTimeoutResult1);
    }
}