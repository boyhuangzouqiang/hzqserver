package com.hzqserver.springai;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Spring AI 应用程序启动类
 * 用于启动集成了OpenAI功能的Spring Boot应用程序
 */
@SpringBootApplication
public class SpringAIApplication {

    /**
     * 应用程序入口点
     * @param args 启动参数
     */
    public static void main(String[] args) {
        SpringApplication.run(SpringAIApplication.class, args);
    }

}