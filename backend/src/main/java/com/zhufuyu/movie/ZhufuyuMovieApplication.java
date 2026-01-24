package com.zhufuyu.movie;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication(scanBasePackages = {"com.zhufuyu.bless", "com.zhufuyu.movie"})
@EnableJpaRepositories(basePackages = {"com.zhufuyu.bless.repository"})
@EntityScan(basePackages = {"com.zhufuyu.bless.entity"})
@ConfigurationPropertiesScan
@EnableAsync
public class ZhufuyuMovieApplication {

    public static void main(String[] args) {
        // 设置系统属性以使用配置的超时时间
        System.setProperty("http.client.connect.timeout", System.getProperty("http.client.connect.timeout", "30"));
        System.setProperty("http.client.request.timeout", System.getProperty("http.client.request.timeout", "60"));
        SpringApplication.run(ZhufuyuMovieApplication.class, args);
    }
}