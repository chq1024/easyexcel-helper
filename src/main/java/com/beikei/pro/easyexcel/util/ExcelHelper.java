package com.beikei.pro.easyexcel.util;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.read.builder.ExcelReaderBuilder;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.beikei.pro.easyexcel.comment.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.Nullable;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * excel工具类，用于简单的read,write
 * 支持数据量较大时多sheet写入
 * @author bk
 */
@SuppressWarnings("all")
@Slf4j
public class ExcelHelper {

    private static int DEFAULT_BATCH_WRITE_MAX_SIZE = 20;

    private static int DEFAULT_SHEET_MAX_SIZE = 100;

    private static ConcurrentHashMap<Class, Object> cacheHandleMap = new ConcurrentHashMap<>();

    public static void read(MultipartFile file, String unqiueName,DbHelper dbHelper) {
        read0(file, unqiueName,dbHelper);
    }

    public static void write(File file, String unqiueName,DbHelper dbHelper) {
        write(file, unqiueName,dbHelper, null, null);
    }

    public static void write(File file, String uniqueName, DbHelper dbHelper, @Nullable Dict queryWrapper, @Nullable Dict orderItems) {
        write0(file, uniqueName,dbHelper, queryWrapper, orderItems);
    }

    private static void read0(MultipartFile file, String unqiueName,DbHelper dbHelper) {
        try {
            ExcelReadListener listener = new ExcelReadListener(dbHelper);
            ExcelReaderBuilder read = EasyExcel.read(file.getInputStream(), listener);
            read.sheet().doRead();
        } catch (Exception e) {
            log.error("======= read error!=========");
            throw new RuntimeException(e);
        }
    }

    private static void write0(File file, String unqiueName, DbHelper dbHelper,@Nullable Dict queryWrapper, @Nullable Dict orderItems) {
        List<List<String>> heads = new ArrayList<>();
        ArrayList<String> head0 = new ArrayList<>();
        head0.add("id");
        ArrayList<String> head1 = new ArrayList<>();
        head1.add("gid");
        ArrayList<String> head2 = new ArrayList<>();
        head2.add("name");
        heads.add(head0);
        heads.add(head1);
        heads.add(head2);
        try (ExcelWriter excelWriter = EasyExcel.write(file).head(heads).build()) {
            ExcelHandler handler = ExcelHandler.getInstance(dbHelper);
            long count = handler.count(queryWrapper);
            if (count > DEFAULT_SHEET_MAX_SIZE) {
                long sheetNum = count % DEFAULT_BATCH_WRITE_MAX_SIZE > 0 ? count / DEFAULT_BATCH_WRITE_MAX_SIZE + 1 : count / DEFAULT_BATCH_WRITE_MAX_SIZE;
                for (long j = 1; j <= sheetNum; j++) {
                    WriteSheet writeSheet = EasyExcel.writerSheet("sheet_" + j).build();
                    long batch = DEFAULT_SHEET_MAX_SIZE;
                    if (j == sheetNum) {
                        batch = count - (j - 1) * DEFAULT_SHEET_MAX_SIZE;
                    }
                    sheetWrite(batch,excelWriter,writeSheet,handler,queryWrapper,orderItems);
                }
            } else {
                WriteSheet writeSheet = EasyExcel.writerSheet("sheet_1").build();
                sheetWrite(count,excelWriter,writeSheet,handler,queryWrapper,orderItems);
            }
        } catch (Exception e) {
            log.error("======= write error!=========");
            throw e;
        }
    }

    private static void sheetWrite(long batch,ExcelWriter excelWriter,WriteSheet writeSheet,ExcelHandler handler,@Nullable Dict queryWrapper, @Nullable Dict orderItems) {
        long times = batch % DEFAULT_BATCH_WRITE_MAX_SIZE > 0 ? (batch / DEFAULT_BATCH_WRITE_MAX_SIZE) + 1: batch / DEFAULT_BATCH_WRITE_MAX_SIZE;
        for (int i = 0; i < times; i++) {
            List<Dict> batchData = handler.batchQuery(i, DEFAULT_BATCH_WRITE_MAX_SIZE, queryWrapper, orderItems).get();
            excelWriter.write(batchData, writeSheet);
        }
    }
}
