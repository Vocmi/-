package com.vocmi.daijia.rules.controller;

import com.vocmi.daijia.common.result.Result;
import com.vocmi.daijia.model.form.rules.RewardRuleRequestForm;
import com.vocmi.daijia.model.vo.rules.RewardRuleResponseVo;
import com.vocmi.daijia.rules.service.RewardRuleService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/rules/reward")
@SuppressWarnings({"unchecked", "rawtypes"})
public class RewardRuleController {
    @Resource
    private RewardRuleService rewardRuleService;

    @Operation(summary = "计算订单奖励费用")
    @PostMapping("/calculateOrderRewardFee")
    public Result<RewardRuleResponseVo> calculateOrderRewardFee(@RequestBody RewardRuleRequestForm rewardRuleRequestForm) {
        return Result.ok(rewardRuleService.calculateOrderRewardFee(rewardRuleRequestForm));
    }
}

