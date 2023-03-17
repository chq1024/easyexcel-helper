package com.chuhq.demo.easyexcel.service.impl;

import com.chuhq.demo.easyexcel.entity.StoreExcel;
import com.chuhq.demo.easyexcel.service.IStoreService;

import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;

/**
 * @author bk
 */
public class StoreServiceImpl implements IStoreService {

    @Override
    public Supplier<Collection<?>> download() {
        return null;
    }

    @Override
    public void sync(List<StoreExcel> storeExcelsCache) {

    }
}
