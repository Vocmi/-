package com.vocmi.daijia.mgr.service.impl;

import com.vocmi.daijia.mgr.service.SysLoginLogService;
import com.vocmi.daijia.model.entity.system.SysLoginLog;
import com.vocmi.daijia.model.query.system.SysLoginLogQuery;
import com.vocmi.daijia.model.vo.base.PageVo;
import com.vocmi.daijia.system.client.SysLoginLogFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@SuppressWarnings({"unchecked", "rawtypes"})
public class SysLoginLogServiceImpl implements SysLoginLogService {

    @Autowired
    private SysLoginLogFeignClient sysLoginLogFeignClient;

    @Override
    public PageVo<SysLoginLog> findPage(Long page, Long limit, SysLoginLogQuery sysLoginLogQuery) {
        return sysLoginLogFeignClient.findPage(page, limit, sysLoginLogQuery).getData();
    }

    @Override
    public void recordLoginLog(SysLoginLog sysLoginLog) {
        sysLoginLogFeignClient.recordLoginLog(sysLoginLog);
    }

    @Override
    public SysLoginLog getById(Long id) {
        return sysLoginLogFeignClient.getById(id).getData();
    }
}
