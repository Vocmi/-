package com.vocmi.daijia.customer.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.vocmi.daijia.common.constant.RedisConstant;
import com.vocmi.daijia.common.execption.VocmiException;
import com.vocmi.daijia.common.result.Result;
import com.vocmi.daijia.common.result.ResultCodeEnum;
import com.vocmi.daijia.customer.client.CustomerInfoFeignClient;
import com.vocmi.daijia.customer.service.CustomerService;
import com.vocmi.daijia.model.form.customer.UpdateWxPhoneForm;
import com.vocmi.daijia.model.vo.customer.CustomerLoginVo;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@SuppressWarnings({"unchecked", "rawtypes"})
public class CustomerServiceImpl implements CustomerService {

    //注入远程调用接口
    @Resource
    private CustomerInfoFeignClient client;

    @Resource
    private RedisTemplate redisTemplate;

    @Override
    public String login(String code) {
        Result<Long> loginResult = client.login(code);

        Integer codeResult = loginResult.getCode();
        if(codeResult != 200){
            throw  new VocmiException(ResultCodeEnum.DATA_ERROR);
        }

        Long customerId = loginResult.getData();
        if(customerId == null){
            throw  new VocmiException(ResultCodeEnum.DATA_ERROR);
        }

        String token = UUID.randomUUID().toString().replaceAll("-", "");
        redisTemplate.opsForValue().set(RedisConstant.USER_LOGIN_KEY_PREFIX+token, customerId.toString(), RedisConstant.USER_LOGIN_KEY_TIMEOUT, TimeUnit.SECONDS);

        return token;
    }

    @Override
    public CustomerLoginVo getCustomerLoginInfo(Long customerId) {
        Integer codeResult = client.getCustomerLoginInfo(customerId).getCode();
        if(codeResult != 200){
            throw  new VocmiException(ResultCodeEnum.DATA_ERROR);
        }

        CustomerLoginVo customerLoginVo = client.getCustomerLoginInfo(customerId).getData();
        if (BeanUtil.isEmpty(customerLoginVo)){
            throw  new VocmiException(ResultCodeEnum.DATA_ERROR);
        }

        return customerLoginVo;
    }

    @Override
    public Boolean updateWxPhoneNumber(UpdateWxPhoneForm updateWxPhoneForm) {
        client.updateWxPhoneNumber(updateWxPhoneForm);
        return true;
    }
}
