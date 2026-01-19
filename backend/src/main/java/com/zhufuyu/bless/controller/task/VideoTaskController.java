package com.zhufuyu.bless.controller.task;

import com.zhufuyu.bless.model.common.BaseResponse;
import com.zhufuyu.bless.model.request.task.VideoTaskCreateReq;
import com.zhufuyu.bless.model.request.task.VideoTaskListReq;
import com.zhufuyu.bless.model.response.common.PageResponse;
import com.zhufuyu.bless.model.response.task.VideoTaskResp;
import com.zhufuyu.bless.service.VideoTaskService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 视频创作任务管理
 */
@RestController
@RequestMapping("/api/task")
public class VideoTaskController {

    private final VideoTaskService videoTaskService;

    public VideoTaskController(VideoTaskService videoTaskService) {
        this.videoTaskService = videoTaskService;
    }

    /**
     * 创建任务
     */
    @PostMapping("/create/v1")
    public BaseResponse<Long> createTask(@RequestBody VideoTaskCreateReq request) {
        Long id = videoTaskService.createTask(request);
        return BaseResponse.success(id);
    }

    /**
     * 分页查询任务列表
     */
    @PostMapping("/list/query/v1")
    public BaseResponse<PageResponse<VideoTaskResp>> listTasks(@RequestBody VideoTaskListReq request) {
        PageResponse<VideoTaskResp> response = videoTaskService.listTasks(request);
        return BaseResponse.success(response);
    }

    /**
     * 获取任务详情
     */
    @PostMapping("/detail/query/v1")
    public BaseResponse<VideoTaskResp> getTaskDetail(@RequestBody Long id) {
        VideoTaskResp response = videoTaskService.getTaskById(id);
        return BaseResponse.success(response);
    }

    /**
     * 执行单个任务
     */
    @PostMapping("/execute/v1")
    public BaseResponse<Void> executeTask(@RequestBody Long id) {
        videoTaskService.executeTask(id);
        return BaseResponse.success(null);
    }

    /**
     * 批量执行任务
     */
    @PostMapping("/execute-batch/v1")
    public BaseResponse<Void> executeTasks(@RequestBody List<Long> ids) {
        videoTaskService.executeTasks(ids);
        return BaseResponse.success(null);
    }

    /**
     * 确认任务结果入库
     */
    @PostMapping("/confirm-library/v1")
    public BaseResponse<Void> confirmToLibrary(@RequestBody Long id) {
        videoTaskService.confirmToLibrary(id);
        return BaseResponse.success(null);
    }
}
