package com.hzqserver.file.service.storage.impl;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.*;
import com.hzqserver.file.config.OssConfig;
import com.hzqserver.file.service.storage.StorageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * 阿里云OSS存储服务实现
 * 基于阿里云OSS SDK实现S3兼容的对象存储操作
 */
@Slf4j
@Service
public class OssStorageServiceImpl implements StorageService {
    
    @Autowired
    private OssConfig ossConfig;
    
    /**
     * 创建OSS客户端
     */
    private OSS createOssClient() {
        return new OSSClientBuilder().build(
                ossConfig.getEndpoint(),
                ossConfig.getAccessKeyId(),
                ossConfig.getAccessKeySecret()
        );
    }
    
    @Override
    public String uploadFile(String bucketName, String objectName, InputStream inputStream,
                             long size, String contentType) {
        OSS ossClient = createOssClient();
        try {
            log.info("OSS上传文件: bucket={}, object={}, size={}", bucketName, objectName, size);
            
            // 确保存储桶存在
            ensureBucketExists(bucketName);
            
            // 创建上传请求
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(size);
            if (contentType != null) {
                metadata.setContentType(contentType);
            }
            
            // 上传文件
            PutObjectRequest putObjectRequest = new PutObjectRequest(
                    bucketName, objectName, inputStream, metadata
            );
            ossClient.putObject(putObjectRequest);
            
            // 返回文件访问URL
            return getFileUrl(bucketName, objectName);
            
        } catch (Exception e) {
            log.error("OSS文件上传失败", e);
            throw new RuntimeException("OSS文件上传失败: " + e.getMessage(), e);
        } finally {
            ossClient.shutdown();
        }
    }
    
    @Override
    public void deleteFile(String bucketName, String objectName) {
        OSS ossClient = createOssClient();
        try {
            log.info("OSS删除文件: bucket={}, object={}", bucketName, objectName);
            ossClient.deleteObject(bucketName, objectName);
        } catch (Exception e) {
            log.error("OSS文件删除失败", e);
            throw new RuntimeException("OSS文件删除失败: " + e.getMessage(), e);
        } finally {
            ossClient.shutdown();
        }
    }
    
    @Override
    public void deleteFiles(String bucketName, List<String> objectNames) {
        OSS ossClient = createOssClient();
        try {
            log.info("OSS批量删除文件: bucket={}, count={}", bucketName, objectNames.size());
            
            DeleteObjectsRequest deleteRequest = new DeleteObjectsRequest(bucketName);
            deleteRequest.setKeys(objectNames);
            deleteRequest.setQuiet(true);
            
            ossClient.deleteObjects(deleteRequest);
        } catch (Exception e) {
            log.error("OSS批量文件删除失败", e);
            throw new RuntimeException("OSS批量文件删除失败: " + e.getMessage(), e);
        } finally {
            ossClient.shutdown();
        }
    }
    
    @Override
    public boolean fileExists(String bucketName, String objectName) {
        OSS ossClient = createOssClient();
        try {
            return ossClient.doesObjectExist(bucketName, objectName);
        } catch (Exception e) {
            return false;
        } finally {
            ossClient.shutdown();
        }
    }
    
    @Override
    public String getFileUrl(String bucketName, String objectName) {
        // 如果配置了自定义域名，使用自定义域名
        if (ossConfig.getCustomDomain() != null && !ossConfig.getCustomDomain().isEmpty()) {
            return String.format("%s/%s", ossConfig.getCustomDomain(), objectName);
        }
        // 否则使用默认OSS域名
        return String.format("https://%s.%s/%s", bucketName, ossConfig.getEndpoint(), objectName);
    }
    
    @Override
    public String getPresignedUrl(String bucketName, String objectName, int expiryMinutes) {
        OSS ossClient = createOssClient();
        try {
            log.info("OSS获取预签名URL: bucket={}, object={}, expiry={}min", 
                    bucketName, objectName, expiryMinutes);
            
            // 计算过期时间（毫秒）
            long expiryMillis = System.currentTimeMillis() + (expiryMinutes * 60 * 1000L);
            
            // 生成预签名URL
            java.util.Date expiration = new java.util.Date(expiryMillis);
            GeneratePresignedUrlRequest request = new GeneratePresignedUrlRequest(
                    bucketName, objectName, com.aliyun.oss.HttpMethod.GET
            );
            request.setExpiration(expiration);
            
            java.net.URL url = ossClient.generatePresignedUrl(request);
            return url.toString();
            
        } catch (Exception e) {
            log.error("OSS获取预签名URL失败", e);
            throw new RuntimeException("OSS获取预签名URL失败: " + e.getMessage(), e);
        } finally {
            ossClient.shutdown();
        }
    }
    
    @Override
    public String composeFile(String bucketName, String targetObjectName,
                              List<String> sourceObjectNames, String contentType) {
        OSS ossClient = createOssClient();
        try {
            log.info("OSS合并分片文件: bucket={}, target={}, sources={}", 
                    bucketName, targetObjectName, sourceObjectNames.size());
            
            // 初始化分片上传
            InitiateMultipartUploadRequest initRequest = new InitiateMultipartUploadRequest(
                    bucketName, targetObjectName
            );
            if (contentType != null) {
                ObjectMetadata metadata = new ObjectMetadata();
                metadata.setContentType(contentType);
                initRequest.setObjectMetadata(metadata);
            }
            InitiateMultipartUploadResult initResult = ossClient.initiateMultipartUpload(initRequest);
            String uploadId = initResult.getUploadId();
            
            // 上传各个分片
            List<PartETag> partETags = new ArrayList<>();
            for (int i = 0; i < sourceObjectNames.size(); i++) {
                String sourceObject = sourceObjectNames.get(i);
                
                // 获取源对象
                OSSObject sourceObjectResult = ossClient.getObject(bucketName, sourceObject);
                InputStream sourceStream = sourceObjectResult.getObjectContent();
                
                // 上传分片
                UploadPartRequest uploadPartRequest = new UploadPartRequest();
                uploadPartRequest.setBucketName(bucketName);
                uploadPartRequest.setKey(targetObjectName);
                uploadPartRequest.setUploadId(uploadId);
                uploadPartRequest.setInputStream(sourceStream);
                uploadPartRequest.setPartSize(sourceObjectResult.getObjectMetadata().getContentLength());
                uploadPartRequest.setPartNumber(i + 1);
                
                UploadPartResult uploadPartResult = ossClient.uploadPart(uploadPartRequest);
                partETags.add(uploadPartResult.getPartETag());
                
                sourceStream.close();
            }
            
            // 完成分片上传
            CompleteMultipartUploadRequest completeRequest = new CompleteMultipartUploadRequest(
                    bucketName, targetObjectName, uploadId, partETags
            );
            ossClient.completeMultipartUpload(completeRequest);
            
            return getFileUrl(bucketName, targetObjectName);
            
        } catch (Exception e) {
            log.error("OSS分片合并失败", e);
            throw new RuntimeException("OSS分片合并失败: " + e.getMessage(), e);
        } finally {
            ossClient.shutdown();
        }
    }
    
    @Override
    public InputStream downloadFile(String bucketName, String objectName) {
        OSS ossClient = createOssClient();
        try {
            log.info("OSS下载文件: bucket={}, object={}", bucketName, objectName);
            OSSObject ossObject = ossClient.getObject(bucketName, objectName);
            return ossObject.getObjectContent();
        } catch (Exception e) {
            log.error("OSS文件下载失败", e);
            throw new RuntimeException("OSS文件下载失败: " + e.getMessage(), e);
        } finally {
            // 注意：这里不能关闭ossClient，因为返回的InputStream依赖于它
            // 调用方需要负责关闭InputStream和ossClient
        }
    }
    
    @Override
    public String getStorageType() {
        return "oss";
    }
    
    @Override
    public void ensureBucketExists(String bucketName) {
        OSS ossClient = createOssClient();
        try {
            boolean exists = ossClient.doesBucketExist(bucketName);
            
            if (!exists) {
                log.info("OSS存储桶不存在，创建存储桶: {}", bucketName);
                ossClient.createBucket(bucketName);
            }
        } catch (Exception e) {
            log.error("OSS存储桶检查/创建失败", e);
            throw new RuntimeException("OSS存储桶检查/创建失败: " + e.getMessage(), e);
        } finally {
            ossClient.shutdown();
        }
    }
}
