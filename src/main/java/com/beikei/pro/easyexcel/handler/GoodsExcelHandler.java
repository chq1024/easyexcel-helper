package com.beikei.pro.easyexcel.handler;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.plugins.pagination.PageDTO;
import com.beikei.pro.easyexcel.comment.IExcelHandler;
import com.beikei.pro.easyexcel.handler.mapper.GoodsMapper;
import com.beikei.pro.easyexcel.handler.transform.GoodsExcel;
import com.beikei.pro.easyexcel.util.SpringUtils;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * @author bk
 */
@Getter
@Setter
@Slf4j
public class GoodsExcelHandler implements IExcelHandler<GoodsExcel> {

    private volatile GoodsExcelHandler instance;

    private GoodsMapper goodsMapper;

    private GoodsExcelHandler() {}

    @Override
    public GoodsExcelHandler getInstance() {
        if (instance == null) {
            synchronized (GoodsExcelHandler.class) {
                if (instance == null) {
                    GoodsMapper mapper = SpringUtils.getBean(GoodsMapper.class);
                    instance = new GoodsExcelHandler();
                    instance.setGoodsMapper(mapper);
                    instance.setInstance(this);
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
                } catch (DuplicateKeyException e) {
                    log.warn("重复键异常，Tab:goods_excel,Err:" + e.getMessage());
                }
            }
        };
    }

    @Override
    public Supplier<List<GoodsExcel>> pageQuery(long page, long size, LambdaQueryWrapper<GoodsExcel> queryWrapper,List<OrderItem> orderItems) {
        return ()->{
            Page<GoodsExcel> pageQuery = PageDTO.of(page, size);
            pageQuery.setOrders(Optional.ofNullable(orderItems).orElse(new ArrayList<>()));
            // 外层已有查询总数，本次查询关注数据
            pageQuery.setSearchCount(false);
            pageQuery = goodsMapper.selectPage(pageQuery, queryWrapper);
            return pageQuery.getRecords();
        };
    }


    @Override
    public long count(LambdaQueryWrapper<GoodsExcel> queryWrapper) {
        return goodsMapper.selectCount(queryWrapper);
    }

}
