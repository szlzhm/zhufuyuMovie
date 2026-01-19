package com.zhufuyu.bless.model.request.material;

import lombok.Data;

/**
 * 音色素材更新请求
 */
@Data
public class VoiceMaterialUpdateReq {
    
    private Long id;
    
    private String name;
    
    private String gender;
    
    private String language;
    
    private String ageGroup;
    
    private String type;
}
