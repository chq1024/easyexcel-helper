package com.beikei.pro.easyexcel.handler.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.beikei.pro.easyexcel.handler.transform.GoodsExcel;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface GoodsMapper extends BaseMapper<GoodsExcel> {
}
