package com.zhufuyu.bless.service;

import com.zhufuyu.bless.model.request.material.TextMaterialCreateReq;
import com.zhufuyu.bless.model.request.material.TextMaterialListReq;
import com.zhufuyu.bless.model.request.material.TextMaterialUpdateReq;
import com.zhufuyu.bless.model.response.common.PageResponse;
import com.zhufuyu.bless.model.response.material.TextMaterialResp;

public interface TextMaterialService {
    
    Long createTextMaterial(TextMaterialCreateReq request);
    
    PageResponse<TextMaterialResp> listTextMaterials(TextMaterialListReq request);
    
    TextMaterialResp getTextMaterialById(Long id);
    
    void updateTextMaterial(TextMaterialUpdateReq request);
}
