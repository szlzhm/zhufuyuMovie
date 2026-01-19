package com.zhufuyu.bless.controller.config;

import com.zhufuyu.bless.model.common.BaseResponse;
import com.zhufuyu.bless.model.common.PageResult;
import com.zhufuyu.bless.model.response.common.IdResp;
import com.zhufuyu.bless.model.request.config.TextCategoryCreateReq;
import com.zhufuyu.bless.model.request.config.TextCategoryListReq;
import com.zhufuyu.bless.model.request.config.TextCategoryToggleStatusReq;
import com.zhufuyu.bless.model.request.config.TextCategoryUpdateReq;
import com.zhufuyu.bless.model.response.config.TextCategoryListItemResp;
import com.zhufuyu.bless.service.TextCategoryService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/text-category")
public class TextCategoryController {

    private final TextCategoryService textCategoryService;

    public TextCategoryController(TextCategoryService textCategoryService) {
        this.textCategoryService = textCategoryService;
    }

    @PostMapping("/list/query/v1")
    public BaseResponse<PageResult<TextCategoryListItemResp>> queryTextCategoryList(@RequestBody TextCategoryListReq request) {
        PageResult<TextCategoryListItemResp> result = textCategoryService.queryTextCategoryList(request);
        return BaseResponse.success(result);
    }

    @PostMapping("/create/v1")
    public BaseResponse<IdResp> createTextCategory(@Valid @RequestBody TextCategoryCreateReq request) {
        Long id = textCategoryService.createTextCategory(request);
        IdResp resp = new IdResp();
        resp.setId(id);
        return BaseResponse.success(resp);
    }

    @PostMapping("/update/v1")
    public BaseResponse<Void> updateTextCategory(@Valid @RequestBody TextCategoryUpdateReq request) {
        textCategoryService.updateTextCategory(request);
        return BaseResponse.success(null);
    }

    @PostMapping("/toggle/status/v1")
    public BaseResponse<Void> toggleStatus(@Valid @RequestBody TextCategoryToggleStatusReq request) {
        textCategoryService.toggleStatus(request);
        return BaseResponse.success(null);
    }
}
