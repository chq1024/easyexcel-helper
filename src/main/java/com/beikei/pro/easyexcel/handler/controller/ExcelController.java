package com.beikei.pro.easyexcel.handler.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author bk
 */
@RestController
@RequestMapping("/excel")
public class ExcelController {


    @PostMapping("/upload")
    public void upload(@RequestParam("file") MultipartFile file) {

    }

    @PostMapping("/download")
    public void upload() {

    }
}
