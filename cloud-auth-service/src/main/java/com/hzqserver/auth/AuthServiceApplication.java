package com.hzqserver.auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * 认证服务应用启动类
 * 启动Spring Boot应用并启用服务发现功能
 */
@SpringBootApplication
@EnableDiscoveryClient
public class AuthServiceApplication {
    /**
     * 应用程序入口点
     * 启动认证服务应用
     * 
     * @param args 命令行参数
     */
    public static void main(String[] args) {
        SpringApplication.run(AuthServiceApplication.class, args);
    }
}