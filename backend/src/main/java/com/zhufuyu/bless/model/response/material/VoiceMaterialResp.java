package com.zhufuyu.bless.model.response.material;

import lombok.Data;

/**
 * 音色素材响应
 */
@Data
public class VoiceMaterialResp {
    
    private Long id;
    
    private String name;
    
    private String gender;
    
    private String language;
    
    private String ageGroup;
    
    private String type;
    
    private Integer status;
    
    private String createdTime;
}
