package com.hzqserver.file.service.storage.impl;

import com.hzqserver.file.config.MinioConfig;
import com.hzqserver.file.service.storage.StorageService;
import io.minio.*;
import io.minio.errors.*;
import io.minio.messages.DeleteObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * MinIO存储服务实现
 * 基于MinIO Java SDK实现S3兼容的对象存储操作
 */
@Slf4j
@Service
public class MinioStorageServiceImpl implements StorageService {
    
    @Autowired
    private MinioClient minioClient;
    
    @Autowired
    private MinioConfig minioConfig;
    
    @Override
    public String uploadFile(String bucketName, String objectName, InputStream inputStream,
                             long size, String contentType) {
        try {
            log.info("MinIO上传文件: bucket={}, object={}, size={}", bucketName, objectName, size);
            
            // 确保存储桶存在
            ensureBucketExists(bucketName);
            
            // 上传文件
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .stream(inputStream, size, -1)
                            .contentType(contentType != null ? contentType : "application/octet-stream")
                            .build()
            );
            
            // 返回文件访问URL
            return getFileUrl(bucketName, objectName);
            
        } catch (Exception e) {
            log.error("MinIO文件上传失败", e);
            throw new RuntimeException("MinIO文件上传失败: " + e.getMessage(), e);
        }
    }
    
    @Override
    public void deleteFile(String bucketName, String objectName) {
        try {
            log.info("MinIO删除文件: bucket={}, object={}", bucketName, objectName);
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .build()
            );
        } catch (Exception e) {
            log.error("MinIO文件删除失败", e);
            throw new RuntimeException("MinIO文件删除失败: " + e.getMessage(), e);
        }
    }
    
    @Override
    public void deleteFiles(String bucketName, List<String> objectNames) {
        try {
            log.info("MinIO批量删除文件: bucket={}, count={}", bucketName, objectNames.size());
            
            List<DeleteObject> objects = objectNames.stream()
                    .map(DeleteObject::new)
                    .collect(Collectors.toList());
            
            minioClient.removeObjects(
                    RemoveObjectsArgs.builder()
                            .bucket(bucketName)
                            .objects(objects)
                            .build()
            );
        } catch (Exception e) {
            log.error("MinIO批量文件删除失败", e);
            throw new RuntimeException("MinIO批量文件删除失败: " + e.getMessage(), e);
        }
    }
    
    @Override
    public boolean fileExists(String bucketName, String objectName) {
        try {
            minioClient.statObject(
                    StatObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .build()
            );
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    @Override
    public String getFileUrl(String bucketName, String objectName) {
        return String.format("%s/%s/%s", minioConfig.getEndpoint(), bucketName, objectName);
    }
    
    @Override
    public String getPresignedUrl(String bucketName, String objectName, int expiryMinutes) {
        try {
            log.info("MinIO获取预签名URL: bucket={}, object={}, expiry={}min", 
                    bucketName, objectName, expiryMinutes);
            
            return minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .method(io.minio.http.Method.GET)
                            .bucket(bucketName)
                            .object(objectName)
                            .expiry(expiryMinutes, TimeUnit.MINUTES)
                            .build()
            );
        } catch (Exception e) {
            log.error("MinIO获取预签名URL失败", e);
            throw new RuntimeException("MinIO获取预签名URL失败: " + e.getMessage(), e);
        }
    }
    
    @Override
    public String composeFile(String bucketName, String targetObjectName,
                              List<String> sourceObjectNames, String contentType) {
        try {
            log.info("MinIO合并分片文件: bucket={}, target={}, sources={}", 
                    bucketName, targetObjectName, sourceObjectNames.size());
            
            // 构建分片源列表
            List<ComposeSource> sources = sourceObjectNames.stream()
                    .map(objectName -> ComposeSource.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .build())
                    .collect(Collectors.toList());
            
            // 合并分片
            minioClient.composeObject(
                    ComposeObjectArgs.builder()
                            .bucket(bucketName)
                            .object(targetObjectName)
                            .sources(sources)
                            .build()
            );
            
            return getFileUrl(bucketName, targetObjectName);
            
        } catch (Exception e) {
            log.error("MinIO分片合并失败", e);
            throw new RuntimeException("MinIO分片合并失败: " + e.getMessage(), e);
        }
    }
    
    @Override
    public InputStream downloadFile(String bucketName, String objectName) {
        try {
            log.info("MinIO下载文件: bucket={}, object={}", bucketName, objectName);
            return minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .build()
            );
        } catch (Exception e) {
            log.error("MinIO文件下载失败", e);
            throw new RuntimeException("MinIO文件下载失败: " + e.getMessage(), e);
        }
    }
    
    @Override
    public String getStorageType() {
        return "minio";
    }
    
    @Override
    public void ensureBucketExists(String bucketName) {
        try {
            boolean found = minioClient.bucketExists(
                    BucketExistsArgs.builder()
                            .bucket(bucketName)
                            .build()
            );
            
            if (!found) {
                log.info("MinIO存储桶不存在，创建存储桶: {}", bucketName);
                minioClient.makeBucket(
                        MakeBucketArgs.builder()
                                .bucket(bucketName)
                                .build()
                );
            }
        } catch (Exception e) {
            log.error("MinIO存储桶检查/创建失败", e);
            throw new RuntimeException("MinIO存储桶检查/创建失败: " + e.getMessage(), e);
        }
    }
}
