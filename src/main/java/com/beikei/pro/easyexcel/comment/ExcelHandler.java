package com.beikei.pro.easyexcel.comment;

import com.beikei.pro.easyexcel.util.SpringUtils;
import com.beikei.pro.easyexcel.util.ThreadHelper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.util.StringUtils;

import java.awt.*;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * @author bk
 */
public class ExcelHandler {

    private final int DEFAULT_MAX_BATCH_SIZE = 10;

    private volatile static ExcelHandler handler;

    private ExcelHandler() {}

    public static ExcelHandler getInstance() {
        if (handler == null) {
            synchronized (ExcelHandler.class) {
                handler = new ExcelHandler();
            }
        }
        return handler;
    }

    /**
     * 批量处理数据
     * @param data
     * @return
     */
    public boolean sync(List<Dict> data) {
        if (data.size() >= DEFAULT_MAX_BATCH_SIZE) {
            return async(data);
        } else {
            syncdb().accept(data);
            return true;
        }
    }

    /**
     * 多线程处理大批量数据
     * @param data
     * @return
     */
    private boolean async(List<Dict> data) {
        int size = data.size();
        int times = size % DEFAULT_MAX_BATCH_SIZE > 0 ? (size / DEFAULT_MAX_BATCH_SIZE) + 1 : size / DEFAULT_MAX_BATCH_SIZE;
        ThreadPoolTaskExecutor pool = ThreadHelper.pool();
        CountDownLatch latch = new CountDownLatch(times);
        for (int i = 0; i < times; i++) {
            int curr = i * DEFAULT_MAX_BATCH_SIZE;
            int next = (i + 1) * DEFAULT_MAX_BATCH_SIZE;
            if (i + 1 == times) {
                next = size;
            }
            List<Dict> batch = data.subList(curr, next);
            pool.execute(()->{
                syncdb().accept(batch);
                latch.countDown();
            });
        }
        try {
            latch.await();
            return true;
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }


    private Consumer<List<Dict>> syncdb() {
        return (data)->{
            for (Dict datum : data) {
                String tbName = (String) datum.getOrDefault("tb", "");
                if (!StringUtils.hasText(tbName)) {
                    throw new RuntimeException("数据中必须包含tb字段");
                }
                // 找到缓存表结构，对于数据中的tb version 来决定是否需要刷新本地表结构缓存

                // 自定义插入
                StringBuilder builder = new StringBuilder("INSERT INTO " + tbName + "(");
                AtomicInteger decr = new AtomicInteger(datum.keySet().size());
                for (String key : datum.keySet()) {
                    builder.append(key);
                    decr.decrementAndGet();
                    if (decr.get() != 0) {
                        builder.append(",");
                    }
                }
                builder.append(")").append(" ").append("VALUES(");

                // 1. 动态sql拼接，参数传递
                JdbcTemplate template = SpringUtils.getBean(JdbcTemplate.class);
                template.update("",)
                // 2. 或者玩家可自定义使用方式，如自定义实体类等等，
            }
        };
    }


    public long count(Dict queryWrapper) {


        return 0;
    }

    public Supplier<List<Dict>> batchQuery(long page, int size, Dict queryWrapper, Dict orderItems) {
        return ()->{

            return null;
        };
    }
}
