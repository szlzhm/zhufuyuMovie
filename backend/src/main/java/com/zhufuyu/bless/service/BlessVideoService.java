package com.zhufuyu.bless.service;

import com.zhufuyu.bless.model.request.video.BlessVideoListReq;
import com.zhufuyu.bless.model.request.video.VideoPublishInfoUpdateReq;
import com.zhufuyu.bless.model.response.common.PageResponse;
import com.zhufuyu.bless.model.response.video.BlessVideoResp;

/**
 * 祝福语视频服务接口
 */
public interface BlessVideoService {

    /**
     * 分页查询视频列表
     */
    PageResponse<BlessVideoResp> listVideos(BlessVideoListReq request);

    /**
     * 获取视频详情
     */
    BlessVideoResp getVideoById(Long id);

    /**
     * 更新视频发布信息
     */
    void updateVideoPublishInfo(VideoPublishInfoUpdateReq request);
}
