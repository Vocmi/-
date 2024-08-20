package com.vocmi.daijia.driver.service;

import com.vocmi.daijia.model.form.driver.DriverFaceModelForm;
import com.vocmi.daijia.model.form.driver.UpdateDriverAuthInfoForm;
import com.vocmi.daijia.model.vo.driver.DriverAuthInfoVo;
import com.vocmi.daijia.model.vo.driver.DriverLoginVo;

public interface DriverService {


    String login(String code);

    DriverLoginVo getDriverLoginInfo(Long driverId);

    Boolean updateDriverAuthInfo(UpdateDriverAuthInfoForm updateDriverAuthInfoForm);

    DriverAuthInfoVo getDriverAuthInfo(Long driverId);

    Boolean creatDriverFaceModel(DriverFaceModelForm driverFaceModelForm);

    Boolean isFaceRecognition(Long driverId);

    Boolean verifyDriverFace(DriverFaceModelForm driverFaceModelForm);

    Boolean startService(Long driverId);

    Boolean stopService(Long driverId);
}
