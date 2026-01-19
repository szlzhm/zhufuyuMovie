package com.zhufuyu.bless.model.request.material;

import lombok.Data;

/**
 * 音色素材状态切换请求
 */
@Data
public class VoiceMaterialStatusReq {
    
    private Long id;
    
    private Integer status;
}
