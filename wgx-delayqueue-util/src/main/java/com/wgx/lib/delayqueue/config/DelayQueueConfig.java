package com.wgx.lib.delayqueue.config;

import com.wgx.lib.delayqueue.modal.DelayedTask;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.DelayQueue;

@Configuration
public class DelayQueueConfig {
    @Bean
    public DelayQueue<DelayedTask> delayQueue() {
        return new DelayQueue<>();
    }
}
