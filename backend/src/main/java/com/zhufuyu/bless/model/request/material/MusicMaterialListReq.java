package com.zhufuyu.bless.model.request.material;

import com.zhufuyu.bless.model.request.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class MusicMaterialListReq extends PageRequest {
    private String name;      // 音乐名称(模糊查询)
    private Long emotion;     // 情绪ID

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getEmotion() {
        return emotion;
    }

    public void setEmotion(Long emotion) {
        this.emotion = emotion;
    }
}