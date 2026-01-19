package com.zhufuyu.bless.controller.material;

import com.zhufuyu.bless.model.common.BaseResponse;
import com.zhufuyu.bless.model.request.material.VoiceMaterialCreateReq;
import com.zhufuyu.bless.model.request.material.VoiceMaterialListReq;
import com.zhufuyu.bless.model.request.material.VoiceMaterialStatusReq;
import com.zhufuyu.bless.model.request.material.VoiceMaterialUpdateReq;
import com.zhufuyu.bless.model.response.common.PageResponse;
import com.zhufuyu.bless.model.response.material.VoiceMaterialResp;
import com.zhufuyu.bless.service.VoiceMaterialService;
import org.springframework.web.bind.annotation.*;

/**
 * 音色素材Controller
 */
@RestController
@RequestMapping("/api/material/voice")
public class VoiceMaterialController {

    private final VoiceMaterialService voiceMaterialService;

    public VoiceMaterialController(VoiceMaterialService voiceMaterialService) {
        this.voiceMaterialService = voiceMaterialService;
    }

    /**
     * 创建音色素材
     */
    @PostMapping("/create/v1")
    public BaseResponse<Long> createVoiceMaterial(@RequestBody VoiceMaterialCreateReq request) {
        Long id = voiceMaterialService.createVoiceMaterial(request);
        return BaseResponse.success(id);
    }

    /**
     * 分页查询音色素材列表
     */
    @PostMapping("/list/query/v1")
    public BaseResponse<PageResponse<VoiceMaterialResp>> listVoiceMaterials(@RequestBody VoiceMaterialListReq request) {
        PageResponse<VoiceMaterialResp> response = voiceMaterialService.listVoiceMaterials(request);
        return BaseResponse.success(response);
    }

    /**
     * 获取音色素材详情
     */
    @PostMapping("/detail/query/v1")
    public BaseResponse<VoiceMaterialResp> getVoiceMaterialDetail(@RequestBody Long id) {
        VoiceMaterialResp response = voiceMaterialService.getVoiceMaterialById(id);
        return BaseResponse.success(response);
    }

    /**
     * 更新音色素材
     */
    @PostMapping("/update/v1")
    public BaseResponse<Void> updateVoiceMaterial(@RequestBody VoiceMaterialUpdateReq request) {
        voiceMaterialService.updateVoiceMaterial(request);
        return BaseResponse.success(null);
    }

    /**
     * 切换音色素材状态
     */
    @PostMapping("/toggle-status/v1")
    public BaseResponse<Void> toggleVoiceMaterialStatus(@RequestBody VoiceMaterialStatusReq request) {
        voiceMaterialService.toggleVoiceMaterialStatus(request);
        return BaseResponse.success(null);
    }
}
