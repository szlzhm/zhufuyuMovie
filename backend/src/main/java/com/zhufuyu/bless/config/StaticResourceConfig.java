package com.zhufuyu.bless.config;

import com.zhufuyu.bless.entity.SysConfigEntity;
import com.zhufuyu.bless.repository.SysConfigRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 静态资源访问配置
 */
@Configuration
public class StaticResourceConfig implements WebMvcConfigurer {

    private final SysConfigRepository sysConfigRepository;
    private String fileRootPath;

    public StaticResourceConfig(SysConfigRepository sysConfigRepository) {
        this.sysConfigRepository = sysConfigRepository;
    }

    @PostConstruct
    public void init() {
        // 直接从数据库读取配置，不经过权限检查
        SysConfigEntity config = sysConfigRepository.findByConfigKey("file.root.path")
                .orElse(null);
        if (config != null) {
            this.fileRootPath = config.getConfigValue();
        } else {
            // 默认路径
            this.fileRootPath = "E:/data/bless/";
        }
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 配置文件访问路径
        registry.addResourceHandler("/files/**")
                .addResourceLocations("file:" + fileRootPath);
    }
}
