package com.zhufuyu.bless.model.request.video;

import com.zhufuyu.bless.model.request.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class BlessVideoListReq extends PageRequest {
    private String title;         // 标题模糊查询
    private String sortField;     // 排序字段: createdTime / generatedTime
    private String sortOrder;     // 排序方式: asc / desc

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSortField() {
        return sortField;
    }

    public void setSortField(String sortField) {
        this.sortField = sortField;
    }

    public String getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(String sortOrder) {
        this.sortOrder = sortOrder;
    }
}