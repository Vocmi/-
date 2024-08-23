package com.vocmi.daijia.driver.service;

import com.vocmi.daijia.model.form.map.OrderServiceLocationForm;
import com.vocmi.daijia.model.form.map.UpdateDriverLocationForm;
import com.vocmi.daijia.model.form.map.UpdateOrderLocationForm;

import java.util.List;

public interface LocationService {


    Boolean updateDriverLocation(UpdateDriverLocationForm updateDriverLocationForm);

    Object updateOrderLocationToCache(UpdateOrderLocationForm updateOrderLocationForm);

    Boolean saveOrderServiceLocation(List<OrderServiceLocationForm> orderLocationServiceFormList);
}
