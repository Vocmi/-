package com.vocmi.daijia.driver.service;

import com.vocmi.daijia.model.entity.driver.DriverInfo;
import com.baomidou.mybatisplus.extension.service.IService;
import com.vocmi.daijia.model.vo.driver.DriverLoginVo;

public interface DriverInfoService extends IService<DriverInfo> {

    Long login(String code);

    DriverLoginVo getDriverLoginInfo(Long driverId);
}
