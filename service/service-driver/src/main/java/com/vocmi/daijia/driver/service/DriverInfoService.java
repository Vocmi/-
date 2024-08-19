package com.vocmi.daijia.driver.service;

import com.vocmi.daijia.model.entity.driver.DriverInfo;
import com.baomidou.mybatisplus.extension.service.IService;
import com.vocmi.daijia.model.entity.driver.DriverSet;
import com.vocmi.daijia.model.form.driver.DriverFaceModelForm;
import com.vocmi.daijia.model.form.driver.UpdateDriverAuthInfoForm;
import com.vocmi.daijia.model.vo.driver.DriverAuthInfoVo;
import com.vocmi.daijia.model.vo.driver.DriverLoginVo;

public interface DriverInfoService extends IService<DriverInfo> {

    Long login(String code);

    DriverLoginVo getDriverLoginInfo(Long driverId);

    Boolean updateDriverAuthInfo(UpdateDriverAuthInfoForm updateDriverAuthInfoForm);

    DriverAuthInfoVo getDriverAuthInfo(Long driverId);

    Boolean creatDriverFaceModel(DriverFaceModelForm driverFaceModelForm);

    DriverSet getDriverSet(Long driverId);
}
