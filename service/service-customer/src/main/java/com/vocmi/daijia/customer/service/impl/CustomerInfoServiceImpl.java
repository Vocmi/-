package com.vocmi.daijia.customer.service.impl;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.bean.WxMaJscode2SessionResult;
import com.vocmi.daijia.customer.mapper.CustomerInfoMapper;
import com.vocmi.daijia.customer.service.CustomerInfoService;
import com.vocmi.daijia.model.entity.customer.CustomerInfo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.error.WxErrorException;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@SuppressWarnings({"unchecked", "rawtypes"})
public class CustomerInfoServiceImpl extends ServiceImpl<CustomerInfoMapper, CustomerInfo> implements CustomerInfoService {

    @Resource
    private WxMaService wxMaService;

    @Resource
    private CustomerInfoMapper customerInfoMapper;

    @Override
    public Long login(String code) {
        String openid = null;

        try {
            WxMaJscode2SessionResult sessionInfo = wxMaService.getUserService().getSessionInfo(code);
            openid = sessionInfo.getOpenid();
        } catch (WxErrorException e) {
            throw new RuntimeException(e);
        }

        LambdaQueryWrapper<CustomerInfo> customerInfoLambdaQueryWrapper = new LambdaQueryWrapper<>();
        CustomerInfo customerInfo = customerInfoMapper.selectOne(customerInfoLambdaQueryWrapper.eq(CustomerInfo::getWxOpenId, openid));

        if (customerInfo == null){

        }

        return null;
    }
}
