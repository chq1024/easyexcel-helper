package com.beikei.pro.easyexcel.enums;

import com.beikei.pro.easyexcel.handler.GoodsExcelHandler;
import com.beikei.pro.easyexcel.handler.GoodsReadListener;
import com.beikei.pro.easyexcel.handler.transform.GoodsExcel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * 用枚举的表示方法来实现抽象
 * @author bk
 */
@Getter
@AllArgsConstructor
public enum ExcelEnum {

    GOODS("goods", GoodsExcel.class, GoodsReadListener.class, GoodsExcelHandler.class),
    ;

    private final String uniqueName;
    private final Class<?> transform;
    private final Class<?> listener;
    private final Class<?> handler;


    public static ExcelEnum valueOfUniqueName(String uniqueName) {
        return Arrays.stream(values()).filter(r -> {
            return r.getUniqueName().equals(uniqueName);
        }).findAny().orElseThrow(() -> new RuntimeException("not find match enum!"));
    }
}
