package com.zhufuyu.bless.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 文生图配置
 */
@Data
@Component
@ConfigurationProperties(prefix = "text-to-image")
public class TextToImageConfig {
    
    private Model model = new Model();
    
    @Data
    public static class Model {
        private String name = "Qwen-Image-Max"; // 默认模型名称
        private Double defaultPromptStrength = 0.8;
        private Double defaultNegativePromptStrength = 0.2;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Double getDefaultPromptStrength() {
            return defaultPromptStrength;
        }

        public void setDefaultPromptStrength(Double defaultPromptStrength) {
            this.defaultPromptStrength = defaultPromptStrength;
        }

        public Double getDefaultNegativePromptStrength() {
            return defaultNegativePromptStrength;
        }

        public void setDefaultNegativePromptStrength(Double defaultNegativePromptStrength) {
            this.defaultNegativePromptStrength = defaultNegativePromptStrength;
        }
    }
    
    private Api api = new Api();
    
    @Data
    public static class Api {
        private String syncUrl = "https://dashscope.aliyuncs.com/api/v1/services/aigc/multimodal-generation/generation";

        public String getSyncUrl() {
            return syncUrl;
        }

        public void setSyncUrl(String syncUrl) {
            this.syncUrl = syncUrl;
        }
    }

    public Model getModel() {
        return model;
    }

    public void setModel(Model model) {
        this.model = model;
    }

    public Api getApi() {
        return api;
    }

    public void setApi(Api api) {
        this.api = api;
    }
}