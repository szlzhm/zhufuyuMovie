package com.zhufuyu.bless.model.request.config;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class EmotionUpdateReq {

    @NotNull(message = "ID不能为空")
    private Long id;

    @NotBlank(message = "情绪名称不能为空")
    private String emotionName;

    private String usageDesc;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
