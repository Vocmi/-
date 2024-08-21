package com.vocmi.daijia.driver.controller;

import com.vocmi.daijia.common.login.VocmiLogin;
import com.vocmi.daijia.common.result.Result;
import com.vocmi.daijia.driver.service.CosService;
import com.vocmi.daijia.model.vo.driver.CosUploadVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "上传管理接口")
@RestController
@RequestMapping("file")
public class FileController {
    @Resource
    private CosService cosService;

    @Operation(summary = "上传")
//    @VocmiLogin
    @PostMapping("/upload")
    public Result<String> upload(@RequestPart("file") MultipartFile file, @RequestParam(name = "path", defaultValue = "auth") String path) {
        CosUploadVo upload = cosService.upload(file, path);
        return Result.ok(upload.getShowUrl());
    }
}
