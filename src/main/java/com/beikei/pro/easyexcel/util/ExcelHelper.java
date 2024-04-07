package com.beikei.pro.easyexcel.util;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.read.builder.ExcelReaderBuilder;
import com.alibaba.excel.read.listener.ReadListener;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.beikei.pro.easyexcel.comment.IExcelHandler;
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
 *
 * @author bk
 */
@SuppressWarnings("all")
@Slf4j
public class ExcelHelper {

    private static int DEFAULT_PATE_SIZE = 20;

    private static int DEFAULT_SHEET_SIZE_LIMIT = 100;

    private static ConcurrentHashMap<Class, Object> cacheHandleMap = new ConcurrentHashMap<>();

    public static void read(MultipartFile file, String uniqueName) {
        ExcelEnum excelEnum = com.beikei.pro.easyexcel.enums.ExcelEnum.valueOfUniqueName(uniqueName);
        read0(file, excelEnum.getTransform(), excelEnum.getListener(), excelEnum.getHandler());
    }

    public static void write(File file, String unqiueName) {
        write(file, unqiueName, null, null);
    }

    public static void write(File file, String uniqueName, @Nullable LambdaQueryWrapper queryWrapper, @Nullable List<OrderItem> orderItems) {
        ExcelEnum excelEnum = com.beikei.pro.easyexcel.enums.ExcelEnum.valueOfUniqueName(uniqueName);
        write0("sheet1", file, excelEnum, queryWrapper, orderItems);
    }

    public static void write2Sheets(long sheetSize,File file,String uniqueName,@Nullable LambdaQueryWrapper queryWrapper, @Nullable List<OrderItem> orderItems) {
        ExcelEnum excelEnum = com.beikei.pro.easyexcel.enums.ExcelEnum.valueOfUniqueName(uniqueName);
        write0(sheetSize,file,excelEnum,queryWrapper,orderItems);
    }

    private static void read0(MultipartFile file, Class transform, Class readListener, Class excelHandler) {
        try {
            IExcelHandler excelHandlerInstance = handlerSingerInstance(excelHandler);
            ReadListener listener = (ReadListener) readListener.getDeclaredConstructor(IExcelHandler.class).newInstance(excelHandlerInstance);
            ExcelReaderBuilder read = EasyExcel.read(file.getInputStream(), transform, listener);
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
            IExcelHandler handlerInstance = handlerSingerInstance(excelEnum.getHandler());
            long count = handlerInstance.count(queryWrapper);
            long pageNum = count % DEFAULT_PATE_SIZE == 0 ? count / DEFAULT_PATE_SIZE : (count / DEFAULT_PATE_SIZE) + 1;
            for (long i = 0; i < pageNum; i++) {
                excelWriter.write(handlerInstance.pageQuery(i, DEFAULT_PATE_SIZE, queryWrapper, orderItems), writeSheet);
            }
        } catch (Exception e) {
            log.error("======= write error!=========");
            throw new RuntimeException(e.getMessage());
        }
    }

    private static void write0(long sheetSize, File file, ExcelEnum excelEnum, @Nullable LambdaQueryWrapper queryWrapper, @Nullable List<OrderItem> orderItems) {
        try (ExcelWriter excelWriter = EasyExcel.write(file, excelEnum.getTransform()).build()) {
            IExcelHandler handlerInstance = handlerSingerInstance(excelEnum.getHandler());
            long count = handlerInstance.count(queryWrapper);
            long sheetNum = count % sheetSize == 0 ? count / sheetSize : (count / sheetSize) + 1;
            for (int i = 0; i < sheetNum; i++) {
                WriteSheet writeSheet = EasyExcel.writerSheet(i).build();
                if (sheetSize <= DEFAULT_SHEET_SIZE_LIMIT) {
                    excelWriter.write(handlerInstance.pageQuery(i, sheetSize, queryWrapper, orderItems), writeSheet);
                } else {
                    long pageNum = count % DEFAULT_PATE_SIZE == 0 ? count / DEFAULT_PATE_SIZE : (count / DEFAULT_PATE_SIZE) + 1;
                    for (long j = 0; j < pageNum; j++) {
                        excelWriter.write(handlerInstance.pageQuery(j, DEFAULT_PATE_SIZE, queryWrapper, orderItems), writeSheet);
                    }
                }
            }
        } catch (Exception e) {
            log.error("======= write error!=========");
            throw new RuntimeException(e.getMessage());
        }
    }

    private static IExcelHandler handlerSingerInstance(Class excelHandler) {
        boolean containsHandler = cacheHandleMap.containsKey(excelHandler);
        if (!containsHandler) {
            try {
                Constructor constructor = excelHandler.getDeclaredConstructor();
                constructor.setAccessible(true);
                Object nullFieldInstance = constructor.newInstance();
                Method getInstance = excelHandler.getDeclaredMethod("getInstance");
                IExcelHandler excelHandlerInstance = (IExcelHandler) getInstance.invoke(nullFieldInstance);
                cacheHandleMap.put(excelHandler, excelHandlerInstance);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return (IExcelHandler) cacheHandleMap.get(excelHandler);
    }
}
