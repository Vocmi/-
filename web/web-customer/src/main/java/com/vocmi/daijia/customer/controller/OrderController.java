package com.vocmi.daijia.customer.controller;

import com.vocmi.daijia.common.login.VocmiLogin;
import com.vocmi.daijia.common.result.Result;
import com.vocmi.daijia.customer.service.OrderService;
import com.vocmi.daijia.model.form.customer.ExpectOrderForm;
import com.vocmi.daijia.model.vo.customer.ExpectOrderVo;
import com.vocmi.daijia.model.vo.order.CurrentOrderInfoVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Tag(name = "订单API接口管理")
@RestController
@RequestMapping("/order")
@SuppressWarnings({"unchecked", "rawtypes"})
public class OrderController {
    @Resource
    private OrderService orderService;

    @Operation(summary = "查找乘客端当前订单")
    @VocmiLogin
    @GetMapping("/searchCustomerCurrentOrder")
    public Result<CurrentOrderInfoVo> searchCustomerCurrentOrder() {
        CurrentOrderInfoVo currentOrderInfoVo = new CurrentOrderInfoVo();
        currentOrderInfoVo.setIsHasCurrentOrder(false);
        return Result.ok(currentOrderInfoVo);
    }

    @Operation(summary = "预估订单数据")
    @VocmiLogin
    @PostMapping("/expectOrder")
    public Result<ExpectOrderVo> expectOrder(@RequestBody ExpectOrderForm expectOrderForm) {
        return Result.ok(orderService.expectOrder(expectOrderForm));
    }
}

