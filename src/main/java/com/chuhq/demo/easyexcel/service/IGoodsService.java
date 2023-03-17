package com.chuhq.demo.easyexcel.service;

import com.chuhq.demo.easyexcel.entity.GoodsExcel;

import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;

/**
 * @author bk
 */
public interface IGoodsService {

    /**
     * 导出
     * @return
     */
    Supplier<Collection<?>> download();

    /**
     * 导入
     * @param goodsExcelsCache
     */
    void sync(List<GoodsExcel> goodsExcelsCache);
}
