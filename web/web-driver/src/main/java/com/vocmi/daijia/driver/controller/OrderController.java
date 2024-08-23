package com.vocmi.daijia.driver.controller;

import com.vocmi.daijia.common.login.VocmiLogin;
import com.vocmi.daijia.common.result.Result;
import com.vocmi.daijia.common.util.AuthContextHolder;
import com.vocmi.daijia.driver.service.DriverService;
import com.vocmi.daijia.driver.service.OrderService;
import com.vocmi.daijia.model.form.map.CalculateDrivingLineForm;
import com.vocmi.daijia.model.form.order.OrderFeeForm;
import com.vocmi.daijia.model.form.order.StartDriveForm;
import com.vocmi.daijia.model.form.order.UpdateOrderCartForm;
import com.vocmi.daijia.model.vo.driver.DriverInfoVo;
import com.vocmi.daijia.model.vo.map.DrivingLineVo;
import com.vocmi.daijia.model.vo.map.OrderServiceLastLocationVo;
import com.vocmi.daijia.model.vo.order.CurrentOrderInfoVo;
import com.vocmi.daijia.model.vo.order.NewOrderDataVo;
import com.vocmi.daijia.model.vo.order.OrderInfoVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@Tag(name = "订单API接口管理")
@RestController
@RequestMapping("/order")
@SuppressWarnings({"unchecked", "rawtypes"})
public class OrderController {

    @Resource
    private OrderService orderService;

    @Resource
    private DriverService driverService;

    @Operation(summary = "查询订单状态")
    @VocmiLogin
    @GetMapping("/getOrderStatus/{orderId}")
    public Result<Integer> getOrderStatus(@PathVariable Long orderId) {
        return Result.ok(orderService.getOrderStatus(orderId));
    }

    @Operation(summary = "查询司机新订单数据")
    @VocmiLogin
    @GetMapping("/findNewOrderQueueData")
    public Result<List<NewOrderDataVo>> findNewOrderQueueData() {
        Long driverId = AuthContextHolder.getUserId();
        return Result.ok(orderService.findNewOrderQueueData(driverId));
    }

    @Operation(summary = "司机抢单")
    @VocmiLogin
    @GetMapping("/robNewOrder/{orderId}")
    public Result<Boolean> robNewOrder(@PathVariable Long orderId) {
        Long driverId = AuthContextHolder.getUserId();
        return Result.ok(orderService.robNewOrder(driverId, orderId));
    }

    @Operation(summary = "乘客端查找当前订单")
    @VocmiLogin
    @GetMapping("/searchCustomerCurrentOrder")
    public Result<CurrentOrderInfoVo> searchCustomerCurrentOrder() {
        Long customerId = AuthContextHolder.getUserId();
        return Result.ok(orderService.searchCustomerCurrentOrder(customerId));
    }

    @Operation(summary = "司机端查找当前订单")
    @VocmiLogin
    @GetMapping("/searchDriverCurrentOrder")
    public Result<CurrentOrderInfoVo> searchDriverCurrentOrder() {
        Long driverId = AuthContextHolder.getUserId();
        return Result.ok(orderService.searchDriverCurrentOrder(driverId));
    }

    @Operation(summary = "获取订单账单详细信息")
    @VocmiLogin
    @GetMapping("/getOrderInfo/{orderId}")
    public Result<OrderInfoVo> getOrderInfo(@PathVariable Long orderId) {
        Long driverId = AuthContextHolder.getUserId();
        return Result.ok(orderService.getOrderInfo(orderId, driverId));
    }

    @Operation(summary = "计算最佳驾驶线路")
    @VocmiLogin
    @PostMapping("/calculateDrivingLine")
    public Result<DrivingLineVo> calculateDrivingLine(@RequestBody CalculateDrivingLineForm calculateDrivingLineForm) {
        return Result.ok(orderService.calculateDrivingLine(calculateDrivingLineForm));
    }

    @Operation(summary = "司机到达代驾起始地点")
    @VocmiLogin
    @GetMapping("/driverArriveStartLocation/{orderId}")
    public Result<Boolean> driverArriveStartLocation(@PathVariable Long orderId) {
        Long driverId = AuthContextHolder.getUserId();
        return Result.ok(orderService.driverArriveStartLocation(orderId, driverId));
    }

    @Operation(summary = "更新代驾车辆信息")
    @VocmiLogin
    @PostMapping("/updateOrderCart")
    public Result<Boolean> updateOrderCart(@RequestBody UpdateOrderCartForm updateOrderCartForm) {
        Long driverId = AuthContextHolder.getUserId();
        updateOrderCartForm.setDriverId(driverId);
        return Result.ok(orderService.updateOrderCart(updateOrderCartForm));
    }

    @Operation(summary = "开始代驾服务")
    @VocmiLogin
    @PostMapping("/startDrive")
    public Result<Boolean> startDrive(@RequestBody StartDriveForm startDriveForm) {
        Long driverId = AuthContextHolder.getUserId();
        startDriveForm.setDriverId(driverId);
        return Result.ok(orderService.startDrive(startDriveForm));
    }

    @Operation(summary = "结束代驾服务更新订单账单")
    @VocmiLogin
    @PostMapping("/endDrive")
    public Result<Boolean> endDrive(@RequestBody OrderFeeForm orderFeeForm) {
        Long driverId = AuthContextHolder.getUserId();
        orderFeeForm.setDriverId(driverId);
        return Result.ok(orderService.endDrive(orderFeeForm));
    }
}

