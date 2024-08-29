package com.vocmi.daijia.customer.service.impl;

import com.vocmi.daijia.coupon.client.CouponFeignClient;
import com.vocmi.daijia.customer.service.CouponService;
import com.vocmi.daijia.model.vo.base.PageVo;
import com.vocmi.daijia.model.vo.coupon.AvailableCouponVo;
import com.vocmi.daijia.model.vo.coupon.NoReceiveCouponVo;
import com.vocmi.daijia.model.vo.coupon.NoUseCouponVo;
import com.vocmi.daijia.model.vo.coupon.UsedCouponVo;
import com.vocmi.daijia.model.vo.order.OrderBillVo;
import com.vocmi.daijia.order.client.OrderInfoFeignClient;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@SuppressWarnings({"unchecked", "rawtypes"})
public class CouponServiceImpl implements CouponService {

    @Resource
    private CouponFeignClient couponFeignClient;

    @Resource
    private OrderInfoFeignClient orderInfoFeignClient;

    @Override
    public PageVo<NoReceiveCouponVo> findNoReceivePage(Long customerId, Long page, Long limit) {
        return couponFeignClient.findNoReceivePage(customerId, page, limit).getData();
    }

    @Override
    public PageVo<NoUseCouponVo> findNoUsePage(Long customerId, Long page, Long limit) {
        return couponFeignClient.findNoUsePage(customerId, page, limit).getData();
    }

    @Override
    public PageVo<UsedCouponVo> findUsedPage(Long customerId, Long page, Long limit) {
        return couponFeignClient.findUsedPage(customerId, page, limit).getData();
    }

    @Override
    public Boolean receive(Long customerId, Long couponId) {
        return couponFeignClient.receive(customerId, couponId).getData();
    }

    @Override
    public List<AvailableCouponVo> findAvailableCoupon(Long customerId, Long orderId) {
        OrderBillVo orderBillVo = orderInfoFeignClient.getOrderBillInfo(orderId).getData();
        return couponFeignClient.findAvailableCoupon(customerId, orderBillVo.getPayAmount()).getData();
    }
}
