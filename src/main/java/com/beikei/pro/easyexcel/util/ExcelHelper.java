package com.beikei.pro.easyexcel.util;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.read.builder.ExcelReaderBuilder;
import com.alibaba.excel.read.listener.ReadListener;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.beikei.pro.easyexcel.comment.IExcelHandler;
import com.beikei.pro.easyexcel.enums.ExcelEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.Nullable;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentHashMap;

/**
 * excel工具类，用于简单的read,write
 *
 * @author bk
 */
@SuppressWarnings("all")
@Slf4j
public class ExcelHelper {

    private static int PATE_SIZE = 20;

    private static ConcurrentHashMap<Class, Object> cacheHandleMap = new ConcurrentHashMap<>();

    public static void read(MultipartFile file, String uniqueName) {
        ExcelEnum excelEnum = ExcelEnum.valueOfUniqueName(uniqueName);
        read0(file, excelEnum.getTransform(), excelEnum.getListener(), excelEnum.getHandler());
    }

    public static void write(File file, String uniqueName, @Nullable LambdaQueryWrapper queryWrapper) {
        ExcelEnum excelEnum = ExcelEnum.valueOfUniqueName(uniqueName);
        write0("sheet1", file, excelEnum.getTransform(), excelEnum.getHandler(), queryWrapper);
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

    private static void write0(String sheetName, File file, Class transform, Class excelHandler, @Nullable LambdaQueryWrapper queryWrapper) {
        try (ExcelWriter excelWriter = EasyExcel.write(file, transform).build()) {
            WriteSheet writeSheet = EasyExcel.writerSheet(sheetName).build();
            // 读取数据
            IExcelHandler handlerInstance = handlerSingerInstance(excelHandler);
            long count = handlerInstance.count(queryWrapper);
            long pageNum = count % PATE_SIZE == 0 ? count % PATE_SIZE : (count % PATE_SIZE) + 1;
            for (long i = 0; i < pageNum; i++) {
                excelWriter.write(handlerInstance.pageQuery(i, PATE_SIZE, queryWrapper), writeSheet);
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
