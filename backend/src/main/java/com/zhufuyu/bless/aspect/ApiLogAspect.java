package com.zhufuyu.bless.aspect;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zhufuyu.bless.annotation.ApiLog;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * API日志切面
 * 记录API调用的请求时间、URL、参数、响应内容和响应时长
 */
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class ApiLogAspect {

    private final ObjectMapper objectMapper;

    /**
     * 敏感字段列表，这些字段的值会被脱敏
     */
    private static final Set<String> SENSITIVE_FIELDS = new HashSet<>(Arrays.asList(
            "password", "pwd", "token", "secret", "apiKey", "api_key",
            "accessToken", "access_token", "refreshToken", "refresh_token"
    ));

    @Around("@annotation(com.zhufuyu.bless.annotation.ApiLog)")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        
        // 获取请求信息
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes != null ? attributes.getRequest() : null;
        
        // 获取注解信息
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        ApiLog apiLog = method.getAnnotation(ApiLog.class);
        
        String url = request != null ? request.getRequestURI() : "UNKNOWN";
        String httpMethod = request != null ? request.getMethod() : "UNKNOWN";
        String description = apiLog.value();
        
        // 构建日志前缀
        StringBuilder logBuilder = new StringBuilder();
        logBuilder.append("\n========================================");
        logBuilder.append("\n[API-LOG] ").append(description.isEmpty() ? "" : "[" + description + "] ");
        logBuilder.append("[").append(httpMethod).append("] ").append(url);
        logBuilder.append("\n请求时间: ").append(new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new java.util.Date(startTime)));
        
        // 记录请求参数
        if (apiLog.logRequest()) {
            Object[] args = joinPoint.getArgs();
            if (args != null && args.length > 0) {
                try {
                    String requestJson = objectMapper.writeValueAsString(args);
                    // 简单脱敏处理
                    requestJson = maskSensitiveData(requestJson);
                    logBuilder.append("\n请求参数: ").append(requestJson);
                } catch (Exception e) {
                    logBuilder.append("\n请求参数: [无法序列化]");
                }
            }
        }
        
        Object result = null;
        Throwable exception = null;
        
        try {
            // 执行目标方法
            result = joinPoint.proceed();
            return result;
        } catch (Throwable e) {
            exception = e;
            throw e;
        } finally {
            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;
            
            logBuilder.append("\n响应时长: ").append(duration).append("ms");
            
            // 记录响应内容
            if (apiLog.logResponse() && result != null) {
                try {
                    String responseJson = objectMapper.writeValueAsString(result);
                    // 限制响应内容长度，避免日志过大
                    if (responseJson.length() > 1000) {
                        responseJson = responseJson.substring(0, 1000) + "... [截断]";
                    }
                    logBuilder.append("\n响应内容: ").append(responseJson);
                } catch (Exception e) {
                    logBuilder.append("\n响应内容: [无法序列化]");
                }
            }
            
            // 记录异常信息
            if (exception != null) {
                logBuilder.append("\n异常信息: ").append(exception.getClass().getName())
                           .append(" - ").append(exception.getMessage());
            }
            
            logBuilder.append("\n========================================");
            
            // 输出日志
            if (exception != null) {
                log.error(logBuilder.toString());
            } else {
                log.info(logBuilder.toString());
            }
        }
    }

    /**
     * 简单的敏感数据脱敏
     */
    private String maskSensitiveData(String json) {
        for (String field : SENSITIVE_FIELDS) {
            // 匹配 "field":"value" 或 "field":value 格式
            json = json.replaceAll(
                    "\"" + field + "\"\\s*:\\s*\"[^\"]*\"",
                    "\"" + field + "\":\"***\""
            );
        }
        return json;
    }
}
