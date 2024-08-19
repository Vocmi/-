package com.vocmi.daijia.driver.service.impl;

import com.vocmi.daijia.common.execption.VocmiException;
import com.vocmi.daijia.common.result.ResultCodeEnum;
import com.vocmi.daijia.driver.client.DriverInfoFeignClient;
import com.vocmi.daijia.driver.service.LocationService;
import com.vocmi.daijia.map.client.LocationFeignClient;
import com.vocmi.daijia.model.entity.driver.DriverSet;
import com.vocmi.daijia.model.form.map.UpdateDriverLocationForm;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@SuppressWarnings({"unchecked", "rawtypes"})
public class LocationServiceImpl implements LocationService {

    @Resource
    private LocationFeignClient locationFeignClient;

    @Resource
    private DriverInfoFeignClient driverInfoFeignClient;

    @Override
    public Boolean updateDriverLocation(UpdateDriverLocationForm updateDriverLocationForm) {
        //开启接单了才能更新司机接单位置
        DriverSet driverSet = driverInfoFeignClient.getDriverSet(updateDriverLocationForm.getDriverId()).getData();
        if (driverSet.getServiceStatus().intValue() == 1) {
            return locationFeignClient.updateDriverLocation(updateDriverLocationForm).getData();
        } else {
            throw new VocmiException(ResultCodeEnum.NO_START_SERVICE);
        }
    }
}
