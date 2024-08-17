package com.igse.config;

import com.igse.util.GlobalConstant;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@EnableAsync
@Configuration
public class SchedulerConfig {

    @Bean(name = GlobalConstant.OUT_OF_BOX_TASK_EXECUTOR)
    public ThreadPoolTaskExecutor outOfBoxAsyncTaskExecutor() {
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setCorePoolSize(5);
        taskExecutor.setMaxPoolSize(10);
        taskExecutor.setQueueCapacity(15);// Set the queue capacity for holding pending tasks
        taskExecutor.setThreadNamePrefix("outOfBox-");
        return taskExecutor;
    }
}
