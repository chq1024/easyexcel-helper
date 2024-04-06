package com.beikei.pro.easyexcel.handler.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.beikei.pro.easyexcel.transform.GoodsExcel;
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
            LambdaQueryWrapper<GoodsExcel> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.le(GoodsExcel::getId,50);
            ExcelHelper.write(filepath.toFile(),"goods",queryWrapper);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
