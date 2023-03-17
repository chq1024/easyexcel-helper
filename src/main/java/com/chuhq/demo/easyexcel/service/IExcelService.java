package com.chuhq.demo.easyexcel.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * @author bk
 */
public interface IExcelService {

    /**
     * 下载模板
     * @param type
     */
    void downloadTemplate(Integer type);

    /**
     * 导出文件
     * @param type
     */
    void download(Integer type);

    /**
     * 导入文件
     * @param type
     */
    void read(Integer type, MultipartFile file);
}
