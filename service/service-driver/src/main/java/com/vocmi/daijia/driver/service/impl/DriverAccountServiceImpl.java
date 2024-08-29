package com.vocmi.daijia.driver.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.vocmi.daijia.driver.mapper.DriverAccountDetailMapper;
import com.vocmi.daijia.driver.mapper.DriverAccountMapper;
import com.vocmi.daijia.driver.service.DriverAccountService;
import com.vocmi.daijia.model.entity.driver.DriverAccount;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.vocmi.daijia.model.entity.driver.DriverAccountDetail;
import com.vocmi.daijia.model.form.driver.TransferForm;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@SuppressWarnings({"unchecked", "rawtypes"})
public class DriverAccountServiceImpl extends ServiceImpl<DriverAccountMapper, DriverAccount> implements DriverAccountService {

    @Resource
    private DriverAccountMapper driverAccountMapper;

    @Resource
    private DriverAccountDetailMapper driverAccountDetailMapper;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Boolean transfer(TransferForm transferForm) {
        //去重
        long count = driverAccountDetailMapper.selectCount(new LambdaQueryWrapper<DriverAccountDetail>().eq(DriverAccountDetail::getTradeNo, transferForm.getTradeNo()));
        if(count > 0) return true;

        //添加账号金额
        driverAccountMapper.add(transferForm.getDriverId(), transferForm.getAmount());

        //添加账户明细
        DriverAccountDetail driverAccountDetail = new DriverAccountDetail();
        BeanUtils.copyProperties(transferForm, driverAccountDetail);
        driverAccountDetailMapper.insert(driverAccountDetail);
        return true;
    }
}
