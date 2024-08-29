package com.vocmi.daijia.customer.service;

import com.vocmi.daijia.model.vo.base.PageVo;
import com.vocmi.daijia.model.vo.coupon.AvailableCouponVo;
import com.vocmi.daijia.model.vo.coupon.NoReceiveCouponVo;
import com.vocmi.daijia.model.vo.coupon.NoUseCouponVo;
import com.vocmi.daijia.model.vo.coupon.UsedCouponVo;

import java.util.List;

public interface CouponService  {


    PageVo<NoReceiveCouponVo> findNoReceivePage(Long customerId, Long page, Long limit);

    PageVo<NoUseCouponVo> findNoUsePage(Long customerId, Long page, Long limit);

    PageVo<UsedCouponVo> findUsedPage(Long customerId, Long page, Long limit);

    Boolean receive(Long customerId, Long couponId);

    List<AvailableCouponVo> findAvailableCoupon(Long customerId, Long orderId);
}
