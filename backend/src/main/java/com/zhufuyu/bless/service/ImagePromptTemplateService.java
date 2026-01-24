package com.zhufuyu.bless.service;

import com.zhufuyu.bless.model.request.ImagePromptTemplateQueryReq;
import com.zhufuyu.bless.model.request.ImagePromptTemplateReq;
import com.zhufuyu.bless.model.request.SaveTemplateFromResultReq;
import com.zhufuyu.bless.model.response.ImagePromptTemplateResp;
import com.zhufuyu.bless.model.response.common.PageResponse;

import java.util.List;

/**
 * 提示语模板服务接口
 */
public interface ImagePromptTemplateService {

    /**
     * 保存或更新提示语模板
     */
    void saveTemplate(ImagePromptTemplateReq request);

    /**
     * 分页查询提示语模板
     */
    PageResponse<ImagePromptTemplateResp> queryTemplates(ImagePromptTemplateQueryReq request);

    /**
     * 删除提示语模板 (逻辑删除)
     */
    void deleteTemplate(Long id);

    /**
     * 获取所有启用的模板 (用于下拉选择)
     */
    List<ImagePromptTemplateResp> getAllActiveTemplates();

    /**
     * 根据 ID 获取模板详情
     */
    ImagePromptTemplateResp getTemplateById(Long id);

    /**
     * 从创作结果保存为模板
     */
    void saveTemplateFromResult(SaveTemplateFromResultReq request);
}
