package com.zhufuyu.bless.model.request.config;

import jakarta.validation.constraints.NotNull;

public class TextCategoryUpdateReq {

    @NotNull(message = "ID不能为空")
    private Long id;

    private String categoryName;
    private String categoryDesc;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
