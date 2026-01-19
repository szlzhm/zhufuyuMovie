package com.zhufuyu.bless.model.request.material;

/**
 * 更新图片素材请求
 */
public class ImageMaterialUpdateReq {
    
    private Long id;
    
    private String title;
    
    private Long categoryId;
    
    private String description;
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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
