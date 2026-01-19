package com.zhufuyu.bless.model.request.config;

import jakarta.validation.constraints.NotBlank;

public class ImageCategoryCreateReq {

    @NotBlank(message = "分类编码不能为空")
    private String categoryCode;

    @NotBlank(message = "分类名称不能为空")
    private String categoryName;

    private String categoryDesc;

    public String getCategoryCode() {
        return categoryCode;
    }

    public void setCategoryCode(String categoryCode) {
        this.categoryCode = categoryCode;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getCategoryDesc() {
        return categoryDesc;
    }

    public void setCategoryDesc(String categoryDesc) {
        this.categoryDesc = categoryDesc;
    }
}
