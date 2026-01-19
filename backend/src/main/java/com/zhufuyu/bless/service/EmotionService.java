package com.zhufuyu.bless.service;

import com.zhufuyu.bless.model.common.PageResult;
import com.zhufuyu.bless.model.request.config.*;
import com.zhufuyu.bless.model.response.config.EmotionListItemResp;

public interface EmotionService {

    /**
     * 查询情绪列表
     */
    PageResult<EmotionListItemResp> queryEmotionList(EmotionListReq request);

    /**
     * 创建情绪
     */
    Long createEmotion(EmotionCreateReq request);

    /**
     * 更新情绪
     */
    void updateEmotion(EmotionUpdateReq request);

    /**
     * 启用/禁用情绪
     */
    void toggleStatus(EmotionToggleStatusReq request);
}
