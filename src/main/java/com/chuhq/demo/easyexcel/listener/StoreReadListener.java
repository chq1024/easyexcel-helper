package com.chuhq.demo.easyexcel.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.read.listener.ReadListener;
import com.alibaba.excel.util.ListUtils;
import com.chuhq.demo.easyexcel.entity.StoreExcel;
import com.chuhq.demo.easyexcel.service.IStoreService;
import com.chuhq.demo.easyexcel.service.impl.StoreServiceImpl;
import com.chuhq.demo.easyexcel.util.SpringUtil;
import java.util.List;

/**
 * 商店excel读取监听器
 * @author bk
 */
public class StoreReadListener implements ReadListener<StoreExcel> {

    private static final int BATCH_COUNT = 10;

    private List<StoreExcel> storeExcelsCache = ListUtils.newArrayListWithCapacity(10);

    private final IStoreService storeService;

    public StoreReadListener() {
        storeService = new StoreServiceImpl();
    }

    public StoreReadListener(IStoreService storeService) {
        this.storeService = storeService;
    }

    public StoreReadListener(String beanName) {
        storeService = SpringUtil.getBean(beanName,IStoreService.class);
    }

    @Override
    public void onException(Exception exception, AnalysisContext context) throws Exception {
        ReadListener.super.onException(exception, context);
    }

    @Override
    public void invoke(StoreExcel data, AnalysisContext context) {
        storeExcelsCache.add(data);
        if (storeExcelsCache.size() >=BATCH_COUNT) {
            storeService.sync(storeExcelsCache);
            storeExcelsCache = ListUtils.newArrayListWithCapacity(10);
        }
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        storeService.sync(storeExcelsCache);
    }
}
