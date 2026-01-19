package com.zhufuyu.bless.controller.material;

import com.zhufuyu.bless.model.common.BaseResponse;
import com.zhufuyu.bless.model.request.material.ImageMaterialListReq;
import com.zhufuyu.bless.model.request.material.ImageMaterialUpdateReq;
import com.zhufuyu.bless.model.request.material.ImageMaterialUploadReq;
import com.zhufuyu.bless.model.response.common.PageResponse;
import com.zhufuyu.bless.model.response.material.ImageMaterialResp;
import com.zhufuyu.bless.service.ImageMaterialService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * 图片素材Controller
 */
@RestController
@RequestMapping("/api/material/image")
public class ImageMaterialController {

    private final ImageMaterialService imageMaterialService;

    public ImageMaterialController(ImageMaterialService imageMaterialService) {
        this.imageMaterialService = imageMaterialService;
    }

    /**
     * 上传图片素材
     */
    @PostMapping("/upload/v1")
    public BaseResponse<Long> uploadImageMaterial(
            @RequestParam("title") String title,
            @RequestParam("categoryId") Long categoryId,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam("file") MultipartFile file) {
        
        ImageMaterialUploadReq request = new ImageMaterialUploadReq();
        request.setTitle(title);
        request.setCategoryId(categoryId);
        request.setDescription(description);

        Long id = imageMaterialService.uploadImageMaterial(request, file);
        return BaseResponse.success(id);
    }

    /**
     * 分页查询图片素材列表
     */
    @PostMapping("/list/query/v1")
    public BaseResponse<PageResponse<ImageMaterialResp>> listImageMaterials(@RequestBody ImageMaterialListReq request) {
        PageResponse<ImageMaterialResp> response = imageMaterialService.listImageMaterials(request);
        return BaseResponse.success(response);
    }

    /**
     * 获取图片素材详情
     */
    @PostMapping("/detail/query/v1")
    public BaseResponse<ImageMaterialResp> getImageMaterialDetail(@RequestBody Long id) {
        ImageMaterialResp response = imageMaterialService.getImageMaterialById(id);
        return BaseResponse.success(response);
    }

    /**
     * 更新图片素材
     */
    @PostMapping("/update/v1")
    public BaseResponse<Void> updateImageMaterial(@RequestBody ImageMaterialUpdateReq request) {
        imageMaterialService.updateImageMaterial(request);
        return BaseResponse.success(null);
    }
}
