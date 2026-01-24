package com.zhufuyu.bless.service.impl;

import com.zhufuyu.bless.entity.SysConfigEntity;
import com.zhufuyu.bless.exception.BizException;
import com.zhufuyu.bless.model.request.config.FileRootPathUpdateReq;
import com.zhufuyu.bless.model.response.config.FileRootPathResp;
import com.zhufuyu.bless.repository.SysConfigRepository;
import com.zhufuyu.bless.security.LoginUserContext;
import com.zhufuyu.bless.service.FileConfigService;
import org.springframework.stereotype.Service;

@Service
public class FileConfigServiceImpl implements FileConfigService {

    private static final String FILE_ROOT_PATH_KEY = "file.root.path";

    private final SysConfigRepository sysConfigRepository;

    public FileConfigServiceImpl(SysConfigRepository sysConfigRepository) {
        this.sysConfigRepository = sysConfigRepository;
    }

    private void checkAdmin() {
        LoginUserContext.LoginUserInfo info = LoginUserContext.get();
        if (info == null || !"ADMIN".equals(info.getRole())) {
            throw new BizException(10004, "无权限访问");
        }
    }

    @Override
    public FileRootPathResp getFileRootPath() {
        checkAdmin();

        SysConfigEntity config = sysConfigRepository.findByConfigKey(FILE_ROOT_PATH_KEY)
                .orElseThrow(() -> new BizException(30001, "文件根目录配置不存在"));

        FileRootPathResp resp = new FileRootPathResp();
        resp.setRootPath(config.getConfigValue());
        return resp;
    }
    
    @Override
    public String getFileRootPathValue() {
        // 无需权限检查，供内部使用
        SysConfigEntity config = sysConfigRepository.findByConfigKey(FILE_ROOT_PATH_KEY)
                .orElse(null);
        return config != null ? config.getConfigValue() : "E:/data/bless/";
    }

    @Override
    public void updateFileRootPath(FileRootPathUpdateReq request) {
        checkAdmin();

        SysConfigEntity config = sysConfigRepository.findByConfigKey(FILE_ROOT_PATH_KEY)
                .orElseThrow(() -> new BizException(30001, "文件根目录配置不存在"));

        config.setConfigValue(request.getRootPath());
        sysConfigRepository.save(config);
    }
}
