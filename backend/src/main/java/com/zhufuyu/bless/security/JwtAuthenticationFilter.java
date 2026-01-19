package com.zhufuyu.bless.security;

import com.zhufuyu.bless.exception.BizException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String AUTH_HEADER = "Authorization";
    private static final String AUTH_PREFIX = "Bearer ";

    private final JwtUtil jwtUtil;

    public JwtAuthenticationFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            String uri = request.getRequestURI();

            if (isPermitAll(uri)) {
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
        // 根据需要增加更多免登录路径
        return false;
    }
}
