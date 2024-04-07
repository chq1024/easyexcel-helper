package com.beikei.pro.easyexcel.handler;

import com.alibaba.excel.context.AnalysisContext;
import com.beikei.pro.easyexcel.comment.Dict;
import com.beikei.pro.easyexcel.comment.IExcelHandler;
import com.beikei.pro.easyexcel.comment.IReadListener;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

/**
 * @author bk
 */
@Slf4j
public class DictReadListener extends IReadListener<Dict> {

    private static final int DEFAULT_CACHE_BATCH_SIZE = 20;

    private static final int DEFAULT_SYNC_BATCH_SIZE = 10;

    private final List<Dict> cache = new ArrayList<>(DEFAULT_CACHE_BATCH_SIZE);

    private final IExcelHandler<Dict> excelHandler;

    public DictReadListener(IExcelHandler<Dict> excelHandler) {
        this.excelHandler = excelHandler;
    }

    @Override
    public void invoke(Dict data, AnalysisContext context) {
        cache.add(data);
        if (cache.size() >= DEFAULT_CACHE_BATCH_SIZE) {
            boolean synced = excelHandler.async(cache, DEFAULT_SYNC_BATCH_SIZE);
            if (synced) {
                cache.clear();
            }
        }
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        if (!cache.isEmpty()) {
            boolean sync = excelHandler.sync(cache);
            if (sync) {
                cache.clear();
                log.info("========= curr finished read =========");
            }
        }
    }
}
