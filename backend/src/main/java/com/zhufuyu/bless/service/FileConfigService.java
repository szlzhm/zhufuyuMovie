package com.zhufuyu.bless.service;

import com.zhufuyu.bless.model.request.config.FileRootPathUpdateReq;
import com.zhufuyu.bless.model.response.config.FileRootPathResp;

public interface FileConfigService {

    /**
     * 查询文件根目录
     */
    FileRootPathResp getFileRootPath();

    /**
     * 更新文件根目录
     */
    void updateFileRootPath(FileRootPathUpdateReq request);
}
