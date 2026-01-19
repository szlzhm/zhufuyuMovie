package com.zhufuyu.bless.security;

public class LoginUserContext {

    private static final ThreadLocal<LoginUserInfo> CONTEXT = new ThreadLocal<>();

    public static void set(LoginUserInfo info) {
        CONTEXT.set(info);
    }

    public static LoginUserInfo get() {
        return CONTEXT.get();
    }

    public static void clear() {
        CONTEXT.remove();
    }

    public static class LoginUserInfo {
        private Long userId;
        private String username;
        private String role;

        public LoginUserInfo() {
        }

        public LoginUserInfo(Long userId, String username, String role) {
            this.userId = userId;
            this.username = username;
            this.role = role;
        }

        public Long getUserId() {
            return userId;
        }

        public void setUserId(Long userId) {
            this.userId = userId;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getRole() {
            return role;
        }

        public void setRole(String role) {
            this.role = role;
        }
    }
}
