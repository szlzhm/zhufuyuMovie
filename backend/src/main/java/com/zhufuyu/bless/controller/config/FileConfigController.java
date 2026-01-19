package com.zhufuyu.bless.controller.config;

import com.zhufuyu.bless.model.common.BaseResponse;
import com.zhufuyu.bless.model.request.config.FileRootPathUpdateReq;
import com.zhufuyu.bless.model.response.config.FileRootPathResp;
import com.zhufuyu.bless.service.FileConfigService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/config/file")
public class FileConfigController {

    private final FileConfigService fileConfigService;

    public FileConfigController(FileConfigService fileConfigService) {
        this.fileConfigService = fileConfigService;
    }

    @PostMapping("/root/query/v1")
    public BaseResponse<FileRootPathResp> getFileRootPath() {
        FileRootPathResp resp = fileConfigService.getFileRootPath();
        return BaseResponse.success(resp);
    }

    @PostMapping("/root/update/v1")
    public BaseResponse<Void> updateFileRootPath(@Valid @RequestBody FileRootPathUpdateReq request) {
        fileConfigService.updateFileRootPath(request);
        return BaseResponse.success(null);
    }
}
