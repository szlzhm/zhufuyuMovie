package com.zhufuyu.bless.security;

import com.zhufuyu.bless.exception.BizException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Slf4j
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String AUTH_HEADER = "Authorization";
    private static final String AUTH_PREFIX = "Bearer ";

    private final JwtUtil jwtUtil;

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    public JwtAuthenticationFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            String uri = request.getRequestURI();

            logger.info("Request URI: {}", uri);
            if (isPermitAll(uri)) {
                // 特殊逻辑：获取最早等待任务接口和回执接口限制只能本地访问
                if ("/api/image-creation/task/earliest-waiting/v1".equals(uri) || 
                    "/api/image-creation/task/pull-success/v1".equals(uri) ||
                    "/api/image-creation/task/report-failure/v1".equals(uri) ||
                    "/api/image-creation/task/result/register/v1".equals(uri) ||
                    "/api/tasks/fetch".equals(uri)) {
                    String remoteAddr = request.getRemoteAddr();
                    if (!"127.0.0.1".equals(remoteAddr) && !"0:0:0:0:0:0:0:1".equals(remoteAddr)) {
                        throw new BizException(10004, "该接口仅允许本地访问");
                    }
                }
                filterChain.doFilter(request, response);
                return;
            }

            String authHeader = request.getHeader(AUTH_HEADER);
            if (!StringUtils.hasText(authHeader) || !authHeader.startsWith(AUTH_PREFIX)) {
                throw new BizException(10003, "未登录或登录已失效");
            }

            String token = authHeader.substring(AUTH_PREFIX.length()).trim();
            Claims claims = jwtUtil.parseToken(token);

            String sub = claims.getSubject();
            String username = claims.get("username", String.class);
            String role = claims.get("role", String.class);

            if (!StringUtils.hasText(sub) || !StringUtils.hasText(username) || !StringUtils.hasText(role)) {
                throw new BizException(10003, "未登录或登录已失效");
            }

            Long userId = Long.valueOf(sub);
            LoginUserContext.set(new LoginUserContext.LoginUserInfo(userId, username, role));

            filterChain.doFilter(request, response);
        } catch (BizException e) {
            throw e;
        } catch (ExpiredJwtException e) {
            throw new BizException(10003, "登录已过期，请重新登录");
        } catch (JwtException e) {
            throw new BizException(10003, "未登录或登录已失效");
        } finally {
            LoginUserContext.clear();
        }
    }

    private boolean isPermitAll(String uri) {
        if (uri.startsWith("/api/auth/login")) {
            return true;
        }
        if (uri.startsWith("/files/")) {
            return true;
        }
        if (uri.startsWith("/api/image/")) {
            return true;
        }
        if (uri.startsWith("/api/image-creation/image")) {
            return true;
        }
        if ("/api/image-creation/task/earliest-waiting/v1".equals(uri) ||
            "/api/image-creation/task/pull-success/v1".equals(uri) ||
            "/api/image-creation/task/report-failure/v1".equals(uri) ||
            "/api/image-creation/task/result/register/v1".equals(uri) ||
            "/api/tasks/fetch".equals(uri)) {
            return true;
        }
        // 根据需要增加更多免登录路径
        return false;
    }
}
