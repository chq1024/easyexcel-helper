package com.beikei.pro.easyexcel.comment;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.beikei.pro.easyexcel.entity.PageResult;
import reactor.util.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * @author bk
 */
public interface IExcelHandler<T> {

    Consumer<List<T>> sync2Db();

    /**
     * 同步数据至DB （单线程）
     * @return
     */
    default boolean sync(List<T> batch) {
        sync2Db().accept(batch);
        return true;
    }

    /**
     * 同步数据至DB （多线程）
     * @param batch
     * @return
     */
    default boolean async(List<T> batch) {

        return false;
    }


    /**
     * 获取全量数据,复用分页做法
     * @return
     */
    default Supplier<List<T>> query(@Nullable LambdaQueryWrapper<T> queryWrapper) {
        return ()-> {
            long count = count(queryWrapper);
            List<T> arr = new ArrayList<>();
            for (int i = 0; i < count; i++) {
                PageResult<T> pageResult = pageQuery(i, 20, queryWrapper).get();
                arr.addAll(pageResult.getData());
            }
            return arr;
        };
    }

    /**
     * 分页获取数据
     * @return
     */
    Supplier<PageResult<T>> pageQuery(long page,int size,LambdaQueryWrapper<T> queryWrapper);

    /**
     * 查询总数（用于分页查询前操作）
     * @return
     */
    long count(LambdaQueryWrapper<T> queryWrapper);
}
