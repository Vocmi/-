package com.vocmi.daijia.customer.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.vocmi.daijia.common.constant.RedisConstant;
import com.vocmi.daijia.common.execption.VocmiException;
import com.vocmi.daijia.common.login.VocmiLogin;
import com.vocmi.daijia.common.result.Result;
import com.vocmi.daijia.common.result.ResultCodeEnum;
import com.vocmi.daijia.common.util.AuthContextHolder;
import com.vocmi.daijia.customer.service.CustomerService;
import com.vocmi.daijia.model.form.customer.UpdateWxPhoneForm;
import com.vocmi.daijia.model.vo.customer.CustomerLoginVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Tag(name = "客户API接口管理")
@RestController
@RequestMapping("/customer")
@SuppressWarnings({"unchecked", "rawtypes"})
public class CustomerController {
    @Resource
    private CustomerService customerInfoService;

    @Resource
    private RedisTemplate redisTemplate;

    @Operation(summary = "小程序授权登录")
    @GetMapping("/login/{code}")
    public Result<String> wxLogin(@PathVariable String code) {
        return Result.ok(customerInfoService.login(code));
    }

    @Operation(summary = "获取客户登录信息")
    @GetMapping("/getCustomerLoginInfo")
    @VocmiLogin
    public Result<CustomerLoginVo> getCustomerLoginInfo() {
        return Result.ok(customerInfoService.getCustomerLoginInfo(AuthContextHolder.getUserId()));
    }

    @Operation(summary = "更新用户微信手机号")
    @VocmiLogin
    @PostMapping("/updateWxPhone")
    public Result<Boolean> updateWxPhone(@RequestBody UpdateWxPhoneForm updateWxPhoneForm) {
        updateWxPhoneForm.setCustomerId(AuthContextHolder.getUserId());
       // Boolean b = customerInfoService.updateWxPhoneNumber(updateWxPhoneForm);
        return Result.ok(true);
    }
}

