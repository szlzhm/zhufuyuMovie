package com.zhufuyu.bless.model.request.material;

/**
 * 上传图片素材请求
 */
public class ImageMaterialUploadReq {
    
    private String title;
    
    private Long categoryId;
    
    private String description;
    
    // 图片文件将通过MultipartFile单独传递
    
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
