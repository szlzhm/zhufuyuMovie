package com.zhufuyu.bless.model.request.config;

import jakarta.validation.constraints.NotBlank;

public class EmotionCreateReq {

    @NotBlank(message = "情绪编码不能为空")
    private String emotionCode;

    @NotBlank(message = "情绪名称不能为空")
    private String emotionName;

    private String usageDesc;

    public String getEmotionCode() {
        return emotionCode;
    }

    public void setEmotionCode(String emotionCode) {
        this.emotionCode = emotionCode;
    }

    public String getEmotionName() {
        return emotionName;
    }

    public void setEmotionName(String emotionName) {
        this.emotionName = emotionName;
    }

    public String getUsageDesc() {
        return usageDesc;
    }

    public void setUsageDesc(String usageDesc) {
        this.usageDesc = usageDesc;
    }
}
