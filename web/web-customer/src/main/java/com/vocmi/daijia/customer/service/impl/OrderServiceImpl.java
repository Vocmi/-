package com.vocmi.daijia.customer.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.vocmi.daijia.customer.service.OrderService;
import com.vocmi.daijia.dispatch.client.NewOrderFeignClient;
import com.vocmi.daijia.map.client.MapFeignClient;
import com.vocmi.daijia.model.form.customer.ExpectOrderForm;
import com.vocmi.daijia.model.form.customer.SubmitOrderForm;
import com.vocmi.daijia.model.form.map.CalculateDrivingLineForm;
import com.vocmi.daijia.model.form.order.OrderInfoForm;
import com.vocmi.daijia.model.form.rules.FeeRuleRequestForm;
import com.vocmi.daijia.model.vo.customer.ExpectOrderVo;
import com.vocmi.daijia.model.vo.dispatch.NewOrderTaskVo;
import com.vocmi.daijia.model.vo.map.DrivingLineVo;
import com.vocmi.daijia.model.vo.order.NewOrderDataVo;
import com.vocmi.daijia.model.vo.rules.FeeRuleResponseVo;
import com.vocmi.daijia.order.client.OrderInfoFeignClient;
import com.vocmi.daijia.rules.client.FeeRuleFeignClient;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.Date;

@Slf4j
@Service
@SuppressWarnings({"unchecked", "rawtypes"})
public class OrderServiceImpl implements OrderService {

    @Resource
    private MapFeignClient mapFeignClient;

    @Resource
    private FeeRuleFeignClient feeRuleFeignClient;

    @Resource
    private OrderInfoFeignClient orderInfoFeignClient;

    @Resource
    private NewOrderFeignClient newOrderFeignClient;

    @Override
    public ExpectOrderVo expectOrder(ExpectOrderForm expectOrderForm) {
        //计算驾驶线路
        CalculateDrivingLineForm calculateDrivingLineForm = BeanUtil.copyProperties(expectOrderForm, CalculateDrivingLineForm.class);
        DrivingLineVo drivingLineVo = mapFeignClient.calculateDrivingLine(calculateDrivingLineForm).getData();

        //计算订单费用
        FeeRuleRequestForm calculateOrderFeeForm = new FeeRuleRequestForm();
        calculateOrderFeeForm.setDistance(drivingLineVo.getDistance());
        calculateOrderFeeForm.setStartTime(new Date());
        calculateOrderFeeForm.setWaitMinute(0);
        FeeRuleResponseVo feeRuleResponseVo = feeRuleFeignClient.calculateOrderFee(calculateOrderFeeForm).getData();

        //预估订单实体
        ExpectOrderVo expectOrderVo = new ExpectOrderVo();
        expectOrderVo.setDrivingLineVo(drivingLineVo);
        expectOrderVo.setFeeRuleResponseVo(feeRuleResponseVo);
        return expectOrderVo;
    }

    @Override
    public Long submitOrder(SubmitOrderForm submitOrderForm) {
        //1.重新计算驾驶线路
        CalculateDrivingLineForm calculateDrivingLineForm = new CalculateDrivingLineForm();
        BeanUtils.copyProperties(submitOrderForm, calculateDrivingLineForm);
        DrivingLineVo drivingLineVo = mapFeignClient.calculateDrivingLine(calculateDrivingLineForm).getData();

        //2.重新计算订单费用
        FeeRuleRequestForm calculateOrderFeeForm = new FeeRuleRequestForm();
        calculateOrderFeeForm.setDistance(drivingLineVo.getDistance());
        calculateOrderFeeForm.setStartTime(new Date());
        calculateOrderFeeForm.setWaitMinute(0);
        FeeRuleResponseVo feeRuleResponseVo = feeRuleFeignClient.calculateOrderFee(calculateOrderFeeForm).getData();

        //3.封装订单信息对象
        OrderInfoForm orderInfoForm = new OrderInfoForm();
        //订单位置信息
        BeanUtils.copyProperties(submitOrderForm, orderInfoForm);
        //预估里程
        orderInfoForm.setExpectDistance(drivingLineVo.getDistance());
        orderInfoForm.setExpectAmount(feeRuleResponseVo.getTotalAmount());

        //4.保存订单信息
        Long orderId = orderInfoFeignClient.saveOrderInfo(orderInfoForm).getData();

        //启动任务调度,查询附近可以接单的司机
        NewOrderTaskVo newOrderTaskVo = BeanUtil.copyProperties(orderInfoForm, NewOrderTaskVo.class);
        newOrderTaskVo.setOrderId(orderId);
        newOrderTaskVo.setExpectTime(drivingLineVo.getDuration());
        newOrderTaskVo.setCreateTime(new Date());
        newOrderFeignClient.addAndStartTask(newOrderTaskVo);
        return orderId;
    }

    @Override
    public Integer getOrderStatus(Long orderId) {
        return orderInfoFeignClient.getOrderStatus(orderId).getData();
    }
}
