package com.vocmi.daijia.order.service;

import com.vocmi.daijia.model.entity.order.OrderInfo;
import com.baomidou.mybatisplus.extension.service.IService;
import com.vocmi.daijia.model.form.order.OrderInfoForm;

public interface OrderInfoService extends IService<OrderInfo> {

    Long saveOrderInfo(OrderInfoForm orderInfoForm);

    Integer getOrderStatus(Long orderId);
}
