package com.beikei.pro.easyexcel.util;

import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import javax.annotation.PreDestroy;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author bk
 */
public class ThreadHelper {

    private static final ThreadPoolTaskExecutor executor;
    private static final AtomicInteger ito = new AtomicInteger(0);

    static {
        executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(3);
        executor.setMaxPoolSize(6);
        executor.setAllowCoreThreadTimeOut(true);
        executor.setQueueCapacity(100);
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.setKeepAliveSeconds(60);
        executor.setThreadFactory(r -> {
            Thread thread = new Thread(r);
            thread.setName("Thread-Pool-" + ito.getAndIncrement());
            return thread;
        });
        executor.initialize();
    }


    public static ThreadPoolTaskExecutor pool() {
        return executor;
    }


    @PreDestroy
    public void destroy() {
        executor.shutdown();
    }
}
