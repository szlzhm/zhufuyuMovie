package com.zhufuyu.bless.service;

import com.zhufuyu.bless.model.request.config.FileRootPathUpdateReq;
import com.zhufuyu.bless.model.response.config.FileRootPathResp;

public interface FileConfigService {

    /**
     * 查询文件根目录（需要管理员权限）
     */
    FileRootPathResp getFileRootPath();
    
    /**
     * 获取文件根目录配置值（内部使用，无需权限检查）
     */
    String getFileRootPathValue();

    /**
     * 更新文件根目录
     */
    void updateFileRootPath(FileRootPathUpdateReq request);
}
