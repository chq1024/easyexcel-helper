package com.chuhq.demo.easyexcel.service.impl;

import com.alibaba.excel.read.listener.ReadListener;
import com.alibaba.excel.util.MapUtils;
import com.chuhq.demo.easyexcel.enums.ExcelReadWriteEnum;
import com.chuhq.demo.easyexcel.service.IExcelService;
import com.chuhq.demo.easyexcel.util.ExcelUtil;
import com.chuhq.demo.easyexcel.util.SpringUtil;
import com.fasterxml.jackson.databind.json.JsonMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * @author bk
 */
@Service
public class ExcelServiceImpl implements IExcelService {
    @Override
    public void downloadTemplate(Integer type) {
        export(type, true, ArrayList::new);
    }

    @Override
    public void download(Integer type) {
        ExcelReadWriteEnum readWriteEnum = ExcelReadWriteEnum.valueOf(type);
        String clazzName = readWriteEnum.getClazzName();
        String methodName = readWriteEnum.getDownloadMethod();
        Object bean = SpringUtil.getContext().getBean(clazzName);
        try {
            Method method = bean.getClass().getMethod(methodName);
            Object invoke = method.invoke(bean);
            export(type, false, (Supplier<Collection<?>>) invoke);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public void read(Integer type, MultipartFile file) {
        ExcelReadWriteEnum readWriteEnum = ExcelReadWriteEnum.valueOf(type);
        Class<?> uploadListener = readWriteEnum.getUploadListener();
        try {
            ReadListener readListener = (ReadListener) uploadListener.getDeclaredConstructor(String.class).newInstance(readWriteEnum.getClazzName());
            ExcelUtil.read(file.getInputStream(), readWriteEnum.getEntityClazz(), readListener);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void export(Integer type, boolean template,Supplier<Collection<?>> supplier) {
        ServletRequestAttributes servletRequestAttributes = Objects.requireNonNull((ServletRequestAttributes) RequestContextHolder.getRequestAttributes());
        HttpServletResponse response = Objects.requireNonNull(servletRequestAttributes.getResponse());
        try {
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setCharacterEncoding("utf-8");
            ExcelReadWriteEnum readWriteEnum = ExcelReadWriteEnum.valueOf(type);
            response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + readWriteEnum.getFileName() + (template?"-template":"") + ".xlsx");
            File file = ResourceUtils.getFile("classpath:/excel/" + readWriteEnum.getFileName() + "-template.xlsx");
            response.setHeader("stream","true");
            ExcelUtil.download(response.getOutputStream(), readWriteEnum.getEntityClazz(), supplier, file.getPath());
        } catch (Exception e) {
            response.reset();
            response.setContentType("application/json");
            response.setCharacterEncoding("utf-8");
            Map<String, Object> map = MapUtils.newHashMap();
            map.put("status", 40000);
            map.put("message", "下载文件失败" + e);
            map.put("data",Map.of("code",40000,"message","下载文件失败"));
            try (PrintWriter writer = response.getWriter()) {
                writer.println(new JsonMapper().writeValueAsString(map));
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }
    }
}
