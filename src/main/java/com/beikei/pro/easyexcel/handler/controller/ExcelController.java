package com.beikei.pro.easyexcel.handler.controller;

import com.beikei.pro.easyexcel.util.ExcelHelper;
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


    @PostMapping("/upload")
    public void upload(@RequestParam("file") MultipartFile file) {
        ExcelHelper.read(file,"goods");
    }

    @PostMapping("/download")
    public void upload() {
        try {
            Random random = new Random();
            Path filepath = Files.createTempFile(Path.of("D:\\data\\excel"), "goods_" + random.nextInt(), ".xlsx");
            ExcelHelper.write2Sheets(10,filepath.toFile(),"goods",null,null);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
