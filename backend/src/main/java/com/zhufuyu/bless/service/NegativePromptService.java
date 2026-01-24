package com.zhufuyu.bless.service;

import com.zhufuyu.bless.model.request.NegativePromptQueryReq;
import com.zhufuyu.bless.model.request.NegativePromptReq;
import com.zhufuyu.bless.model.response.NegativePromptResp;
import com.zhufuyu.bless.model.response.common.PageResponse;
import java.util.List;

/**
 * 负面提示语服务接口
 */
public interface NegativePromptService {

    void save(NegativePromptReq request);

    PageResponse<NegativePromptResp> query(NegativePromptQueryReq request);

    void delete(Long id);

    List<NegativePromptResp> listAll();
}
