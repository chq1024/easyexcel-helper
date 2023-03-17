package com.chuhq.demo.easyexcel.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.read.listener.ReadListener;
import com.alibaba.excel.util.ListUtils;
import com.chuhq.demo.easyexcel.entity.GoodsExcel;
import com.chuhq.demo.easyexcel.service.IGoodsService;
import com.chuhq.demo.easyexcel.service.impl.GoodsServiceImpl;
import com.chuhq.demo.easyexcel.util.SpringUtil;

import java.util.List;

/**
 * 商品excel读取监听器
 * @author bk
 */
public class GoodsReadListener implements ReadListener<GoodsExcel> {

    private static final int BATCH_COUNT = 10;

    private List<GoodsExcel> goodsExcelsCache = ListUtils.newArrayListWithCapacity(10);

    private final IGoodsService goodsService;

    public GoodsReadListener() {
        goodsService = new GoodsServiceImpl();
    }

    public GoodsReadListener(IGoodsService storeService) {
        this.goodsService = storeService;
    }

    public GoodsReadListener(String beanName) {
        goodsService = SpringUtil.getBean(beanName,IGoodsService.class);
    }

    @Override
    public void onException(Exception exception, AnalysisContext context) throws Exception {
        ReadListener.super.onException(exception, context);
    }

    @Override
    public void invoke(GoodsExcel data, AnalysisContext context) {
        goodsExcelsCache.add(data);
        if (goodsExcelsCache.size() >= BATCH_COUNT) {
            goodsService.sync(goodsExcelsCache);
            goodsExcelsCache = ListUtils.newArrayListWithCapacity(10);
        }
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        goodsService.sync(goodsExcelsCache);
    }
}
