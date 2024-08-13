package com.vocmi.daijia.customer.service;

import com.vocmi.daijia.model.entity.customer.CustomerInfo;
import com.baomidou.mybatisplus.extension.service.IService;

public interface CustomerInfoService extends IService<CustomerInfo> {

    Long login(String code);
}
