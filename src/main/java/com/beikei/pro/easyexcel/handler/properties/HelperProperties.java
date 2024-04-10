package com.beikei.pro.easyexcel.handler.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author bk
 */
@Configuration
@ConfigurationProperties(prefix = "helper")
@Getter
@Setter
public class HelperProperties {

    /**
     * 本次需要操作的数据库名
     */
    private String dbName;

    /**
     * 忽略前缀，即使用在对数据读取和写入时的忽略
     */
    private String ignorePrefix = "non_";

    /**
     * 单sheet的容纳的最大大小
     */
    private Integer sheetMaxSize = 100;

    /**
     * 每次执行写入操作时,写入的最大大小,超出该大小后需要进行分批写入
     */
    private Integer batchWriteMaxSize = 20;

    /**
     * 读取excel数据并写入数据库时的批次最大大小，超过该大小需要分批写入
     */
    private Integer batchSyncMaxSize = 10;

    /**
     * 是否开启excel数据结构与db数据结构不相同时出现中途中断
     */
    private Boolean enableCheck = true;
}
