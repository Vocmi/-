package com.vocmi.daijia.driver.service;

import com.vocmi.daijia.model.vo.driver.DriverLicenseOcrVo;
import com.vocmi.daijia.model.vo.driver.IdCardOcrVo;
import org.springframework.web.multipart.MultipartFile;

public interface OcrService {

    IdCardOcrVo idCardOcr(MultipartFile file);

    DriverLicenseOcrVo driverLicenseOcr(MultipartFile file);
}
