package com.vocmi.daijia.driver.service;

import com.vocmi.daijia.model.form.order.OrderMonitorForm;
import org.springframework.web.multipart.MultipartFile;

public interface MonitorService {

    Boolean upload(MultipartFile file, OrderMonitorForm orderMonitorForm);
}
