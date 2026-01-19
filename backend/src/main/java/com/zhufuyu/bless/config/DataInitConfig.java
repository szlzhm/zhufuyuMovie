package com.zhufuyu.bless.config;

import com.zhufuyu.bless.entity.SysUserEntity;
import com.zhufuyu.bless.repository.SysUserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class DataInitConfig implements CommandLineRunner {

    private final SysUserRepository sysUserRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitConfig(SysUserRepository sysUserRepository, PasswordEncoder passwordEncoder) {
        this.sysUserRepository = sysUserRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (sysUserRepository.count() == 0) {
            SysUserEntity admin = new SysUserEntity();
            admin.setUsername("admin");
            admin.setPasswordHash(passwordEncoder.encode("admin123"));
            admin.setRole("ADMIN");
            admin.setStatus(1);
            sysUserRepository.save(admin);
            System.out.println("默认管理员账号已创建: admin / admin123");
        } else {
            System.out.println("数据库中已存在用户,跳过初始化");
        }
    }
}
