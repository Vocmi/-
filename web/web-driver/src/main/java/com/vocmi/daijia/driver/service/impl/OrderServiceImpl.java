package com.vocmi.daijia.driver.service.impl;

import com.vocmi.daijia.dispatch.client.NewOrderFeignClient;
import com.vocmi.daijia.driver.service.OrderService;
import com.vocmi.daijia.model.vo.order.NewOrderDataVo;
import com.vocmi.daijia.order.client.OrderInfoFeignClient;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@SuppressWarnings({"unchecked", "rawtypes"})
public class OrderServiceImpl implements OrderService {

    @Resource
    private OrderInfoFeignClient orderInfoFeignClient;

    @Resource
    private NewOrderFeignClient newOrderFeignClient;

    @Override
    public Integer getOrderStatus(Long orderId) {
        return orderInfoFeignClient.getOrderStatus(orderId).getData();
    }

    @Override
    public List<NewOrderDataVo> findNewOrderQueueData(Long driverId) {
        return newOrderFeignClient.findNewOrderQueueData(driverId).getData();
    }
}
