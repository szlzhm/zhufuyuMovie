package com.zhufuyu.bless.service;

import com.zhufuyu.bless.model.request.image.AsyncTextToImageRequest;
import com.zhufuyu.bless.model.response.image.AsyncTextToImageResponse;

/**
 * 异步文生图服务接口
 */
public interface AsyncTextToImageService {
    
    /**
     * 提交异步文生图任务
     */
    AsyncTextToImageResponse submitTask(AsyncTextToImageRequest request);
    
    /**
     * 查询任务状态
     */
    AsyncTextToImageResponse getTaskStatus(String taskId);
}