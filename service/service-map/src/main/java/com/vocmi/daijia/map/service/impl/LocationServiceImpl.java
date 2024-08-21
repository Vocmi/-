package com.vocmi.daijia.map.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.vocmi.daijia.common.constant.RedisConstant;
import com.vocmi.daijia.common.constant.SystemConstant;
import com.vocmi.daijia.driver.client.DriverInfoFeignClient;
import com.vocmi.daijia.map.service.LocationService;
import com.vocmi.daijia.model.entity.driver.DriverSet;
import com.vocmi.daijia.model.form.map.SearchNearByDriverForm;
import com.vocmi.daijia.model.form.map.UpdateDriverLocationForm;
import com.vocmi.daijia.model.form.map.UpdateOrderLocationForm;
import com.vocmi.daijia.model.vo.map.NearByDriverVo;
import com.vocmi.daijia.model.vo.map.OrderLocationVo;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.geo.*;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@SuppressWarnings({"unchecked", "rawtypes"})
public class LocationServiceImpl implements LocationService {

    @Resource
    private RedisTemplate redisTemplate;

    @Resource
    private DriverInfoFeignClient driverInfoFeignClient;

    @Override
    public Boolean updateDriverLocation(UpdateDriverLocationForm updateDriverLocationForm) {
        Point point = new Point(updateDriverLocationForm.getLongitude().doubleValue(), updateDriverLocationForm.getLatitude().doubleValue());
        redisTemplate.opsForGeo().add(RedisConstant.DRIVER_GEO_LOCATION, point, updateDriverLocationForm.getDriverId().toString());

        return null;
    }

    @Override
    public Boolean removeDriverLocation(Long driverId) {
        redisTemplate.opsForGeo().remove(RedisConstant.DRIVER_GEO_LOCATION, driverId.toString());

        return true;
    }

    @Override
    public List<NearByDriverVo> searchNearByDriver(SearchNearByDriverForm searchNearByDriverForm) {

        Point point = new Point(searchNearByDriverForm.getLongitude().doubleValue(), searchNearByDriverForm.getLatitude().doubleValue());
        Distance distance = new Distance(SystemConstant.NEARBY_DRIVER_RADIUS, RedisGeoCommands.DistanceUnit.KILOMETERS);
        Circle circle = new Circle(point, distance);
        RedisGeoCommands.GeoRadiusCommandArgs args = RedisGeoCommands.GeoRadiusCommandArgs.newGeoRadiusArgs()
                .includeCoordinates().includeDistance().sortAscending();

        GeoResults<RedisGeoCommands.GeoLocation<String>> result = redisTemplate.opsForGeo().radius(RedisConstant.DRIVER_GEO_LOCATION, circle, args);
        List<GeoResult<RedisGeoCommands.GeoLocation<String>>> content = result.getContent();

        List<NearByDriverVo> list = new ArrayList();
        if (CollectionUtil.isNotEmpty(content)) {
            content.forEach(driver -> {
                long driverId = Long.parseLong(driver.getContent().getName());
                BigDecimal currentDistance = BigDecimal.valueOf(driver.getDistance().getValue()).setScale(2, RoundingMode.HALF_UP);
                log.info("司机：{}，距离：{}", driverId, currentDistance);
                BigDecimal mileageDistance = searchNearByDriverForm.getMileageDistance();

                DriverSet driverSet = driverInfoFeignClient.getDriverSet(driverId).getData();
                BigDecimal acceptDistance = driverSet.getAcceptDistance();
                BigDecimal orderDistance = driverSet.getOrderDistance();
                if (driverSet.getAcceptDistance().doubleValue() != 0 && acceptDistance.subtract(currentDistance).doubleValue() < 0) {
                    return;
                }
                if (driverSet.getOrderDistance().doubleValue() != 0 && orderDistance.subtract(mileageDistance).doubleValue() < 0) {
                    return;
                }

                NearByDriverVo nearByDriverVo = new NearByDriverVo();
                nearByDriverVo.setDriverId(driverId);
                nearByDriverVo.setDistance(currentDistance);
                list.add(nearByDriverVo);
            });
        }
        return list;
    }

    @Override
    public Boolean updateOrderLocationToCache(UpdateOrderLocationForm updateOrderLocationForm) {
        OrderLocationVo orderLocationVo = new OrderLocationVo();
        orderLocationVo.setLongitude(updateOrderLocationForm.getLongitude());
        orderLocationVo.setLatitude(updateOrderLocationForm.getLatitude());
        redisTemplate.opsForValue().set(RedisConstant.UPDATE_ORDER_LOCATION + updateOrderLocationForm.getOrderId(), orderLocationVo);
        return true;
    }

    @Override
    public OrderLocationVo getCacheOrderLocation(Long orderId) {
        OrderLocationVo orderLocationVo = (OrderLocationVo)redisTemplate.opsForValue().get(RedisConstant.UPDATE_ORDER_LOCATION + orderId);
        return orderLocationVo;
    }
}
