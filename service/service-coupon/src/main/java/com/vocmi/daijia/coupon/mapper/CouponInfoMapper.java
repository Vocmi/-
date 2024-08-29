package com.vocmi.daijia.coupon.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.vocmi.daijia.model.entity.coupon.CouponInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.vocmi.daijia.model.vo.coupon.NoReceiveCouponVo;
import com.vocmi.daijia.model.vo.coupon.NoUseCouponVo;
import com.vocmi.daijia.model.vo.coupon.UsedCouponVo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CouponInfoMapper extends BaseMapper<CouponInfo> {

    int updateReceiveCount(Long couponId);

    IPage<NoReceiveCouponVo> findNoReceivePage(Page<CouponInfo> pageParam, Long customerId);

    IPage<NoUseCouponVo> findNoUsePage(Page<CouponInfo> pageParam, Long customerId);

    IPage<UsedCouponVo> findUsedPage(Page<CouponInfo> pageParam, Long customerId);

    List<NoUseCouponVo> findNoUseList(Long customerId);

    int updateUseCount(Long id);
}
