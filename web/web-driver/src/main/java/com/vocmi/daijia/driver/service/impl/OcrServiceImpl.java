package com.vocmi.daijia.driver.service.impl;

import com.vocmi.daijia.driver.client.OcrFeignClient;
import com.vocmi.daijia.driver.service.OcrService;
import com.vocmi.daijia.model.vo.driver.DriverLicenseOcrVo;
import com.vocmi.daijia.model.vo.driver.IdCardOcrVo;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@SuppressWarnings({"unchecked", "rawtypes"})
public class OcrServiceImpl implements OcrService {

    @Resource
    private OcrFeignClient ocrFeignClient;

    @Override
    public IdCardOcrVo idCardOcr(MultipartFile file) {
        return ocrFeignClient.idCardOcr(file).getData();
    }

    @Override
    public DriverLicenseOcrVo driverLicenseOcr(MultipartFile file) {
        return ocrFeignClient.driverLicenseOcr(file).getData();
    }
}
