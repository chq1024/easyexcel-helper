package com.beikei.pro.easyexcel.handler;

import com.alibaba.excel.context.AnalysisContext;
import com.beikei.pro.easyexcel.comment.IExcelHandler;
import com.beikei.pro.easyexcel.comment.IReadListener;
import com.beikei.pro.easyexcel.handler.transform.GoodsExcel;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

/**
 * GoodsReadListener是多例，每次read都是独立，无线程安全问题
 * @author bk
 */
@Slf4j
public class GoodsReadListener extends IReadListener<GoodsExcel> {

    private static final int DEFAULT_CACHE_BATCH_SIZE = 20;

    private static final int DEFAULT_SYNC_BATCH_SIZE = 10;

//    private List<GoodsExcel> cacheAnalysedData = ListUtils.newArrayListWithCapacity(100);
    // 复用该对象
    private final List<GoodsExcel> cacheAnalysedData = new ArrayList<>(DEFAULT_CACHE_BATCH_SIZE);

    private final IExcelHandler<GoodsExcel> excelHandler;

    public GoodsReadListener(IExcelHandler<GoodsExcel> excelHandler) {
        this.excelHandler = excelHandler;
    }

    /**
     * 如何处理每一条数据
     * 1. 先放入缓存，增加读取时的吞吐量
     * 2. 到达阈值，执行操作，当前时async（同步至db）,并且使用多线程处理；
     * 复用cache队列，无需每次创建新队列
     * @param data    one row value. Is is same as {@link AnalysisContext#readRowHolder()}
     * @param context analysis context
     */
    @Override
    public void invoke(GoodsExcel data, AnalysisContext context) {
        cacheAnalysedData.add(data);
        if (cacheAnalysedData.size() >= DEFAULT_CACHE_BATCH_SIZE) {
            boolean batchSync = excelHandler.async(cacheAnalysedData,DEFAULT_SYNC_BATCH_SIZE);
            if (batchSync) {
//                cacheAnalysedData = ListUtils.newArrayListWithCapacity(100);
                cacheAnalysedData.clear();
            }
        }
    }

    /**
     * 执行完所有invoke后的行为
     * 缓存中可能存在未执行具体行为的数据，批量一次性执行sync
     * @param context
     */
    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        if (!cacheAnalysedData.isEmpty()) {
            excelHandler.sync(cacheAnalysedData);
        }
        log.info("========= curr finished read =========");
    }
}
