package com.vocmi.daijia.rules.service;

import com.vocmi.daijia.model.form.rules.ProfitsharingRuleRequestForm;
import com.vocmi.daijia.model.vo.rules.ProfitsharingRuleResponseVo;

public interface ProfitsharingRuleService {

    ProfitsharingRuleResponseVo calculateOrderProfitsharingFee(ProfitsharingRuleRequestForm profitsharingRuleRequestForm);
}
