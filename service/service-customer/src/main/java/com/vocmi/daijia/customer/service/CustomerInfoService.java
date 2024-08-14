package com.vocmi.daijia.customer.service;

import com.vocmi.daijia.model.entity.customer.CustomerInfo;
import com.baomidou.mybatisplus.extension.service.IService;
import com.vocmi.daijia.model.form.customer.UpdateWxPhoneForm;
import com.vocmi.daijia.model.vo.customer.CustomerLoginVo;

public interface CustomerInfoService extends IService<CustomerInfo> {

    Long login(String code);

    CustomerLoginVo getCustomerLoginInfo(Long customerId);

    Boolean updateWxPhoneNumber(UpdateWxPhoneForm updateWxPhoneForm);
}
