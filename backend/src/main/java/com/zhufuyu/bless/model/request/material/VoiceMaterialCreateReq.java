package com.zhufuyu.bless.model.request.material;

import lombok.Data;

/**
 * 音色素材创建请求
 */
@Data
public class VoiceMaterialCreateReq {
    
    private String name;
    
    private String gender;
    
    private String language;
    
    private String ageGroup;
    
    private String type;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getAgeGroup() {
        return ageGroup;
    }

    public void setAgeGroup(String ageGroup) {
        this.ageGroup = ageGroup;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}