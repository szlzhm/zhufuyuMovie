package com.zhufuyu.bless.model.request.config;

import jakarta.validation.constraints.NotBlank;

public class FileRootPathUpdateReq {

    @NotBlank(message = "文件根目录不能为空")
    private String rootPath;

    public String getRootPath() {
        return rootPath;
    }

    public void setRootPath(String rootPath) {
        this.rootPath = rootPath;
    }
}
