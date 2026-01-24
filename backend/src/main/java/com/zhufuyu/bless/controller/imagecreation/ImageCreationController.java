package com.zhufuyu.bless.controller.imagecreation;

import com.zhufuyu.bless.annotation.ApiLog;
import com.zhufuyu.bless.model.common.BaseResponse;
import com.zhufuyu.bless.model.request.*;
import com.zhufuyu.bless.model.response.ImageGenerationTaskResp;
import com.zhufuyu.bless.model.response.ImagePromptTemplateResp;
import com.zhufuyu.bless.model.response.ImageResultResp;
import com.zhufuyu.bless.model.response.common.PageResponse;
import com.zhufuyu.bless.service.ImageCreationService;
import com.zhufuyu.bless.service.ImagePromptTemplateService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 图片创作控制层
 */
@Slf4j
@RestController
@RequestMapping("/api/image-creation")
@RequiredArgsConstructor
public class ImageCreationController {

    private final ImagePromptTemplateService templateService;
    private final ImageCreationService imageCreationService;

    // --- 提示语模板相关接口 ---

    /**
     * 保存或更新提示语模板
     */
    @PostMapping("/template/save/v1")
    public BaseResponse<Void> saveTemplate(@RequestBody @Valid ImagePromptTemplateReq request) {
        templateService.saveTemplate(request);
        return BaseResponse.success(null);
    }

    /**
     * 分页查询提示语模板
     */
    @PostMapping("/template/query/v1")
    public BaseResponse<PageResponse<ImagePromptTemplateResp>> queryTemplates(@RequestBody ImagePromptTemplateQueryReq request) {
        return BaseResponse.success(templateService.queryTemplates(request));
    }

    /**
     * 删除提示语模板 (逻辑删除)
     */
    @PostMapping("/template/delete/v1")
    public BaseResponse<Void> deleteTemplate(@RequestBody ImagePromptTemplateReq request) {
        if (request.getId() != null) {
            templateService.deleteTemplate(request.getId());
        }
        return BaseResponse.success(null);
    }

    /**
     * 获取所有启用的模板列表
     */
    @PostMapping("/template/list-all/v1")
    public BaseResponse<List<ImagePromptTemplateResp>> listAllActiveTemplates() {
        return BaseResponse.success(templateService.getAllActiveTemplates());
    }

    /**
     * 获取模板详情
     */
    @PostMapping("/template/get/v1")
    public BaseResponse<ImagePromptTemplateResp> getTemplate(@RequestBody ImagePromptTemplateReq request) {
        return BaseResponse.success(templateService.getTemplateById(request.getId()));
    }

    /**
     * 从创作结果保存为模板
     */
    @PostMapping("/template/save-from-result/v1")
    public BaseResponse<Void> saveTemplateFromResult(@RequestBody @Valid SaveTemplateFromResultReq request) {
        log.info("收到保存模板请求: templateContent={}, placeholderKeywords={}, resultId={}", 
                 request.getTemplateContent(), request.getPlaceholderKeywords(), request.getResultId());
        templateService.saveTemplateFromResult(request);
        return BaseResponse.success(null);
    }

    // --- 图片创作任务相关接口 ---

    /**
     * 提交图片创作任务
     */
    @PostMapping("/task/submit/v1")
    public BaseResponse<Long> submitTask(@RequestBody @Valid ImagePromptSubmitReq request) {
        return BaseResponse.success(imageCreationService.submitImageTask(request));
    }

    /**
     * 分页查询创作任务列表
     */
    @PostMapping("/task/query/v1")
    public BaseResponse<PageResponse<ImageGenerationTaskResp>> queryTasks(@RequestBody ImageTaskQueryReq request) {
        return BaseResponse.success(imageCreationService.queryTasks(request));
    }

    /**
     * 更新任务状态
     */
    @PostMapping("/task/update-status/v1")
    public BaseResponse<Void> updateTaskStatus(@RequestBody @Valid ImageTaskStatusUpdateReq request) {
        imageCreationService.updateTaskStatus(request);
        return BaseResponse.success(null);
    }

    /**
     * 获取任务详情（含生成结果）
     */
    @PostMapping("/task/detail/v1")
    public BaseResponse<ImageGenerationTaskResp> getTaskDetail(@RequestBody ImageTaskQueryReq request) {
        return BaseResponse.success(imageCreationService.getTaskDetail(request.getTaskId()));
    }

    /**
     * 获取最早的一条等待中的任务数据
     */
    @ApiLog("获取最早等待任务")
    @GetMapping("/task/earliest-waiting/v1")
    public BaseResponse<ImageGenerationTaskResp> getEarliestWaitingTask() {
        return BaseResponse.success(imageCreationService.getEarliestWaitingTask());
    }

    /**
     * 处理任务拉取成功回执
     */
    @ApiLog("任务拉取成功回执")
    @PostMapping("/task/pull-success/v1")
    public BaseResponse<Void> handleTaskPullSuccess(@RequestBody ImageTaskQueryReq request) {
        imageCreationService.handleTaskPullSuccess(request.getTaskId());
        return BaseResponse.success(null);
    }

    /**
     * 上报任务失败回执
     */
    @ApiLog("上报任务失败")
    @PostMapping("/task/report-failure/v1")
    public BaseResponse<Void> reportTaskFailure(@RequestBody @Valid ImageTaskReportFailureReq request) {
        imageCreationService.reportTaskFailure(request);
        return BaseResponse.success(null);
    }

    // --- 图片生成结果相关接口 ---

    /**
     * 分页查询创作结果列表
     */
    @PostMapping("/result/query/v1")
    public BaseResponse<PageResponse<ImageResultResp>> queryResults(@RequestBody ImageResultQueryReq request) {
        return BaseResponse.success(imageCreationService.queryResults(request));
    }

    /**
     * 注册生成结果
     */
    @ApiLog("注册生成结果")
    @PostMapping({"/result/register/v1", "/task/result/register/v1"})
    public BaseResponse<Void> registerResult(@RequestBody @Valid ImageResultRegisterReq request) {
        imageCreationService.registerResult(request);
        return BaseResponse.success(null);
    }
}
