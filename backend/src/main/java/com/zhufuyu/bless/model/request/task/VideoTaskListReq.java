package com.zhufuyu.bless.model.request.task;

import com.zhufuyu.bless.model.request.common.PageRequest;

public class VideoTaskListReq extends PageRequest {
    private String taskName;          // 任务名称模糊查询
    private String batchName;         // 批次名称模糊查询
    private String textMaterialName;  // 文案名称模糊查询
    
    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getBatchName() {
        return batchName;
    }

    public void setBatchName(String batchName) {
        this.batchName = batchName;
    }

    public String getTextMaterialName() {
        return textMaterialName;
    }

    public void setTextMaterialName(String textMaterialName) {
        this.textMaterialName = textMaterialName;
    }
}
