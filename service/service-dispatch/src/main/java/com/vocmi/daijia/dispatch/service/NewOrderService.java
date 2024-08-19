package com.vocmi.daijia.dispatch.service;

import com.vocmi.daijia.model.vo.dispatch.NewOrderTaskVo;
import com.vocmi.daijia.model.vo.order.NewOrderDataVo;

import java.util.List;

public interface NewOrderService {

    Long addAndStartTask(NewOrderTaskVo newOrderTaskVo);

    void executeTask(long jobId);

    List<NewOrderDataVo> findNewOrderQueueData(Long driverId);

    Boolean clearNewOrderQueueData(Long driverId);
}
