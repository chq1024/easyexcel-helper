package com.chuhq.demo.easyexcel.service.impl;

import com.chuhq.demo.easyexcel.entity.GoodsExcel;
import com.chuhq.demo.easyexcel.service.IGoodsService;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;

/**
 * @author bk
 */
@Service
public class GoodsServiceImpl implements IGoodsService {

    @Override
    public Supplier<Collection<?>> download() {
        return null;
    }

    @Override
    public void sync(List<GoodsExcel> goodsExcelsCache) {

    }
}
