package com.vocmi.daijia.driver.service;

import com.vocmi.daijia.model.vo.order.TextAuditingVo;

public interface CiService {
    Boolean imageAuditing(String path);

    TextAuditingVo textAuditing(String content);
}
