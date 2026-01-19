package com.zhufuyu.bless.controller.config;

import com.zhufuyu.bless.model.common.BaseResponse;
import com.zhufuyu.bless.model.common.PageResult;
import com.zhufuyu.bless.model.response.common.IdResp;
import com.zhufuyu.bless.model.request.config.ImageCategoryCreateReq;
import com.zhufuyu.bless.model.request.config.ImageCategoryListReq;
import com.zhufuyu.bless.model.request.config.ImageCategoryToggleStatusReq;
import com.zhufuyu.bless.model.request.config.ImageCategoryUpdateReq;
import com.zhufuyu.bless.model.response.config.ImageCategoryListItemResp;
import com.zhufuyu.bless.service.ImageCategoryService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/image-category")
public class ImageCategoryController {

    private final ImageCategoryService imageCategoryService;

    public ImageCategoryController(ImageCategoryService imageCategoryService) {
        this.imageCategoryService = imageCategoryService;
    }

    @PostMapping("/list/query/v1")
    public BaseResponse<PageResult<ImageCategoryListItemResp>> queryImageCategoryList(@RequestBody ImageCategoryListReq request) {
        PageResult<ImageCategoryListItemResp> result = imageCategoryService.queryImageCategoryList(request);
        return BaseResponse.success(result);
    }

    @PostMapping("/create/v1")
    public BaseResponse<IdResp> createImageCategory(@Valid @RequestBody ImageCategoryCreateReq request) {
        Long id = imageCategoryService.createImageCategory(request);
        IdResp resp = new IdResp();
        resp.setId(id);
        return BaseResponse.success(resp);
    }

    @PostMapping("/update/v1")
    public BaseResponse<Void> updateImageCategory(@Valid @RequestBody ImageCategoryUpdateReq request) {
        imageCategoryService.updateImageCategory(request);
        return BaseResponse.success(null);
    }

    @PostMapping("/toggle/status/v1")
    public BaseResponse<Void> toggleStatus(@Valid @RequestBody ImageCategoryToggleStatusReq request) {
        imageCategoryService.toggleStatus(request);
        return BaseResponse.success(null);
    }
}
