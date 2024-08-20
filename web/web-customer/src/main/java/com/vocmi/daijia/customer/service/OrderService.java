package com.vocmi.daijia.customer.service;

import com.vocmi.daijia.model.form.customer.ExpectOrderForm;
import com.vocmi.daijia.model.form.customer.SubmitOrderForm;
import com.vocmi.daijia.model.vo.customer.ExpectOrderVo;
import com.vocmi.daijia.model.vo.order.OrderInfoVo;

public interface OrderService {

    ExpectOrderVo expectOrder(ExpectOrderForm expectOrderForm);

    Long submitOrder(SubmitOrderForm submitOrderForm);

    Integer getOrderStatus(Long orderId);

    OrderInfoVo getOrderInfo(Long orderId, Long customerId);
}
