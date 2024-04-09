package com.beikei.pro.easyexcel.comment;

import com.beikei.pro.easyexcel.util.ThreadHelper;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * @author bk
 */
public class ExcelHandler {

    private final int DEFAULT_MAX_BATCH_SIZE = 10;

    private volatile static ExcelHandler handler;
    private final DbHelper dbHelper;

    private ExcelHandler(DbHelper dbHelper) {
        this.dbHelper = dbHelper;
    }

    public static ExcelHandler getInstance(DbHelper dbHelper) {
        if (handler == null) {
            synchronized (ExcelHandler.class) {
                handler = new ExcelHandler(dbHelper);
            }
        }
        return handler;
    }

    /**
     * 批量处理数据
     *
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
     *
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
            pool.execute(() -> {
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
        return dbHelper::insert2db;
    }

    public long count(Dict queryWrapper) {
        return dbHelper.count(queryWrapper);
    }

    public Supplier<List<Dict>> batchQuery(long page, int size, Dict queryWrapper, Dict orderItems) {
        return () -> dbHelper.batchQuery(page,size,queryWrapper,orderItems);
    }
}
