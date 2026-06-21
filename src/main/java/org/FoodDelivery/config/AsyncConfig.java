package org.FoodDelivery.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

/**
 * Dedicated executor backing asynchronous notification fan-out.
 *
 * <p>Status updates are published as application events and handled on this pool so the calling
 * (transactional) flow is never blocked by notification delivery.
 */
@Configuration
public class AsyncConfig {

    @Value("${app.async.core-pool-size:4}")
    private int corePoolSize;

    @Value("${app.async.max-pool-size:16}")
    private int maxPoolSize;

    @Value("${app.async.queue-capacity:200}")
    private int queueCapacity;

    @Bean(name = "notificationExecutor")
    public Executor notificationExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(maxPoolSize);
        executor.setQueueCapacity(queueCapacity);
        executor.setThreadNamePrefix("notify-");
        executor.initialize();
        return executor;
    }
}
