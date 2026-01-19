-- 创建数据库（如果不存在）
CREATE DATABASE IF NOT EXISTS zhufuyu_bless
  DEFAULT CHARACTER SET utf8mb4
  DEFAULT COLLATE utf8mb4_general_ci;

-- 使用该数据库
USE zhufuyu_bless;

-- 创建系统用户表
CREATE TABLE IF NOT EXISTS sys_user (
    id              BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    username        VARCHAR(64)     NOT NULL COMMENT '登录账号，唯一',
    password_hash   VARCHAR(255)    NOT NULL COMMENT '密码哈希（如BCrypt）',
    role            VARCHAR(32)     NOT NULL COMMENT '角色：ADMIN / USER',
    status          TINYINT         NOT NULL DEFAULT 1 COMMENT '状态：1=启用，0=禁用',
    last_login_time DATETIME        NULL COMMENT '最近登录时间',
    created_time    DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_time    DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_sys_user_username (username),
    KEY idx_sys_user_role (role),
    KEY idx_sys_user_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统用户表';
