package com.zhufuyu.bless.controller.task;

import com.zhufuyu.bless.annotation.ApiLog;
import com.zhufuyu.bless.model.common.BaseResponse;
import com.zhufuyu.bless.model.response.ImageGenerationTaskResp;
import com.zhufuyu.bless.service.ImageCreationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 内部任务调度接口
 */
@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskInternalController {

    private final ImageCreationService imageCreationService;

    /**
     * 获取最早的一条等待中的任务数据 (内部调用)
     */
    @ApiLog("获取最早等待任务")
    @GetMapping("/fetch")
    public BaseResponse<ImageGenerationTaskResp> fetchEarliestTask() {
        return BaseResponse.success(imageCreationService.getEarliestWaitingTask());
    }
}
