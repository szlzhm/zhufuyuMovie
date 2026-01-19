package com.zhufuyu.bless.service;

import com.zhufuyu.bless.model.common.PageResult;
import com.zhufuyu.bless.model.request.config.TextCategoryCreateReq;
import com.zhufuyu.bless.model.request.config.TextCategoryListReq;
import com.zhufuyu.bless.model.request.config.TextCategoryToggleStatusReq;
import com.zhufuyu.bless.model.request.config.TextCategoryUpdateReq;
import com.zhufuyu.bless.model.response.config.TextCategoryListItemResp;

public interface TextCategoryService {

    /**
     * 查询文案分类列表
     */
    PageResult<TextCategoryListItemResp> queryTextCategoryList(TextCategoryListReq request);

    /**
     * 创建文案分类
     */
    Long createTextCategory(TextCategoryCreateReq request);

    /**
     * 更新文案分类
     */
    void updateTextCategory(TextCategoryUpdateReq request);

    /**
     * 启用/禁用文案分类
     */
    void toggleStatus(TextCategoryToggleStatusReq request);
}
