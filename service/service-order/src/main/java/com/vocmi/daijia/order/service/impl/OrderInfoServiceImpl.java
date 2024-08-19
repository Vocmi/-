package com.vocmi.daijia.order.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.vocmi.daijia.common.constant.RedisConstant;
import com.vocmi.daijia.model.entity.order.OrderInfo;
import com.vocmi.daijia.model.entity.order.OrderStatusLog;
import com.vocmi.daijia.model.enums.OrderStatus;
import com.vocmi.daijia.model.form.order.OrderInfoForm;
import com.vocmi.daijia.order.mapper.OrderInfoMapper;
import com.vocmi.daijia.order.mapper.OrderStatusLogMapper;
import com.vocmi.daijia.order.service.OrderInfoService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@SuppressWarnings({"unchecked", "rawtypes"})
public class OrderInfoServiceImpl extends ServiceImpl<OrderInfoMapper, OrderInfo> implements OrderInfoService {
    @Resource
    private OrderInfoMapper orderInfoMapper;

    @Resource
    private OrderStatusLogMapper orderStatusLogMapper;

    @Resource
    private RedisTemplate redisTemplate;

    @Override
    public Long saveOrderInfo(OrderInfoForm orderInfoForm) {
        OrderInfo orderInfo = BeanUtil.copyProperties(orderInfoForm, OrderInfo.class);
        String orderNo = UUID.randomUUID().toString().replaceAll("-", "");
        orderInfo.setStatus(OrderStatus.WAITING_ACCEPT.getStatus());
        orderInfo.setOrderNo(orderNo);
        orderInfoMapper.insert(orderInfo);

        //记录日志
        this.log(orderInfo.getId(), orderInfo.getStatus());

        //查找附近可以接单的司机


        return orderInfo.getId();
    }

    @Override
    public Integer getOrderStatus(Long orderId) {
        LambdaQueryWrapper<OrderInfo> queryWrapper = new LambdaQueryWrapper<>();
        OrderInfo orderInfo = orderInfoMapper.selectOne(queryWrapper.eq(OrderInfo::getId, orderId).select(OrderInfo::getStatus));
        if (BeanUtil.isEmpty(orderInfo)) {
            return OrderStatus.NULL_ORDER.getStatus();
        }
        return orderInfo.getStatus();
    }

    public void log(Long orderId, Integer status) {
        OrderStatusLog orderStatusLog = new OrderStatusLog();
        orderStatusLog.setOrderId(orderId);
        orderStatusLog.setOrderStatus(status);
        orderStatusLog.setOperateTime(new Date());
        orderStatusLogMapper.insert(orderStatusLog);
    }
}
