package com.vocmi.daijia.driver.controller;

import com.vocmi.daijia.common.result.Result;
import com.vocmi.daijia.driver.service.CosService;
import com.vocmi.daijia.model.vo.driver.CosUploadVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Tag(name = "腾讯云cos上传接口管理")
@RestController
@RequestMapping(value = "/cos")
@SuppressWarnings({"unchecked", "rawtypes"})
public class CosController {
    @Resource
    private CosService cosService;

    @Operation(summary = "上传")
//    @VocmiLogin
    @PostMapping("/upload")
    public Result<CosUploadVo> upload(@RequestPart("file") MultipartFile file, @RequestParam(name = "path", defaultValue = "auth") String path) {
        return Result.ok(cosService.upload(file, path));
    }
}

