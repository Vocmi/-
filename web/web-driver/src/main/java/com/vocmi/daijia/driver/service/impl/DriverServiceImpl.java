package com.vocmi.daijia.driver.service.impl;

import com.vocmi.daijia.common.constant.RedisConstant;
import com.vocmi.daijia.driver.client.DriverInfoFeignClient;
import com.vocmi.daijia.driver.service.DriverService;
import com.vocmi.daijia.model.form.driver.DriverFaceModelForm;
import com.vocmi.daijia.model.form.driver.UpdateDriverAuthInfoForm;
import com.vocmi.daijia.model.vo.driver.DriverAuthInfoVo;
import com.vocmi.daijia.model.vo.driver.DriverLoginVo;
import jakarta.annotation.Resource;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@SuppressWarnings({"unchecked", "rawtypes"})
public class DriverServiceImpl implements DriverService {

    @Resource
    private DriverInfoFeignClient driverInfoFeignClient;

    @Resource
    private RedisTemplate redisTemplate;

    @SneakyThrows
    @Override
    public String login(String code) {
        //获取openId
        Long driverId = driverInfoFeignClient.login(code).getData();

        String token = UUID.randomUUID().toString().replaceAll("-", "");
        redisTemplate.opsForValue().set(RedisConstant.USER_LOGIN_KEY_PREFIX + token, driverId.toString(), RedisConstant.USER_LOGIN_KEY_TIMEOUT, TimeUnit.SECONDS);
        return token;
    }

    @Override
    public DriverLoginVo getDriverLoginInfo(Long driverId) {
        return driverInfoFeignClient.getDriverLoginInfo(driverId).getData();
    }

    @Override
    public Boolean updateDriverAuthInfo(UpdateDriverAuthInfoForm updateDriverAuthInfoForm) {
        return driverInfoFeignClient.UpdateDriverAuthInfo(updateDriverAuthInfoForm).getData();
    }

    @Override
    public DriverAuthInfoVo getDriverAuthInfo(Long driverId) {
        return driverInfoFeignClient.getDriverAuthInfo(driverId).getData();
    }

    @Override
    public Boolean creatDriverFaceModel(DriverFaceModelForm driverFaceModelForm) {
        return driverInfoFeignClient.creatDriverFaceModel(driverFaceModelForm).getData();
    }
}
