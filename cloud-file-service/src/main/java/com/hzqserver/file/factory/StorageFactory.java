package com.hzqserver.file.factory;

import com.hzqserver.file.config.StorageProperties;
import com.hzqserver.file.enums.StorageType;
import com.hzqserver.file.service.storage.StorageService;
import com.hzqserver.file.service.storage.impl.MinioStorageServiceImpl;
import com.hzqserver.file.service.storage.impl.OssStorageServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;

/**
 * 存储服务工厂类
 * 根据配置动态创建对应的存储服务实例
 */
@Slf4j
@Component
public class StorageFactory {
    
    @Autowired
    private StorageProperties storageProperties;
    
    @Autowired
    private MinioStorageServiceImpl minioStorageService;
    
    @Autowired
    private OssStorageServiceImpl ossStorageService;
    
    private StorageService storageService;
    
    @PostConstruct
    public void init() {
        StorageType storageType = storageProperties.getStorageType();
        log.info("初始化存储服务工厂, 存储类型: {}", storageType.getDescription());
        
        switch (storageType) {
            case MINIO:
                storageService = minioStorageService;
                log.info("使用MinIO存储服务");
                break;
            case OSS:
                storageService = ossStorageService;
                log.info("使用阿里云OSS存储服务");
                break;
            default:
                throw new IllegalArgumentException("不支持的存储类型: " + storageType.getCode());
        }
        
        log.info("存储服务工厂初始化完成: {}", storageService.getStorageType());
    }
    
    /**
     * 获取存储服务实例
     */
    public StorageService getStorageService() {
        if (storageService == null) {
            throw new IllegalStateException("存储服务未初始化，请检查配置");
        }
        return storageService;
    }
    
    /**
     * 获取当前存储类型
     */
    public String getCurrentStorageType() {
        return storageProperties.getType();
    }
    
    /**
     * 动态切换存储服务（运行时切换，谨慎使用）
     */
    public void switchStorage(StorageType storageType) {
        log.info("动态切换存储服务: {}", storageType.getDescription());
        
        switch (storageType) {
            case MINIO:
                storageService = minioStorageService;
                break;
            case OSS:
                storageService = ossStorageService;
                break;
            default:
                throw new IllegalArgumentException("不支持的存储类型: " + storageType.getCode());
        }
        
        log.info("存储服务切换成功: {}", storageService.getStorageType());
    }
}
