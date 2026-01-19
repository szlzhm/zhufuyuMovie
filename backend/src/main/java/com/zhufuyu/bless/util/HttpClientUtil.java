package com.zhufuyu.bless.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Map;

/**
 * HTTP客户端工具类
 */
public class HttpClientUtil {
    
    private static final Logger logger = LoggerFactory.getLogger(HttpClientUtil.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();
    
    // 从系统属性或默认值获取超时时间
    private static final int CONNECT_TIMEOUT_SECONDS = Integer.parseInt(System.getProperty("http.client.connect.timeout", "30"));
    private static final int REQUEST_TIMEOUT_SECONDS = Integer.parseInt(System.getProperty("http.client.request.timeout", "60"));
    
    private static final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(CONNECT_TIMEOUT_SECONDS))
            .build();
    
    /**
     * 发送POST请求
     * @param url URL
     * @param headers 请求头
     * @param requestBody 请求体
     * @return 响应字符串
     * @throws RuntimeException 网络异常
     */
    public static String post(String url, Map<String, String> headers, Object requestBody) {
        try {
            String jsonBody = objectMapper.writeValueAsString(requestBody);
            
            HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .timeout(Duration.ofSeconds(REQUEST_TIMEOUT_SECONDS));
            
            // 设置默认Content-Type，如果用户未提供
            boolean hasContentType = false;
            if (headers != null) {
                for (Map.Entry<String, String> entry : headers.entrySet()) {
                    if (entry.getKey().equalsIgnoreCase("Content-Type")) {
                        hasContentType = true;
                        break;
                    }
                }
            }
            
            if (!hasContentType) {
                requestBuilder.header("Content-Type", "application/json");
            }
            
            // 添加自定义请求头
            if (headers != null) {
                for (Map.Entry<String, String> entry : headers.entrySet()) {
                    requestBuilder.header(entry.getKey(), entry.getValue());
                }
            }
            
            HttpRequest request = requestBuilder
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();
            
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            
            if (response.statusCode() != 200) {
                throw new RuntimeException("HTTP请求失败，状态码: " + response.statusCode() + ", 响应: " + response.body());
            }
            
            return response.body();
        } catch (IOException | InterruptedException e) {
            logger.error("发送HTTP POST请求失败", e);
            Thread.currentThread().interrupt();
            throw new RuntimeException("发送HTTP请求失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 发送GET请求
     * @param url URL
     * @param headers 请求头
     * @return 响应字符串
     * @throws RuntimeException 网络异常
     */
    public static String get(String url, Map<String, String> headers) {
        try {
            HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .timeout(Duration.ofSeconds(REQUEST_TIMEOUT_SECONDS));
            
            // 添加请求头
            if (headers != null) {
                for (Map.Entry<String, String> entry : headers.entrySet()) {
                    requestBuilder.header(entry.getKey(), entry.getValue());
                }
            }
            
            HttpRequest request = requestBuilder
                    .GET()
                    .build();
            
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            
            if (response.statusCode() != 200) {
                throw new RuntimeException("HTTP请求失败，状态码: " + response.statusCode() + ", 响应: " + response.body());
            }
            
            return response.body();
        } catch (IOException | InterruptedException e) {
            logger.error("发送HTTP GET请求失败", e);
            Thread.currentThread().interrupt();
            throw new RuntimeException("发送HTTP请求失败: " + e.getMessage(), e);
        }
    }
}