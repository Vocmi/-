package com.vocmi.daijia.driver.client;

import com.vocmi.daijia.common.result.Result;
import com.vocmi.daijia.model.vo.driver.DriverLoginVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(value = "service-driver")
public interface DriverInfoFeignClient {
    @GetMapping("/driver/info/login/{code}")
    Result<Long> login(@PathVariable("code") String code);

    @GetMapping("/driver/info/getDriverLoginInfo/{driverId}")
    Result<DriverLoginVo> getDriverLoginInfo(@PathVariable("driverId") Long driverId);
}