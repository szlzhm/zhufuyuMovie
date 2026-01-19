package com.zhufuyu.bless.controller.image;

import com.zhufuyu.bless.model.common.BaseResponse;
import com.zhufuyu.bless.model.request.image.AsyncTextToImageRequest;
import com.zhufuyu.bless.model.response.image.AsyncTextToImageResponse;
import com.zhufuyu.bless.service.AsyncTextToImageService;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;

/**
 * 异步文生图控制器
 */
@RestController
@RequestMapping("/api/image/async")
public class AsyncTextToImageController {

    @Resource
    private AsyncTextToImageService asyncTextToImageService;

    /**
     * 提交异步文生图任务
     */
    @PostMapping("/generate")
    public BaseResponse<AsyncTextToImageResponse> submitTask(@RequestBody AsyncTextToImageRequest request) {
        AsyncTextToImageResponse response = asyncTextToImageService.submitTask(request);
        return BaseResponse.success(response);
    }

    /**
     * 查询任务状态
     */
    @GetMapping("/task/{taskId}")
    public BaseResponse<AsyncTextToImageResponse> getTaskStatus(@PathVariable("taskId") String taskId) {
        AsyncTextToImageResponse response = asyncTextToImageService.getTaskStatus(taskId);
        return BaseResponse.success(response);
    }
}