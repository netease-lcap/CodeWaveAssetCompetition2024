package com.wgx;/*
package com.wgx;

import com.wgx.lib.delayqueue.modal.DelayedTask;
import com.wgx.lib.delayqueue.service.DelayQueueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.List;

@SpringBootApplication
public class Application implements CommandLineRunner {
    @Autowired
    private DelayQueueService delayedTaskService;

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

@Override
    public void run(String... args) throws Exception {
        // 创建任务
        DelayedTask task1 = new DelayedTask("task-001", 1000L, System.currentTimeMillis() + 1000L, "PENDING", "", "", "Sample Task 1");
        DelayedTask task2 = new DelayedTask("task-002", 2000L, System.currentTimeMillis() + 2000L, "PENDING", "", "", "Sample Task 2");
        DelayedTask task3 = new DelayedTask("task-003", 5000L, System.currentTimeMillis() + 5000L, "PENDING", "", "", "Sample Task 3");

        // 添加任务到延迟队列
        boolean added1 = delayedTaskService.addTask(task1);
        boolean added2 = delayedTaskService.addTask(task2);
        boolean added3 = delayedTaskService.addTask(task3);

        // 检查任务是否添加成功
        if (added1) {
            System.out.println("Task 1 added successfully");
        }
        if (added2) {
            System.out.println("Task 2 added successfully");
        }
        if (added3) {
            System.out.println("Task 3 added successfully");
        }

        // 尝试获取任务
        DelayedTask task = delayedTaskService.pollNow();
        if (task != null) {
            System.out.println("Polled task: " + task.getTaskName());
        }

        // 等待一段时间，然后尝试获取下一个任务
        Thread.sleep(1100); // 等待1100毫秒，足以让第一个任务到期
        task = delayedTaskService.take();
        if (task != null) {
            System.out.println("Took task1: " + task.getTaskName());
        }

        // 搜索任务
        List<DelayedTask> tasks = delayedTaskService.searchTasksByTaskName("Sample");
        for (DelayedTask t : tasks) {
            System.out.println("Found task: " + t);
        }

        // 获取队列大小
        int size = delayedTaskService.size();
        System.out.println("Queue size: " + size);

        task = delayedTaskService.take();
        if (task != null) {
            System.out.println("Took task2: " + task.getTaskName());
        }
        task = delayedTaskService.take();
        if (task != null) {
            System.out.println("Took task3: " + task.getTaskName());
        }

        // 清除所有任务
        boolean cleared = delayedTaskService.clearAllTasks();
        if (cleared) {
            System.out.println("All tasks cleared successfully");
        }

    //}
}
*/
