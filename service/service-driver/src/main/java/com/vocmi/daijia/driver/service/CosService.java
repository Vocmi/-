package com.vocmi.daijia.driver.service;

import com.vocmi.daijia.model.vo.driver.CosUploadVo;
import org.springframework.web.multipart.MultipartFile;

public interface CosService {


    CosUploadVo upload(MultipartFile file, String path);

    //获取临时签名的url
    String getImageUrl(String path);
}
