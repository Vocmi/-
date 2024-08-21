package com.vocmi.daijia.map.client;

import com.vocmi.daijia.common.result.Result;
import com.vocmi.daijia.model.form.map.CalculateDrivingLineForm;
import com.vocmi.daijia.model.form.map.UpdateOrderLocationForm;
import com.vocmi.daijia.model.vo.map.DrivingLineVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(value = "service-map")
public interface MapFeignClient {
    /**
     * 计算驾驶线路
     * @param calculateDrivingLineForm
     * @return
     */
    @PostMapping("/map/calculateDrivingLine")
    Result<DrivingLineVo> calculateDrivingLine(@RequestBody CalculateDrivingLineForm calculateDrivingLineForm);
}