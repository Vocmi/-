package com.vocmi.daijia.driver.service;

import com.vocmi.daijia.model.entity.driver.DriverAccount;
import com.baomidou.mybatisplus.extension.service.IService;
import com.vocmi.daijia.model.form.driver.TransferForm;

public interface DriverAccountService extends IService<DriverAccount> {


    Boolean transfer(TransferForm transferForm);
}
