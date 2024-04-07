package com.beikei.pro.easyexcel.handler.transform;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * @author bk
 */
@Getter
@Setter
public class GoodsExcel implements Serializable {

    @TableId(type = IdType.AUTO)
    @ExcelIgnore
    private Long id;
    @ExcelProperty(value = "gid")
    private String gid;
    @ExcelProperty(value = "name")
    private String name;
}
