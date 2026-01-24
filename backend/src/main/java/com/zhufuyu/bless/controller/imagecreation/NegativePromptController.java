package com.zhufuyu.bless.controller.imagecreation;

import com.zhufuyu.bless.model.common.BaseResponse;
import com.zhufuyu.bless.model.request.NegativePromptQueryReq;
import com.zhufuyu.bless.model.request.NegativePromptReq;
import com.zhufuyu.bless.model.response.NegativePromptResp;
import com.zhufuyu.bless.model.response.common.PageResponse;
import com.zhufuyu.bless.service.NegativePromptService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.List;

/**
 * 负面提示语管理接口
 */
@RestController
@RequestMapping("/api/negative-prompt")
@RequiredArgsConstructor
public class NegativePromptController {

    private final NegativePromptService negativePromptService;

    /**
     * 保存或更新负面提示语
     */
    @PostMapping("/save/v1")
    public BaseResponse<Void> save(@RequestBody @Valid NegativePromptReq request) {
        negativePromptService.save(request);
        return BaseResponse.success(null);
    }

    /**
     * 分页查询负面提示语
     */
    @PostMapping("/query/v1")
    public BaseResponse<PageResponse<NegativePromptResp>> query(@RequestBody NegativePromptQueryReq request) {
        return BaseResponse.success(negativePromptService.query(request));
    }

    /**
     * 删除负面提示语
     */
    @PostMapping("/delete/v1")
    public BaseResponse<Void> delete(@RequestBody NegativePromptReq request) {
        if (request.getId() != null) {
            negativePromptService.delete(request.getId());
        }
        return BaseResponse.success(null);
    }

    /**
     * 获取所有负面提示语列表
     */
    @PostMapping("/list-all/v1")
    public BaseResponse<List<NegativePromptResp>> listAll() {
        return BaseResponse.success(negativePromptService.listAll());
    }
}
