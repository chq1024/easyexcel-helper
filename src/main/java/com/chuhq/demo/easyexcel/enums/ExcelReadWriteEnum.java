package com.chuhq.demo.easyexcel.enums;

import com.chuhq.demo.easyexcel.entity.GoodsExcel;
import com.chuhq.demo.easyexcel.entity.StoreExcel;
import com.chuhq.demo.easyexcel.listener.GoodsReadListener;
import com.chuhq.demo.easyexcel.listener.StoreReadListener;
import lombok.Getter;

/**
 * @author bk
 */
@Getter
public enum ExcelReadWriteEnum {

    STORE(1,"store", StoreExcel.class,"storeServiceImpl","download", StoreReadListener.class),
    STORE_GOODS(2,"goods", GoodsExcel.class,"storeGoodsServiceImpl","download", GoodsReadListener.class),
    ;

    private Integer code;
    private String fileName;
    private String clazzName;
    private String downloadMethod;
    private Class<?> uploadListener;
    private Class<?> entityClazz;

    ExcelReadWriteEnum(Integer code, String fileName, Class<?> entityClazz, String clazzName, String downloadMethod, Class<?> uploadListener) {
        this.code = code;
        this.fileName = fileName;
        this.clazzName = clazzName;
        this.downloadMethod = downloadMethod;
        this.uploadListener = uploadListener;
        this.entityClazz = entityClazz;
    }


    public static ExcelReadWriteEnum valueOf(Integer code) {
        for (ExcelReadWriteEnum value : values()) {
            if (code.equals(value.getCode())) {
                return value;
            }
        }
        throw new RuntimeException("not match enum code");
    }
}
