package com.vocmi.daijia.customer.client;

import com.vocmi.daijia.common.result.Result;
import com.vocmi.daijia.model.form.customer.UpdateWxPhoneForm;
import com.vocmi.daijia.model.vo.customer.CustomerLoginVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(value = "service-customer")
public interface CustomerInfoFeignClient {

    @GetMapping("/customer/info/login/{code}")
    public Result<Long> login(@PathVariable String code);

    @GetMapping("/customer/info/getCustomerLoginInfo/{customerId}")
    public Result<CustomerLoginVo> getCustomerLoginInfo(@PathVariable Long customerId);

    @PostMapping("/customer/info/updateWxPhoneNumber")
    Result<Boolean> updateWxPhoneNumber(@RequestBody UpdateWxPhoneForm updateWxPhoneForm);
}