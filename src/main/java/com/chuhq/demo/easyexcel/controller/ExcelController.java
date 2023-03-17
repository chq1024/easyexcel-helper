package com.chuhq.demo.easyexcel.controller;

import com.chuhq.demo.easyexcel.service.IExcelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author bk
 */
@RestController
@RequestMapping("/excel")
public class ExcelController {

    @Autowired
    private IExcelService excelService;

    @GetMapping("/download/{type}")
    public void downloadTemplate(@PathVariable("type") Integer type) {
        excelService.downloadTemplate(type);
    }

    @GetMapping("/download/file/{type}")
    public void download(@PathVariable("type") Integer type) {
        excelService.download(type);
    }

    @PostMapping("/upload/file/{type}")
    public String upload(@PathVariable("type") Integer type, @RequestParam("file") MultipartFile file) {
        excelService.read(type,file);
        return "success";
    }
}
