package com.zhufuyu.bless.service;

import com.zhufuyu.bless.model.request.task.VideoTaskCreateReq;
import com.zhufuyu.bless.model.request.task.VideoTaskListReq;
import com.zhufuyu.bless.model.response.common.PageResponse;
import com.zhufuyu.bless.model.response.task.VideoTaskResp;

/**
 * 视频创作任务服务接口
 */
public interface VideoTaskService {

    /**
     * 创建任务
     */
    Long createTask(VideoTaskCreateReq request);

    /**
     * 分页查询任务列表
     */
    PageResponse<VideoTaskResp> listTasks(VideoTaskListReq request);

    /**
     * 获取任务详情
     */
    VideoTaskResp getTaskById(Long id);

    /**
     * 执行任务(开始创作)
     */
    void executeTask(Long id);

    /**
     * 批量执行任务
     */
    void executeTasks(java.util.List<Long> ids);

    /**
     * 确认任务结果入库
     */
    void confirmToLibrary(Long id);
}
