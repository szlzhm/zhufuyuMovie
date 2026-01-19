package com.zhufuyu.bless.model.request.material;

import com.zhufuyu.bless.model.request.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 音色素材列表查询请求
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class VoiceMaterialListReq extends PageRequest {
    
    private String name;
    
    private String gender;
    
    private String language;
}
