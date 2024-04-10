package com.beikei.pro.easyexcel.comment;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 不限制orm，可集成重写
 * 1. 读取表结构
 * 2. 解析表结构
 * 服务于excel表头和查询后数据的转化
 * @author bk
 */
@Getter
@Setter
public abstract class DbHelper {

    private Dict schema;

    private String datumNon;

    public abstract void insert2db(List<Dict> data);

    public abstract long count(Dict queryWrapper);

    public abstract List<Dict> batchQuery(long page, int size, Dict queryWrapper, Dict orderItems);

    public abstract Dict loadSchema(String schema);

}

