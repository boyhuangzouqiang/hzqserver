package com.hzqserver.file.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 阿里云OSS配置类
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "aliyun.oss")
public class OssConfig {
    
    /**
     * OSS服务端点
     * 例如：oss-cn-hangzhou.aliyuncs.com
     */
    private String endpoint;
    
    /**
     * 访问密钥ID
     */
    private String accessKeyId;
    
    /**
     * 访问密钥Secret
     */
    private String accessKeySecret;
    
    /**
     * 默认存储桶名称
     */
    private String bucketName;
    
    /**
     * 自定义域名（可选）
     * 如果配置了自定义域名，则使用自定义域名生成文件访问URL
     */
    private String customDomain;
}
