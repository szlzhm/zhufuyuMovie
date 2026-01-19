package com.zhufuyu.bless.model.request.common;

/**
 * 分页请求基类
 */
public class PageRequest {
    
    private Integer pageNo = 1;
    
    private Integer pageSize = 10;
    
    /**
     * 排序字段，如: name, createdTime
     */
    private String sortField;
    
    /**
     * 排序方式：asc 或 desc
     */
    private String sortOrder;
    
    public Integer getPageNo() {
        return pageNo;
    }

    public void setPageNo(Integer pageNo) {
        this.pageNo = pageNo;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
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
