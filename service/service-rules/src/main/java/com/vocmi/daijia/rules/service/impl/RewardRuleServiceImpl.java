package com.vocmi.daijia.rules.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.vocmi.daijia.model.entity.rule.RewardRule;
import com.vocmi.daijia.model.form.rules.RewardRuleRequest;
import com.vocmi.daijia.model.form.rules.RewardRuleRequestForm;
import com.vocmi.daijia.model.vo.rules.RewardRuleResponse;
import com.vocmi.daijia.model.vo.rules.RewardRuleResponseVo;
import com.vocmi.daijia.rules.mapper.RewardRuleMapper;
import com.vocmi.daijia.rules.service.RewardRuleService;
import com.vocmi.daijia.rules.utils.DroolsHelper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.kie.api.runtime.KieSession;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@SuppressWarnings({"unchecked", "rawtypes"})
public class RewardRuleServiceImpl implements RewardRuleService {

    private static final String RULES_CUSTOMER_RULES_DRL = "rules/RewardRule.drl";

    @Override
    public RewardRuleResponseVo calculateOrderRewardFee(RewardRuleRequestForm rewardRuleRequestForm) {
        //封装传入对象
        RewardRuleRequest rewardRuleRequest = new RewardRuleRequest();
        rewardRuleRequest.setOrderNum(rewardRuleRequestForm.getOrderNum());
        log.info("传入参数：{}", JSON.toJSONString(rewardRuleRequest));

        // TODO 获取最新订单费用规则，入库的时候需要
        //RewardRule rewardRule = rewardRuleMapper.selectOne(new LambdaQueryWrapper<RewardRule>().orderByDesc(RewardRule::getId).last("limit 1"));
        //KieSession kieSession = DroolsHelper.loadForRule(rewardRule.getRule());

        //获取本地数据路径
        KieSession kieSession = DroolsHelper.loadForRule(RULES_CUSTOMER_RULES_DRL);

        //封装返回对象
        RewardRuleResponse rewardRuleResponse = new RewardRuleResponse();
        kieSession.setGlobal("rewardRuleResponse", rewardRuleResponse);
        // 设置订单对象
        kieSession.insert(rewardRuleRequest);
        // 触发规则
        kieSession.fireAllRules();
        // 中止会话
        kieSession.dispose();
        log.info("计算结果：{}", JSON.toJSONString(rewardRuleResponse));

        //封装返回对象
        RewardRuleResponseVo rewardRuleResponseVo = new RewardRuleResponseVo();
//        rewardRuleResponseVo.setRewardRuleId(rewardRule.getId());//入库的时候需要
        rewardRuleResponseVo.setRewardAmount(rewardRuleResponse.getRewardAmount());
        return rewardRuleResponseVo;
    }
}
