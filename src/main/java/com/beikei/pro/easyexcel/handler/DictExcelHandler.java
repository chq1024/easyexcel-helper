package com.beikei.pro.easyexcel.handler;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.beikei.pro.easyexcel.comment.Dict;
import com.beikei.pro.easyexcel.comment.IExcelHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * @author bk
 */
@Slf4j
public class DictExcelHandler implements IExcelHandler<Dict> {

    private DictExcelHandler() {}

    private volatile DictExcelHandler handler;

    @Override
    public IExcelHandler<Dict> getInstance() {
        if (handler == null) {
            synchronized (DictExcelHandler.class) {
                if (handler == null) {
                    handler = new DictExcelHandler();
                }
            }
        }
        return handler;
    }

    @Override
    public Consumer<List<Dict>> sync2Db() {
        return (data)->{

        };
    }

    @Override
    public Supplier<List<Dict>> pageQuery(long page, long size, LambdaQueryWrapper<Dict> queryWrapper, List<OrderItem> orderItems) {
        return ()->{


            return null;
        };
    }

    @Override
    public long count(LambdaQueryWrapper<Dict> queryWrapper) {
        return 0;
    }

    private String querySql() {

        return "";
    }
}
