package com.vocmi.daijia.map.service;

import com.vocmi.daijia.model.form.map.SearchNearByDriverForm;
import com.vocmi.daijia.model.form.map.UpdateDriverLocationForm;
import com.vocmi.daijia.model.vo.map.NearByDriverVo;

import java.util.List;

public interface LocationService {

    Boolean updateDriverLocation(UpdateDriverLocationForm updateDriverLocationForm);

    Boolean removeDriverLocation(Long driverId);

    List<NearByDriverVo> searchNearByDriver(SearchNearByDriverForm searchNearByDriverForm);
}
