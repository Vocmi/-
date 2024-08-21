package com.vocmi.daijia.driver.service;

import com.vocmi.daijia.model.form.map.UpdateDriverLocationForm;
import com.vocmi.daijia.model.form.map.UpdateOrderLocationForm;

public interface LocationService {


    Boolean updateDriverLocation(UpdateDriverLocationForm updateDriverLocationForm);

    Object updateOrderLocationToCache(UpdateOrderLocationForm updateOrderLocationForm);
}
