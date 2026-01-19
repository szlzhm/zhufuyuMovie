package com.zhufuyu.bless.model.request.material;

import lombok.Data;

@Data
public class MusicMaterialCreateReq {
    private String name;          // 背景音乐名称,必填
    private String filePath;      // 文件路径,必填
    private String description;   // 简介,可空
    private Long emotion;         // 情绪ID,可空

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getEmotion() {
        return emotion;
    }

    public void setEmotion(Long emotion) {
        this.emotion = emotion;
    }
}