package com.paymen.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * @description:
 * @author: huangzouqiang
 * @create: 2024-06-26 20:34
 * @Version 1.0
 **/
@Configuration
public class AsyncConfig {

    @Bean
    public TaskExecutor getAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        //核心线程池数量，方法: 返回可用处理器的Java虚拟机的数量。
        executor.setCorePoolSize(25);
        //最大线程数量
        executor.setMaxPoolSize(50);
        //线程池的队列容量
        executor.setQueueCapacity(50);
        //设置线程活跃时间（秒）
        executor.setKeepAliveSeconds(60);
        //线程名称的前缀
        executor.setThreadNamePrefix("async-task-thread-pool-");
        //设置拒绝策略
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        //等待所有任务结束时再关闭线程池
        executor.setWaitForTasksToCompleteOnShutdown(true);
        return executor;
    }
}
