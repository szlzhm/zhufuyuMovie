package com.zhufuyu.bless.model.request.material;

import com.zhufuyu.bless.model.request.common.PageRequest;

/**
 * 图片素材列表查询请求
 */
public class ImageMaterialListReq extends PageRequest {
    
    private String title;
    
    private Long categoryId;
    
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }
}
