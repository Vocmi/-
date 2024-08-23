package com.vocmi.daijia.rules.service;

import com.vocmi.daijia.model.form.rules.RewardRuleRequestForm;
import com.vocmi.daijia.model.vo.rules.RewardRuleResponseVo;

public interface RewardRuleService {

    RewardRuleResponseVo calculateOrderRewardFee(RewardRuleRequestForm rewardRuleRequestForm);
}
