package com.beikei.pro.easyexcel.handler;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.beikei.pro.easyexcel.comment.IExcelHandler;
import com.beikei.pro.easyexcel.entity.PageResult;
import com.beikei.pro.easyexcel.handler.mapper.GoodsMapper;
import com.beikei.pro.easyexcel.transform.GoodsExcel;
import com.beikei.pro.easyexcel.util.SpringUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * @author bk
 */
public class GoodsExcelHandler implements IExcelHandler<GoodsExcel> {

    private GoodsExcelHandler instance;

    private GoodsMapper goodsMapper;

    public GoodsExcelHandler getInstance() {
        if (instance == null) {
            synchronized (GoodsExcelHandler.class) {
                if (instance == null) {
                    instance = new GoodsExcelHandler();
                    goodsMapper = SpringUtils.getBean(GoodsMapper.class);
                }
            }
        }
        return instance;
    }

    @Override
    public Consumer<List<GoodsExcel>> sync2Db() {
        return (data)->{
            for (GoodsExcel datum : data) {
                goodsMapper.insert(datum);
            }
        };
    }

    @Override
    public Supplier<PageResult<GoodsExcel>> pageQuery(long page, int size, LambdaQueryWrapper<GoodsExcel> queryWrapper) {
        return ()-> {
            PageResult<GoodsExcel> result = new PageResult<>();
            result.setPage(page);
            result.setSize(size);
            result.setData(goodsMapper.selectList(queryWrapper));
//            result.setCount(goodsMapper.selectCount(queryWrapper));
            return result;
        };
    }


    @Override
    public long count(LambdaQueryWrapper<GoodsExcel> queryWrapper) {
        return goodsMapper.selectCount(queryWrapper);
    }

}
