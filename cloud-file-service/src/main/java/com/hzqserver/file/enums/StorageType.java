package com.hzqserver.file.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 存储类型枚举
 */
@Getter
@AllArgsConstructor
public enum StorageType {
    
    /**
     * MinIO存储
     */
    MINIO("minio", "MinIO对象存储"),
    
    /**
     * 阿里云OSS存储
     */
    OSS("oss", "阿里云OSS对象存储");
    
    private final String code;
    private final String description;
    
    /**
     * 根据code获取枚举
     */
    public static StorageType fromCode(String code) {
        for (StorageType type : values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        throw new IllegalArgumentException("不支持的存储类型: " + code);
    }
}
