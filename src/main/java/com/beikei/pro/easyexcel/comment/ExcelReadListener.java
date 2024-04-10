package com.beikei.pro.easyexcel.comment;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.exception.ExcelDataConvertException;
import com.alibaba.excel.metadata.data.ReadCellData;
import com.alibaba.excel.read.listener.ReadListener;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author bk
 */
@Slf4j
public class ExcelReadListener implements ReadListener<Map<Integer,String>> {

    private final int DEFAULT_CACHE_SIZE = 20;

    private final List<Dict> cache = new ArrayList<>(DEFAULT_CACHE_SIZE);

    private final Map<Integer,String> columnIndexNameMap = new HashMap<>();

    private final DbHelper dbHelper;

    private final String uniqueName;

    public ExcelReadListener(String uniqueName,DbHelper dbHelper) {
        this.dbHelper = dbHelper;
        this.uniqueName = uniqueName;
    }

    @Override
    public void invokeHead(Map<Integer, ReadCellData<?>> headMap, AnalysisContext context) {
        for (Map.Entry<Integer, ReadCellData<?>> head : headMap.entrySet()) {
            Integer idx = head.getKey();
            ReadCellData<?> cellConstruct = head.getValue();
            columnIndexNameMap.put(idx,cellConstruct.getStringValue());
        }
    }

    @Override
    public void invoke(Map<Integer,String> data, AnalysisContext context) {
        cache.add(map2Dict(data));
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

    private Dict map2Dict(Map<Integer,String> cellMap) {
        Dict dict = new Dict();
        for (Map.Entry<Integer, String> entry : cellMap.entrySet()) {
            Integer idx = entry.getKey();
            String cell = entry.getValue();
            String columnName = columnIndexNameMap.get(idx);
            dict.put(columnName,cell);
        }
        dict.put(dbHelper.getDatumNon() + Const.TB,uniqueName);
        return dict;
    }
}
