package com.banco.t4.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
public class ExecutorConfig {

    @Value("${app.executor.core-pool-size:4}")
    private int corePool;
    @Value("${app.executor.max-pool-size:8}")
    private int maxPool;
    @Value("${app.executor.queue-capacity:100}")
    private int queueCapacity;

    @Bean("processingExecutor")
    public Executor processingExecutor() {
        ThreadPoolTaskExecutor exec = new ThreadPoolTaskExecutor();
        exec.setCorePoolSize(corePool);
        exec.setMaxPoolSize(maxPool);
        exec.setQueueCapacity(queueCapacity);
        exec.setThreadNamePrefix("t4-exec-");
        exec.initialize();
        return exec;
    }
}
