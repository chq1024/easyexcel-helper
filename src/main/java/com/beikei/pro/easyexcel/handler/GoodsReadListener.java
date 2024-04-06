package com.beikei.pro.easyexcel.handler;

import com.alibaba.excel.context.AnalysisContext;
import com.beikei.pro.easyexcel.comment.IExcelHandler;
import com.beikei.pro.easyexcel.comment.IReadListener;
import com.beikei.pro.easyexcel.transform.GoodsExcel;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

/**
 * @author bk
 */
@Slf4j
public class GoodsReadListener extends IReadListener<GoodsExcel> {

//    private List<GoodsExcel> cacheAnalysedData = ListUtils.newArrayListWithCapacity(100);
    // 复用该对象
    private final List<GoodsExcel> cacheAnalysedData = new ArrayList<>(100);

    private final IExcelHandler<GoodsExcel> excelHandler;

    public GoodsReadListener(IExcelHandler<GoodsExcel> excelHandler) {
        this.excelHandler = excelHandler;
    }

    @Override
    public void invoke(GoodsExcel data, AnalysisContext context) {
        cacheAnalysedData.add(data);
        if (cacheAnalysedData.size() >= 20) {
            boolean batchSync = excelHandler.async(cacheAnalysedData,10);
            if (batchSync) {
//                cacheAnalysedData = ListUtils.newArrayListWithCapacity(100);
                cacheAnalysedData.clear();
            }
        }
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        if (!cacheAnalysedData.isEmpty()) {
            excelHandler.sync(cacheAnalysedData);
        }
        log.info("========= curr finished read =========");
    }
}
