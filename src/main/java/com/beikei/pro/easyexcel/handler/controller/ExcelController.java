package com.beikei.pro.easyexcel.handler.controller;

import com.beikei.pro.easyexcel.comment.Dict;
import com.beikei.pro.easyexcel.comment.ExcelHandler;
import com.beikei.pro.easyexcel.handler.JdbcTemplateDbHelper;
import com.beikei.pro.easyexcel.util.ExcelHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Random;

/**
 * @author bk
 */
@RestController
@RequestMapping("/excel")
public class ExcelController {

    @Autowired
    private JdbcTemplateDbHelper dbHelper;


    @PostMapping("/upload")
    public void upload(@RequestParam("file") MultipartFile file) {

    }

    @PostMapping("/download")
    public void upload() {
        try {
            Random random = new Random();
            Path filepath = Files.createTempFile(Path.of("D:\\data\\excel"), "goods_" + random.nextInt(), ".xlsx");
            Dict queryWrapper = new Dict();
            queryWrapper.put("gid","lt",50);
            queryWrapper.put("non_tb","goods_excel");
            ExcelHelper.write(filepath.toFile(),"goods_excel",dbHelper,queryWrapper,null);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
