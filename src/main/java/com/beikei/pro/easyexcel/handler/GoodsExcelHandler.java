package com.beikei.pro.easyexcel.handler;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.PageDTO;
import com.beikei.pro.easyexcel.comment.IExcelHandler;
import com.beikei.pro.easyexcel.entity.PageResult;
import com.beikei.pro.easyexcel.handler.mapper.GoodsMapper;
import com.beikei.pro.easyexcel.transform.GoodsExcel;
import com.beikei.pro.easyexcel.util.SpringUtils;
import lombok.Getter;
import lombok.Setter;

import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * @author bk
 */
@Getter
@Setter
public class GoodsExcelHandler implements IExcelHandler<GoodsExcel> {

    private GoodsExcelHandler instance;

    private GoodsMapper goodsMapper;

    private GoodsExcelHandler() {}

    public GoodsExcelHandler getInstance() {
        if (instance == null) {
            synchronized (GoodsExcelHandler.class) {
                if (instance == null) {
                    instance = new GoodsExcelHandler();
                    goodsMapper = SpringUtils.getBean(GoodsMapper.class);
                    instance.setInstance(this);
                    instance.setGoodsMapper(goodsMapper);
                }
            }
        }
        return instance;
    }

    @Override
    public Consumer<List<GoodsExcel>> sync2Db() {
        return (data)->{
            for (GoodsExcel datum : data) {
                try {
                    goodsMapper.insert(datum);
                } catch (Exception e) {
                    // todo 需要处理
                }
            }
        };
    }

    @Override
    public Supplier<List<GoodsExcel>> pageQuery(long page, int size, LambdaQueryWrapper<GoodsExcel> queryWrapper) {
        return ()-> {
            PageDTO<GoodsExcel> queryPage = new PageDTO<>(page,size);
            queryPage = goodsMapper.selectPage(queryPage, queryWrapper);
            return queryPage.getRecords();
        };
    }


    @Override
    public long count(LambdaQueryWrapper<GoodsExcel> queryWrapper) {
        return goodsMapper.selectCount(queryWrapper);
    }

}
