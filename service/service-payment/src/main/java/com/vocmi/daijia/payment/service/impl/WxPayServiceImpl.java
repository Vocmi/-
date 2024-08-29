package com.vocmi.daijia.payment.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.UUID;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.vocmi.daijia.common.constant.MqConst;
import com.vocmi.daijia.common.service.RabbitService;
import com.vocmi.daijia.driver.client.DriverAccountFeignClient;
import com.vocmi.daijia.model.entity.payment.PaymentInfo;
import com.vocmi.daijia.model.enums.TradeType;
import com.vocmi.daijia.model.form.driver.TransferForm;
import com.vocmi.daijia.model.form.payment.PaymentInfoForm;
import com.vocmi.daijia.model.vo.order.OrderRewardVo;
import com.vocmi.daijia.model.vo.payment.WxPrepayVo;
import com.vocmi.daijia.order.client.OrderInfoFeignClient;
import com.vocmi.daijia.payment.mapper.PaymentInfoMapper;
import com.vocmi.daijia.payment.service.WxPayService;
import io.seata.spring.annotation.GlobalTransactional;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.transaction.Transaction;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@Slf4j
public class WxPayServiceImpl implements WxPayService {

    @Resource
    private PaymentInfoMapper paymentInfoMapper;

    @Resource
    private RabbitService rabbitService;

    @Resource
    private OrderInfoFeignClient orderInfoFeignClient;

    @Resource
    private DriverAccountFeignClient driverAccountFeignClient;

    @Override
    public WxPrepayVo createWxPayment(PaymentInfoForm paymentInfoForm) {
        // TODO 这里模拟微信支付更能，只对数据库表进行更新
        PaymentInfo paymentInfo = paymentInfoMapper.selectOne(new LambdaQueryWrapper<PaymentInfo>().eq(PaymentInfo::getOrderNo, paymentInfoForm.getOrderNo()));
        if (BeanUtil.isEmpty(paymentInfo)) {
            paymentInfo = new PaymentInfo();
            BeanUtils.copyProperties(paymentInfoForm, paymentInfo);
            paymentInfo.setPaymentStatus(0);
            paymentInfo.setOrderNo(UUID.randomUUID().toString());
            paymentInfoMapper.insert(paymentInfo);
        }

        //直接调用，模拟订单成功
        handlePayment(paymentInfo);
        return null;
    }

    @Override
    public Boolean queryPayStatus(String orderNo) {
        return true;
    }

    @Override
    @GlobalTransactional
    public void handleOrder(String orderNo) {
        //1 远程调用：更新订单状态：已经支付
        orderInfoFeignClient.updateOrderPayStatus(orderNo);

        //2 远程调用：获取系统奖励，打入到司机账户
        OrderRewardVo orderRewardVo = orderInfoFeignClient.getOrderRewardFee(orderNo).getData();
        if(orderRewardVo != null && orderRewardVo.getRewardFee().doubleValue()>0) {
            TransferForm transferForm = new TransferForm();
            transferForm.setTradeNo(orderNo);
            transferForm.setTradeType(TradeType.REWARD.getType());
            transferForm.setContent(TradeType.REWARD.getContent());
            transferForm.setAmount(orderRewardVo.getRewardFee());
            transferForm.setDriverId(orderRewardVo.getDriverId());
            driverAccountFeignClient.transfer(transferForm);
        }

        //3 TODO 其他
    }

    // TODO 如果支付成功，调用其他方法实现支付后处理逻辑
    public void handlePayment(PaymentInfo transaction) {

        //1 更新支付记录，状态修改为 已经支付
        //订单编号
        String orderNo = transaction.getOrderNo();
        //根据订单编号查询支付记录
        LambdaQueryWrapper<PaymentInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PaymentInfo::getOrderNo,orderNo);
        PaymentInfo paymentInfo = paymentInfoMapper.selectOne(wrapper);
        //如果已经支付，不需要更新
        if(paymentInfo.getPaymentStatus() == 1) {
            return;
        }
        paymentInfo.setPaymentStatus(1);
        paymentInfo.setOrderNo(transaction.getOrderNo());
        paymentInfo.setTransactionId(transaction.getTransactionId());
        paymentInfo.setCallbackTime(new Date());
        paymentInfo.setCallbackContent(JSON.toJSONString(transaction));
        paymentInfoMapper.updateById(paymentInfo);

        //2 发送端：发送mq消息，传递 订单编号
        //  接收端：获取订单编号，完成后续处理
        rabbitService.sendMessage(MqConst.EXCHANGE_ORDER,
                MqConst.ROUTING_PAY_SUCCESS,
                orderNo);
    }
}
