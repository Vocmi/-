package com.vocmi.daijia.rules.service;

import com.vocmi.daijia.model.form.rules.FeeRuleRequestForm;
import com.vocmi.daijia.model.vo.rules.FeeRuleResponseVo;

public interface FeeRuleService {

    FeeRuleResponseVo calculateOrderFee(FeeRuleRequestForm calculateOrderFeeForm);
}
