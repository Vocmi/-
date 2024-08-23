package com.vocmi.daijia.driver.controller;

import com.vocmi.daijia.common.login.VocmiLogin;
import com.vocmi.daijia.common.result.Result;
import com.vocmi.daijia.driver.service.CosService;
import com.vocmi.daijia.driver.service.FileService;
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
    private FileService fileService;

    @Operation(summary = "Minio文件上传")
    @PostMapping("upload")
    public Result<String> upload(@RequestPart("file") MultipartFile file) {
        return Result.ok(fileService.upload(file));
    }
}
