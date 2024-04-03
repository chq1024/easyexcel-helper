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

    private Integer page;
    private Integer size;
    private Integer count;
    private List<T> data;
}
