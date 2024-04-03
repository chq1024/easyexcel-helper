package com.beikei.pro.easyexcel.comment;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.beikei.pro.easyexcel.entity.PageResult;
import reactor.util.annotation.Nullable;

import java.util.List;
import java.util.function.Supplier;

/**
 * @author bk
 */
public interface IExcelHandler<T> {

    /**
     * 同步数据至DB （单线程）
     * @param batch
     * @return
     */
    default boolean sync(List<T> batch) {
        return false;
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
     * 获取全量数据
     * @return
     */
    default Supplier<List<T>> queryAll(@Nullable LambdaQueryWrapper<T> queryWrapper) {
        return ()-> null;
    }

    /**
     * 分页获取数据
     * @return
     */
    default Supplier<PageResult<T>> pageQuery(long page,int size,LambdaQueryWrapper<T> queryWrapper) {
        return ()-> null;
    }

    /**
     * 查询总数（用于分页查询前操作）
     * @return
     */
    default long count(LambdaQueryWrapper<T> queryWrapper) {
        return 0;
    }
}
