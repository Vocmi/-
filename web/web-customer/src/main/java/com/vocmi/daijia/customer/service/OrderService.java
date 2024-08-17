package com.vocmi.daijia.customer.service;

import com.vocmi.daijia.model.form.customer.ExpectOrderForm;
import com.vocmi.daijia.model.vo.customer.ExpectOrderVo;

public interface OrderService {

    ExpectOrderVo expectOrder(ExpectOrderForm expectOrderForm);
}
