package com.zhufuyu.bless.service;

import com.zhufuyu.bless.model.request.*;
import com.zhufuyu.bless.model.response.ImageGenerationTaskResp;
import com.zhufuyu.bless.model.response.ImageResultResp;
import com.zhufuyu.bless.model.response.common.PageResponse;

/**
 * 图片创作核心服务接口
 */
public interface ImageCreationService {

    /**
     * 提交图片创作任务
     */
    Long submitImageTask(ImagePromptSubmitReq request);

    /**
     * 分页查询创作任务
     */
    PageResponse<ImageGenerationTaskResp> queryTasks(ImageTaskQueryReq request);

    /**
     * 更新任务状态
     */
    void updateTaskStatus(ImageTaskStatusUpdateReq request);

    /**
     * 分页查询创作结果
     */
    PageResponse<ImageResultResp> queryResults(ImageResultQueryReq request);

    /**
     * 注册生成结果
     */
    void registerResult(ImageResultRegisterReq request);

    /**
     * 获取任务详情（包含生成的结果）
     */
    ImageGenerationTaskResp getTaskDetail(Long taskId);

    /**
     * 获取最早的一条等待中的任务数据
     */
    ImageGenerationTaskResp getEarliestWaitingTask();

    /**
     * 处理任务拉取成功回执，更新状态为 PROCESSING
     */
    void handleTaskPullSuccess(Long taskId);

    /**
     * 上报任务失败回执，更新状态为 FAILED
     */
    void reportTaskFailure(ImageTaskReportFailureReq request);
}
