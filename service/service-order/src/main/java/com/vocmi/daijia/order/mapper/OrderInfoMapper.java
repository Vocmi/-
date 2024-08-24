package com.vocmi.daijia.order.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.vocmi.daijia.model.entity.order.OrderInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.vocmi.daijia.model.vo.order.OrderListVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface OrderInfoMapper extends BaseMapper<OrderInfo> {

    IPage<OrderListVo> selectCustomerOrderPage(@Param("pageParam") Page<OrderInfo> pageParam, @Param("customerId") Long customerId);

    IPage<OrderListVo> selectDriverOrderPage(Page<OrderInfo> pageParam, Long driverId);
}
