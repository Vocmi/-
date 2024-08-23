package com.vocmi.daijia.map.service;

import com.vocmi.daijia.model.form.map.OrderServiceLocationForm;
import com.vocmi.daijia.model.form.map.SearchNearByDriverForm;
import com.vocmi.daijia.model.form.map.UpdateDriverLocationForm;
import com.vocmi.daijia.model.form.map.UpdateOrderLocationForm;
import com.vocmi.daijia.model.vo.map.NearByDriverVo;
import com.vocmi.daijia.model.vo.map.OrderLocationVo;
import com.vocmi.daijia.model.vo.map.OrderServiceLastLocationVo;

import java.math.BigDecimal;
import java.util.List;

public interface LocationService {

    Boolean updateDriverLocation(UpdateDriverLocationForm updateDriverLocationForm);

    Boolean removeDriverLocation(Long driverId);

    List<NearByDriverVo> searchNearByDriver(SearchNearByDriverForm searchNearByDriverForm);

    Boolean updateOrderLocationToCache(UpdateOrderLocationForm updateOrderLocationForm);

    OrderLocationVo getCacheOrderLocation(Long orderId);

    Boolean saveOrderServiceLocation(List<OrderServiceLocationForm> orderLocationServiceFormList);

    OrderServiceLastLocationVo getOrderServiceLastLocation(Long orderId);

    BigDecimal calculateOrderRealDistance(Long orderId);
}
