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

/**
 * excel工具类，用于简单的read,write
 * @author bk
 */
@SuppressWarnings("all")
@Slf4j
public class ExcelHelper {

    private static int PATE_SIZE = 20;

    public void read(MultipartFile file, String uniqueName) {
        ExcelEnum excelEnum = ExcelEnum.valueOfUniqueName(uniqueName);
        read0(file, excelEnum.getTransform(), excelEnum.getListener());
    }

    public void write(File file, String uniqueName, @Nullable LambdaQueryWrapper queryWrapper) {
        ExcelEnum excelEnum = ExcelEnum.valueOfUniqueName(uniqueName);
        write0("sheet1",file,excelEnum.getTransform(),(Class<IExcelHandler>)excelEnum.getHandler(),queryWrapper);
    }

    private void read0(MultipartFile file, Class transform, Class readListener) {
        try {
            ReadListener listener = (ReadListener) readListener.getDeclaredConstructor().newInstance();
            ExcelReaderBuilder read = EasyExcel.read(file.getInputStream(), transform, listener);
            read.sheet().doRead();
        } catch (Exception e) {
            log.error("======= read error!=========");
            throw new RuntimeException(e);
        }
    }

    private void write0(String sheetName, File file, Class transform, Class<IExcelHandler> handler, @Nullable LambdaQueryWrapper queryWrapper) {
        try (ExcelWriter excelWriter = EasyExcel.write(file, transform).build()) {
            WriteSheet writeSheet = EasyExcel.writerSheet(sheetName).build();
            // 读取数据
            IExcelHandler handlerInstance = handler.getDeclaredConstructor().newInstance();
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
}
