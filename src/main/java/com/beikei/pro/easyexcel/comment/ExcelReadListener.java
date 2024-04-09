package com.beikei.pro.easyexcel.comment;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.exception.ExcelDataConvertException;
import com.alibaba.excel.read.listener.ReadListener;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

/**
 * @author bk
 */
@Slf4j
public class ExcelReadListener implements ReadListener<Dict> {

    private final int DEFAULT_CACHE_SIZE = 20;

    private final List<Dict> cache = new ArrayList<>(DEFAULT_CACHE_SIZE);

    private final DbHelper dbHelper;

    public ExcelReadListener(DbHelper dbHelper) {
        this.dbHelper = dbHelper;
    }

    @Override
    public void invoke(Dict data, AnalysisContext context) {
        cache.add(data);
        if (cache.size() >= DEFAULT_CACHE_SIZE) {
            ExcelHandler handler = ExcelHandler.getInstance(dbHelper);
            boolean synced = handler.sync(cache);
            if (synced) {
                cache.clear();
            }
        }
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        if (!cache.isEmpty()) {
            ExcelHandler handler = ExcelHandler.getInstance(dbHelper);
            boolean synced = handler.sync(cache);
            if (synced) {
                cache.clear();
            }
        }
    }

    @Override
    public void onException(Exception exception, AnalysisContext context) {
        if (exception instanceof ExcelDataConvertException) {
            ExcelDataConvertException ex = (ExcelDataConvertException) exception;
            log.error(String.format("====== read error. row_index: %d,column_index_num: %d,data is: %s ======", ex.getRowIndex(), ex.getColumnIndex(),ex.getCellData()));
        } else {
            int rowIndex = context.readRowHolder().getRowIndex();
            int headRowNumber = context.currentReadHolder().excelReadHeadProperty().getHeadRowNumber();
            log.error(String.format("====== read error. row_index: %d , head_row_num: %d ======", rowIndex, headRowNumber));
        }
        throw new RuntimeException(exception);
    }
}
