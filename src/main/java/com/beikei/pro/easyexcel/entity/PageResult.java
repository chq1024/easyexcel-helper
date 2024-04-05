package com.beikei.pro.easyexcel.entity;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author bk
 */
@Getter
@Setter
public class PageResult<T> {

    private Long page;
    private Integer size;
    private Long count;
    private List<T> data;
    private Long pageCount;
}
