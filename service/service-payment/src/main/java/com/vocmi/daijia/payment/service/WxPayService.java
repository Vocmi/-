package com.vocmi.daijia.payment.service;

import com.vocmi.daijia.model.form.payment.PaymentInfoForm;
import com.vocmi.daijia.model.vo.payment.WxPrepayVo;

public interface WxPayService {


    WxPrepayVo createWxPayment(PaymentInfoForm paymentInfoForm);

    Boolean queryPayStatus(String orderNo);

    void handleOrder(String orderNo);
}
