package com.zhufuyu.bless.service;

import com.zhufuyu.bless.model.common.PageResult;
import com.zhufuyu.bless.model.request.config.ImageCategoryCreateReq;
import com.zhufuyu.bless.model.request.config.ImageCategoryListReq;
import com.zhufuyu.bless.model.request.config.ImageCategoryToggleStatusReq;
import com.zhufuyu.bless.model.request.config.ImageCategoryUpdateReq;
import com.zhufuyu.bless.model.response.config.ImageCategoryListItemResp;

public interface ImageCategoryService {

    /**
     * 查询图片分类列表
     */
    PageResult<ImageCategoryListItemResp> queryImageCategoryList(ImageCategoryListReq request);

    /**
     * 创建图片分类
     */
    Long createImageCategory(ImageCategoryCreateReq request);

    /**
     * 更新图片分类
     */
    void updateImageCategory(ImageCategoryUpdateReq request);

    /**
     * 启用/禁用图片分类
     */
    void toggleStatus(ImageCategoryToggleStatusReq request);
}
