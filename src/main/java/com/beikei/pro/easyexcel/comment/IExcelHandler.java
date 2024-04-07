package com.beikei.pro.easyexcel.comment;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.beikei.pro.easyexcel.util.ThreadHelper;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import reactor.util.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * excel处理接口
 * 实现该接口的类只需要关注
 * 0. 单例获取实现类（因为该实现类与listener不同，多次操作可共用同一个实现类）
 * 1. 同步到db的具体行为 （read）
 * 2. 分页查询db数据时的行为 (write)
 * 3. 查询db数据总数 (write)
 * @author bk
 */
public interface IExcelHandler<T> {

    /**
     * 获取单例
     *
     * @return
     */
    IExcelHandler<T> getInstance();

    /**
     * 同步到db的具体行为
     *
     * @return
     */
    Consumer<List<T>> sync2Db();

    /**
     * 同步数据至DB （单线程）
     * @param batch 本批次数据
     * @return
     */
    default boolean sync(List<T> batch) {
        sync2Db().accept(batch);
        return true;
    }

    /**
     * 同步数据至DB （多线程）
     * 不保证顺序，并且，数据库中需要加入唯一索引
     * @param batch 本批次数据
     * @param taskSize 每个线程处理的任务数据大小
     * @return
     */
    default boolean async(List<T> batch,int taskSize) {
        int size = batch.size();
        int per = size % taskSize > 0 ? size / taskSize + 1 : size / taskSize;
        ThreadPoolTaskExecutor pool = ThreadHelper.pool();
//        AtomicInteger inc = new AtomicInteger(0);
//        List<CompletableFuture<Void>> tasks = new ArrayList<>();
//        while (inc.get() < per) {
//            int index = inc.incrementAndGet();
//            CompletableFuture<Void> future = CompletableFuture.runAsync(()->{
//                int curr = (index- 1) * batchSize;
//                int next = index * batchSize;
//                if (index == per) {
//                    next = batch.size();
//                }
//                sync2Db().accept(batch.subList(curr, next));
//            },pool);
//            tasks.add(future);
//        }
//        try {
//            CompletableFuture.allOf(tasks.toArray(new CompletableFuture[]{})).get();
//            return true;
//        } catch (InterruptedException | ExecutionException e) {
//            throw new RuntimeException(e);
//        }

        CountDownLatch latch = new CountDownLatch(per);
        for (int i = 1; i <= per; i++) {
            int count = i;
            pool.execute(()->{
                int curr = (count- 1) * taskSize;
                int next = count * taskSize;
                if (count == per) {
                    next = batch.size();
                }
                System.out.println(Thread.currentThread().getName());
                sync2Db().accept(batch.subList(curr, next));
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


    /**
     * 获取全量数据,复用分页做法
     * @param queryWrapper 查询条件
     * @param orderItems 排序条件
     * @return
     */
    default Supplier<List<T>> query(@Nullable LambdaQueryWrapper<T> queryWrapper,@Nullable List<OrderItem> orderItems) {
        return () -> {
            long count = count(queryWrapper);
            List<T> arr = new ArrayList<>();
            for (int i = 0; i < count; i++) {
                arr.addAll(pageQuery(i, 20, queryWrapper,orderItems).get());
            }
            return arr;
        };
    }

    /**
     * 分页获取数据
     * @param page 当前页
     * @param size 当前页大小
     * @param queryWrapper 查询条件
     * @param orderItems 排序条件
     * @return
     */
    Supplier<List<T>> pageQuery(long page, long size, @Nullable LambdaQueryWrapper<T> queryWrapper, @Nullable List<OrderItem> orderItems);

    /**
     * 查询总数（用于分页查询前操作）
     * @param queryWrapper 查询条件
     * @return
     */
    long count(LambdaQueryWrapper<T> queryWrapper);
}
