package com.zhufuyu.bless.service;

import com.zhufuyu.bless.model.request.material.ImageMaterialListReq;
import com.zhufuyu.bless.model.request.material.ImageMaterialUpdateReq;
import com.zhufuyu.bless.model.request.material.ImageMaterialUploadReq;
import com.zhufuyu.bless.model.response.common.PageResponse;
import com.zhufuyu.bless.model.response.material.ImageMaterialResp;
import org.springframework.web.multipart.MultipartFile;

/**
 * 图片素材服务接口
 */
public interface ImageMaterialService {

    /**
     * 上传图片素材
     */
    Long uploadImageMaterial(ImageMaterialUploadReq request, MultipartFile file);

    /**
     * 分页查询图片素材列表
     */
    PageResponse<ImageMaterialResp> listImageMaterials(ImageMaterialListReq request);

    /**
     * 获取图片素材详情
     */
    ImageMaterialResp getImageMaterialById(Long id);

    /**
     * 更新图片素材
     */
    void updateImageMaterial(ImageMaterialUpdateReq request);
}
