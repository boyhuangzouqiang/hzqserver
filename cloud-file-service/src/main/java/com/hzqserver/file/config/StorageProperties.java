package com.hzqserver.file.config;

import com.hzqserver.file.enums.StorageType;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 统一存储配置类
 * 支持通过配置切换不同的存储后端（MinIO/OSS）
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "file.storage")
public class StorageProperties {
    
    /**
     * 存储类型：minio 或 oss
     */
    private String type = "minio";
    
    /**
     * 默认存储桶名称
     */
    private String defaultBucket = "file-upload";
    
    /**
     * 获取存储类型枚举
     */
    public StorageType getStorageType() {
        return StorageType.fromCode(type);
    }
    
    /**
     * 是否为MinIO存储
     */
    public boolean isMinio() {
        return StorageType.MINIO.getCode().equals(type);
    }
    
    /**
     * 是否为OSS存储
     */
    public boolean isOss() {
        return StorageType.OSS.getCode().equals(type);
    }
}
