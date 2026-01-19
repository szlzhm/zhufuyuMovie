package com.zhufuyu.bless.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * DashScope API配置
 */
@Component
@ConfigurationProperties(prefix = "dashscope")
public class DashScopeConfig {
    
    /**
     * API密钥
     */
    private String apiKey;
    
    /**
     * API基础URL
     */
    private String baseUrl = "https://dashscope.aliyuncs.com/api/v1/services/aigc/text2image/image";
    
    /**
     * 地域，默认为北京
     */
    private String region = "beijing";

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }
}