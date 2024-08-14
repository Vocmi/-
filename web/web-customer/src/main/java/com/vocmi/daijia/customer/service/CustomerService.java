package com.vocmi.daijia.customer.service;

import com.vocmi.daijia.model.form.customer.UpdateWxPhoneForm;
import com.vocmi.daijia.model.vo.customer.CustomerLoginVo;

public interface CustomerService {


    String login(String code);

    CustomerLoginVo getCustomerLoginInfo(Long customerId);

    Boolean updateWxPhoneNumber(UpdateWxPhoneForm updateWxPhoneForm);
}
