package com.hzqserver.file.service.storage;

import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.List;

/**
 * 对象存储服务统一接口
 * 基于S3协议，支持MinIO和阿里云OSS等不同存储后端
 */
public interface StorageService {
    
    /**
     * 上传文件
     * @param bucketName 存储桶名称
     * @param objectName 对象名称
     * @param inputStream 文件输入流
     * @param size 文件大小
     * @param contentType 内容类型
     * @return 文件访问URL
     */
    String uploadFile(String bucketName, String objectName, InputStream inputStream, 
                      long size, String contentType);
    
    /**
     * 上传MultipartFile
     * @param bucketName 存储桶名称
     * @param objectName 对象名称
     * @param file 文件
     * @return 文件访问URL
     */
    default String uploadFile(String bucketName, String objectName, MultipartFile file) {
        try {
            return uploadFile(bucketName, objectName, file.getInputStream(), 
                    file.getSize(), file.getContentType());
        } catch (Exception e) {
            throw new RuntimeException("文件上传失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 删除文件
     * @param bucketName 存储桶名称
     * @param objectName 对象名称
     */
    void deleteFile(String bucketName, String objectName);
    
    /**
     * 批量删除文件
     * @param bucketName 存储桶名称
     * @param objectNames 对象名称列表
     */
    void deleteFiles(String bucketName, List<String> objectNames);
    
    /**
     * 检查文件是否存在
     * @param bucketName 存储桶名称
     * @param objectName 对象名称
     * @return 是否存在
     */
    boolean fileExists(String bucketName, String objectName);
    
    /**
     * 获取文件访问URL
     * @param bucketName 存储桶名称
     * @param objectName 对象名称
     * @return 文件访问URL
     */
    String getFileUrl(String bucketName, String objectName);
    
    /**
     * 获取文件访问URL（带过期时间）
     * @param bucketName 存储桶名称
     * @param objectName 对象名称
     * @param expiryMinutes 过期时间（分钟）
     * @return 预签名URL
     */
    String getPresignedUrl(String bucketName, String objectName, int expiryMinutes);
    
    /**
     * 合并分片文件
     * @param bucketName 存储桶名称
     * @param targetObjectName 目标对象名称
     * @param sourceObjectNames 源对象名称列表（按顺序）
     * @param contentType 内容类型
     * @return 合并后的文件访问URL
     */
    String composeFile(String bucketName, String targetObjectName, 
                       List<String> sourceObjectNames, String contentType);
    
    /**
     * 下载文件
     * @param bucketName 存储桶名称
     * @param objectName 对象名称
     * @return 文件输入流
     */
    InputStream downloadFile(String bucketName, String objectName);
    
    /**
     * 下载文件指定范围的数据（支持断点续传）
     * @param bucketName 存储桶名称
     * @param objectName 对象名称
     * @param startByte 起始字节位置
     * @param endByte 结束字节位置
     * @return 文件输入流
     */
    InputStream downloadFileRange(String bucketName, String objectName, long startByte, long endByte);
    
    /**
     * 获取存储类型
     * @return 存储类型
     */
    String getStorageType();
    
    /**
     * 确保存储桶存在
     * @param bucketName 存储桶名称
     */
    void ensureBucketExists(String bucketName);
}
