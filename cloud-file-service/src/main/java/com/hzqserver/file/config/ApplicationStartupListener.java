package com.hzqserver.file.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

/**
 * 应用启动完成监听器
 * 确保应用完全启动后再进行服务注册
 */
@Slf4j
@Component
public class ApplicationStartupListener implements ApplicationListener<ApplicationReadyEvent> {
    
    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        log.info("========================================");
        log.info("应用启动完成，准备注册到Nacos");
        log.info("应用名称: {}", event.getApplicationContext().getApplicationName());
        log.info("========================================");
        
        // 这里可以添加额外的初始化逻辑
        // Nacos会自动在应用就绪后注册服务
    }
}
