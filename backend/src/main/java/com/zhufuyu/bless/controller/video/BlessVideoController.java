package com.zhufuyu.bless.controller.video;

import com.zhufuyu.bless.model.common.BaseResponse;
import com.zhufuyu.bless.model.request.video.BlessVideoListReq;
import com.zhufuyu.bless.model.request.video.VideoPublishInfoUpdateReq;
import com.zhufuyu.bless.model.response.common.PageResponse;
import com.zhufuyu.bless.model.response.video.BlessVideoResp;
import com.zhufuyu.bless.service.BlessVideoService;
import org.springframework.web.bind.annotation.*;

/**
 * 祝福语视频管理
 */
@RestController
@RequestMapping("/api/video")
public class BlessVideoController {

    private final BlessVideoService blessVideoService;

    public BlessVideoController(BlessVideoService blessVideoService) {
        this.blessVideoService = blessVideoService;
    }

    /**
     * 分页查询视频列表
     */
    @PostMapping("/list/query/v1")
    public BaseResponse<PageResponse<BlessVideoResp>> listVideos(@RequestBody BlessVideoListReq request) {
        PageResponse<BlessVideoResp> response = blessVideoService.listVideos(request);
        return BaseResponse.success(response);
    }

    /**
     * 获取视频详情
     */
    @PostMapping("/detail/query/v1")
    public BaseResponse<BlessVideoResp> getVideoDetail(@RequestBody Long id) {
        BlessVideoResp response = blessVideoService.getVideoById(id);
        return BaseResponse.success(response);
    }

    /**
     * 更新视频发布信息
     */
    @PostMapping("/publish-info/update/v1")
    public BaseResponse<Void> updateVideoPublishInfo(@RequestBody VideoPublishInfoUpdateReq request) {
        blessVideoService.updateVideoPublishInfo(request);
        return BaseResponse.success(null);
    }
}
