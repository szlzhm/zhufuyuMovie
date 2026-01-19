package com.zhufuyu.bless.model.response.common;

import lombok.Data;

import java.util.List;

/**
 * 分页响应类
 */
@Data
public class PageResponse<T> {
    
    private Integer pageNo;
    
    private Integer pageSize;
    
    private Long total;
    
    private List<T> list;

    public PageResponse(Integer pageNo, Integer pageSize, Long total, List<T> list) {
        this.pageNo = pageNo;
        this.pageSize = pageSize;
        this.total = total;
        this.list = list;
    }
}
