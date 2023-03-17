package com.chuhq.demo.easyexcel.service;

import com.chuhq.demo.easyexcel.entity.StoreExcel;

import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;

/**
 * @author bk
 */
public interface IStoreService {

    /**
     * 导出
     * @return
     */
    Supplier<Collection<?>> download();

    /**
     * 导入
     * @param storeExcelsCache
     */
    void sync(List<StoreExcel> storeExcelsCache);
}
