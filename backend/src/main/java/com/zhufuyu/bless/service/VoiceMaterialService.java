package com.zhufuyu.bless.service;

import com.zhufuyu.bless.model.request.material.VoiceMaterialCreateReq;
import com.zhufuyu.bless.model.request.material.VoiceMaterialListReq;
import com.zhufuyu.bless.model.request.material.VoiceMaterialStatusReq;
import com.zhufuyu.bless.model.request.material.VoiceMaterialUpdateReq;
import com.zhufuyu.bless.model.response.common.PageResponse;
import com.zhufuyu.bless.model.response.material.VoiceMaterialResp;

/**
 * 音色素材服务接口
 */
public interface VoiceMaterialService {

    /**
     * 创建音色素材
     */
    Long createVoiceMaterial(VoiceMaterialCreateReq request);

    /**
     * 分页查询音色素材列表
     */
    PageResponse<VoiceMaterialResp> listVoiceMaterials(VoiceMaterialListReq request);

    /**
     * 获取音色素材详情
     */
    VoiceMaterialResp getVoiceMaterialById(Long id);

    /**
     * 更新音色素材
     */
    void updateVoiceMaterial(VoiceMaterialUpdateReq request);

    /**
     * 切换音色素材状态
     */
    void toggleVoiceMaterialStatus(VoiceMaterialStatusReq request);
}
