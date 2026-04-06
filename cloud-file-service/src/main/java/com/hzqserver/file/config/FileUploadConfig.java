package com.hzqserver.file.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 文件上传配置类
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "file.upload")
public class FileUploadConfig {
    
    /**
     * 最大文件大小（默认1GB）
     */
    private Long maxFileSize = 1073741824L;
    
    /**
     * 允许的文件类型
     */
    private String allowedTypes = "jpg,jpeg,png,gif,pdf,doc,docx,xls,xlsx,zip,rar,mp4,avi,mov";
    
    /**
     * 最大分片大小限制（默认10MB，防止客户端传入过大分片）
     */
    private Long maxChunkSize = 10485760L;
    
    /**
     * 最小分片大小限制（默认1MB，防止客户端传入过小分片）
     */
    private Long minChunkSize = 1048576L;
}
