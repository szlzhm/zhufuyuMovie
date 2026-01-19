package com.zhufuyu.bless.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * HTTP客户端配置
 */
@Data
@Component
@ConfigurationProperties(prefix = "http-client")
public class HttpClientConfig {
    
    private Timeout timeout = new Timeout();
    
    @Data
    public static class Timeout {
        /**
         * 连接超时时间（秒）
         */
        private int connect = 30;
        
        /**
         * 请求超时时间（秒）
         */
        private int request = 60;
    }
}