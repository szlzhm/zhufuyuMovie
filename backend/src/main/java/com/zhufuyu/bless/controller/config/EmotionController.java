package com.zhufuyu.bless.controller.config;

import com.zhufuyu.bless.model.common.BaseResponse;
import com.zhufuyu.bless.model.common.PageResult;
import com.zhufuyu.bless.model.request.config.*;
import com.zhufuyu.bless.model.response.common.IdResp;
import com.zhufuyu.bless.model.response.config.EmotionListItemResp;
import com.zhufuyu.bless.service.EmotionService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/emotion")
public class EmotionController {

    private final EmotionService emotionService;

    public EmotionController(EmotionService emotionService) {
        this.emotionService = emotionService;
    }

    @PostMapping("/list/query/v1")
    public BaseResponse<PageResult<EmotionListItemResp>> queryEmotionList(@RequestBody EmotionListReq request) {
        PageResult<EmotionListItemResp> result = emotionService.queryEmotionList(request);
        return BaseResponse.success(result);
    }

    @PostMapping("/create/v1")
    public BaseResponse<IdResp> createEmotion(@Valid @RequestBody EmotionCreateReq request) {
        Long id = emotionService.createEmotion(request);
        IdResp resp = new IdResp();
        resp.setId(id);
        return BaseResponse.success(resp);
    }

    @PostMapping("/update/v1")
    public BaseResponse<Void> updateEmotion(@Valid @RequestBody EmotionUpdateReq request) {
        emotionService.updateEmotion(request);
        return BaseResponse.success(null);
    }

    @PostMapping("/toggle/status/v1")
    public BaseResponse<Void> toggleStatus(@Valid @RequestBody EmotionToggleStatusReq request) {
        emotionService.toggleStatus(request);
        return BaseResponse.success(null);
    }
}
