package com.beikei.pro.easyexcel.comment;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.exception.ExcelDataConvertException;
import com.alibaba.excel.read.listener.ReadListener;
import lombok.extern.slf4j.Slf4j;

/**
 * read监听抽象类，统一处理exception
 * @author bk
 */
@Slf4j
public abstract class IReadListener<T> implements ReadListener<T> {

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
