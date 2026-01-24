package com.zhufuyu.bless.service;

import com.zhufuyu.bless.model.request.ImagePromptSubmitReq;

/**
 * Qwen-Image 模型生成服务接口
 */
public interface QwenImageGenerationService {

    /**
     * 执行图像生成任务 (异步)
     * @param taskId 任务ID
     * @param prompt 最终提示语
     * @param params 提交参数
     */
    void executeGenerationTask(Long taskId, String prompt, ImagePromptSubmitReq params);
}
