package com.vocmi.daijia.driver.service;

import com.vocmi.daijia.model.form.map.CalculateDrivingLineForm;
import com.vocmi.daijia.model.form.order.UpdateOrderCartForm;
import com.vocmi.daijia.model.vo.driver.DriverInfoVo;
import com.vocmi.daijia.model.vo.map.DrivingLineVo;
import com.vocmi.daijia.model.vo.order.CurrentOrderInfoVo;
import com.vocmi.daijia.model.vo.order.NewOrderDataVo;
import com.vocmi.daijia.model.vo.order.OrderInfoVo;

import java.util.List;

public interface OrderService {


    Integer getOrderStatus(Long orderId);

    List<NewOrderDataVo> findNewOrderQueueData(Long driverId);

    Boolean robNewOrder(Long driverId, Long orderId);

    CurrentOrderInfoVo searchCustomerCurrentOrder(Long customerId);

    CurrentOrderInfoVo searchDriverCurrentOrder(Long driverId);

    OrderInfoVo getOrderInfo(Long orderId, Long driverId);

    DrivingLineVo calculateDrivingLine(CalculateDrivingLineForm calculateDrivingLineForm);

    Boolean driverArriveStartLocation(Long orderId, Long driverId);

    Boolean updateOrderCart(UpdateOrderCartForm updateOrderCartForm);
}
