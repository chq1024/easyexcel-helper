package com.chuhq.demo.easyexcel.util;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.read.listener.ReadListener;
import com.alibaba.excel.write.builder.ExcelWriterBuilder;
import com.alibaba.excel.write.handler.WriteHandler;
import com.alibaba.excel.write.metadata.WriteSheet;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;
import java.util.function.Supplier;

/**
 * @author bk
 */
public class ExcelUtil {


    public static <T> void download(String fileName, Class<T> clazz, Supplier<Collection<?>> supplier,@Nullable String templateName,@Nullable WriteHandler...writeHandlers) {
        ExcelWriterBuilder write = EasyExcel.write(fileName, clazz);
        write(write,supplier,templateName,writeHandlers);
    }

    public static <T> void download(OutputStream outputStream, Class<T> clazz, Supplier<Collection<?>> supplier, @Nullable String templateName, @Nullable WriteHandler...writeHandlers) {
        ExcelWriterBuilder write = EasyExcel.write(outputStream, clazz).inMemory(true).autoCloseStream(Boolean.FALSE);
        write(write,supplier,templateName,writeHandlers);
    }

    private static <T> void write(ExcelWriterBuilder write, Supplier<Collection<?>> supplier, @Nullable String templateName, @Nullable WriteHandler...writeHandlers) {
        if (writeHandlers != null && writeHandlers.length > 0) {
            // 添加写拦截器
            for (WriteHandler writeHandler : writeHandlers) {
                write.registerWriteHandler(writeHandler);
            }
        }
        if (StringUtils.hasText(templateName)) {
            write.withTemplate(templateName);
        }
        Collection<?> data = supplier.get();
        try (ExcelWriter writer = write.build()){
            WriteSheet writeSheet = EasyExcel.writerSheet().sheetName("Sheet1").build();
            writer.fill(data,writeSheet);
        }
    }

    public static <T> void read(InputStream stream,Class<T> clazz,ReadListener<T> readListener) {
        EasyExcel.read(stream,clazz,readListener).sheet("Sheet1").headRowNumber(2).doRead();
    }
}
