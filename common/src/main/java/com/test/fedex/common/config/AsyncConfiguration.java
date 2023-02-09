package com.test.fedex.common.config;

import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.aop.interceptor.SimpleAsyncUncaughtExceptionHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

/**
 * @author Aashish Thakran
 * @version 1.0
 *
 * This class contains executor configurations.
 */
@Configuration
@EnableAsync
public class AsyncConfiguration implements AsyncConfigurer {

    private static final String TASK_EXECUTOR_NAME_PREFIX_DEFAULT = "taskExecutor-";

    @Value("${pool.thread.core-size}")
    private Integer corePoolSize;

    @Value("${pool.thread.max-size}")
    private Integer maxPoolSize;

    @Value("${pool.thread.queue-capacity}")
    private Integer queueCapacity;

    /**
     * This method creates {@link Executor} object.
     * @return Executor
     */
    @Bean
    @Override
    public Executor getAsyncExecutor() {
        return taskExecutor(TASK_EXECUTOR_NAME_PREFIX_DEFAULT);
    }

    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return new SimpleAsyncUncaughtExceptionHandler();
    }

    /**
     * This method creates {@link Executor} object and set pool size configurations.
     * @param taskExecutorNamePrefix
     * @return Executor
     */
    private Executor taskExecutor(final String taskExecutorNamePrefix) {
        final ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(maxPoolSize);
        executor.setQueueCapacity(queueCapacity);
        executor.setThreadNamePrefix(taskExecutorNamePrefix);
        return executor;
    }
}
