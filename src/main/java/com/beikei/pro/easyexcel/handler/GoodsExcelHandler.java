package com.beikei.pro.easyexcel.handler;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.beikei.pro.easyexcel.comment.IExcelHandler;
import com.beikei.pro.easyexcel.entity.PageResult;
import com.beikei.pro.easyexcel.transform.GoodsExcel;

import java.util.List;
import java.util.function.Supplier;

/**
 * @author bk
 */
public class GoodsExcelHandler implements IExcelHandler<GoodsExcel> {

    @Override
    public boolean async(List<GoodsExcel> batch) {

        return false;
    }

    @Override
    public Supplier<PageResult<GoodsExcel>> pageQuery(long page, int size, LambdaQueryWrapper<GoodsExcel> queryWrapper) {
        return ()-> null;
    }


    @Override
    public long count(LambdaQueryWrapper<GoodsExcel> queryWrapper) {
        return 0L;
    }

}
