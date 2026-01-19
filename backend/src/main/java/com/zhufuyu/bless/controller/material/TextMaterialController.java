package com.zhufuyu.bless.controller.material;

import com.zhufuyu.bless.model.common.BaseResponse;
import com.zhufuyu.bless.model.request.material.TextMaterialCreateReq;
import com.zhufuyu.bless.model.request.material.TextMaterialListReq;
import com.zhufuyu.bless.model.request.material.TextMaterialUpdateReq;
import com.zhufuyu.bless.model.response.common.PageResponse;
import com.zhufuyu.bless.model.response.material.TextMaterialResp;
import com.zhufuyu.bless.service.TextMaterialService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/material/text")
public class TextMaterialController {

    private final TextMaterialService textMaterialService;

    public TextMaterialController(TextMaterialService textMaterialService) {
        this.textMaterialService = textMaterialService;
    }

    /**
     * 创建文案素材
     */
    @PostMapping("/create/v1")
    public BaseResponse<Long> createTextMaterial(@RequestBody TextMaterialCreateReq request) {
        Long id = textMaterialService.createTextMaterial(request);
        return BaseResponse.success(id);
    }

    /**
     * 分页查询文案素材列表
     */
    @PostMapping("/list/query/v1")
    public BaseResponse<PageResponse<TextMaterialResp>> listTextMaterials(@RequestBody TextMaterialListReq request) {
        PageResponse<TextMaterialResp> response = textMaterialService.listTextMaterials(request);
        return BaseResponse.success(response);
    }

    /**
     * 获取文案素材详情
     */
    @PostMapping("/detail/query/v1")
    public BaseResponse<TextMaterialResp> getTextMaterialDetail(@RequestBody Long id) {
        TextMaterialResp response = textMaterialService.getTextMaterialById(id);
        return BaseResponse.success(response);
    }

    /**
     * 更新文案素材
     */
    @PostMapping("/update/v1")
    public BaseResponse<Void> updateTextMaterial(@RequestBody TextMaterialUpdateReq request) {
        textMaterialService.updateTextMaterial(request);
        return BaseResponse.success(null);
    }
}
