package com.beikei.pro.easyexcel.util;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.read.builder.ExcelReaderBuilder;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.beikei.pro.easyexcel.comment.IExcelHandler;
import com.beikei.pro.easyexcel.comment.IReadListener;
import com.beikei.pro.easyexcel.enums.ExcelEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.Nullable;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * excel工具类，用于简单的read,write
 * 支持数据量较大时多sheet写入
 *
 * @author bk
 */
@SuppressWarnings("all")
@Slf4j
public class ExcelHelper {

    private static int DEFAULT_BATCH_WRITE_SIZE = 20;

    private static int DEFAULT_SHEET_SIZE_LIMIT = 100;

    private static ConcurrentHashMap<Class, Object> cacheHandleMap = new ConcurrentHashMap<>();

    public static void read(MultipartFile file, String uniqueName) {
        ExcelEnum excelEnum = ExcelEnum.valueOfUniqueName(uniqueName);
        read0(file, excelEnum);
    }

    public static void write(File file, String unqiueName) {
        write("sheet1", file, unqiueName, null, null);
    }

    public static void write(String sheetName, File file, String uniqueName, @Nullable LambdaQueryWrapper queryWrapper, @Nullable List<OrderItem> orderItems) {
        ExcelEnum excelEnum = ExcelEnum.valueOfUniqueName(uniqueName);
        write0(sheetName, file, excelEnum, queryWrapper, orderItems);
    }

    public static void write2Sheets(long sheetSize, File file, String uniqueName, @Nullable LambdaQueryWrapper queryWrapper, @Nullable List<OrderItem> orderItems) {
        ExcelEnum excelEnum = ExcelEnum.valueOfUniqueName(uniqueName);
        write0(sheetSize, file, excelEnum, queryWrapper, orderItems);
    }

    private static void read0(MultipartFile file, ExcelEnum excelEnum) {
        try {
            IExcelHandler handler = handlerSingerInstance(excelEnum.getHandler());
            IReadListener listener = (IReadListener) excelEnum.getListener().getDeclaredConstructor(IExcelHandler.class).newInstance(handler);
            ExcelReaderBuilder read = EasyExcel.read(file.getInputStream(), excelEnum.getTransform(), listener);
            read.sheet().doRead();
        } catch (Exception e) {
            log.error("======= read error!=========");
            throw new RuntimeException(e);
        }
    }

    private static void write0(String sheetName, File file, ExcelEnum excelEnum, @Nullable LambdaQueryWrapper queryWrapper, @Nullable List<OrderItem> orderItems) {
        try (ExcelWriter excelWriter = EasyExcel.write(file, excelEnum.getTransform()).build()) {
            WriteSheet writeSheet = EasyExcel.writerSheet(sheetName).build();
            // 读取数据
            IExcelHandler handler = handlerSingerInstance(excelEnum.getHandler());
            long count = handler.count(queryWrapper);
            pageWrite0(count,excelWriter,writeSheet,handler,queryWrapper,orderItems);
        } catch (Exception e) {
            log.error("======= write error!=========");
            throw new RuntimeException(e.getMessage());
        }
    }

    private static void write0(long sheetSize, File file, ExcelEnum excelEnum, @Nullable LambdaQueryWrapper queryWrapper, @Nullable List<OrderItem> orderItems) {
        try (ExcelWriter excelWriter = EasyExcel.write(file, excelEnum.getTransform()).build()) {
            IExcelHandler handler = handlerSingerInstance(excelEnum.getHandler());
            long count = handler.count(queryWrapper);
            long sheetNum = count % sheetSize == 0 ? count / sheetSize : (count / sheetSize) + 1;
            for (int i = 1; i <= sheetNum; i++) {
                WriteSheet writeSheet = EasyExcel.writerSheet(i).build();
                if (sheetSize <= DEFAULT_BATCH_WRITE_SIZE) {
                    excelWriter.write(handler.pageQuery(i, sheetSize, queryWrapper, orderItems), writeSheet);
                } else {
                    pageWrite0(count,excelWriter,writeSheet,handler,queryWrapper,orderItems);
                }
            }
        } catch (Exception e) {
            log.error("======= write error!=========");
            throw new RuntimeException(e.getMessage());
        }
    }

    private static void pageWrite0(long count, ExcelWriter excelWriter, WriteSheet writeSheet, IExcelHandler handler, LambdaQueryWrapper queryWrapper, List<OrderItem> orderItems) {
        long pageNum = count % DEFAULT_BATCH_WRITE_SIZE == 0 ? count / DEFAULT_BATCH_WRITE_SIZE : (count / DEFAULT_BATCH_WRITE_SIZE) + 1;
        for (long j = 0; j < pageNum; j++) {
            excelWriter.write(handler.pageQuery(j, DEFAULT_BATCH_WRITE_SIZE, queryWrapper, orderItems), writeSheet);
        }
    }

    private static IExcelHandler handlerSingerInstance(Class excelHandler) {
        boolean containsHandler = cacheHandleMap.containsKey(excelHandler);
        if (!containsHandler) {
            try {
                Constructor constructor = excelHandler.getDeclaredConstructor();
                constructor.setAccessible(true);
                Object nullFieldInstance = constructor.newInstance();
                Method instance = excelHandler.getDeclaredMethod("getInstance");
                IExcelHandler excelHandlerInstance = (IExcelHandler) instance.invoke(nullFieldInstance);
                cacheHandleMap.put(excelHandler, excelHandlerInstance);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return (IExcelHandler) cacheHandleMap.get(excelHandler);
    }
}
