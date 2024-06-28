package com.paymen.service.s3;

import com.amazonaws.services.s3.model.CompleteMultipartUploadResult;
import com.amazonaws.services.s3.model.PutObjectResult;

import java.io.*;
import java.net.URL;

/**
 * @description:
 * @author: huangzouqiang
 * @create: 2024-06-27 15:43
 * @Version 1.0
 **/
public interface AwsFileUploadService {

    /**
     * 高级API大文件分片上传
     *
     * @param keyName 唯一文件标识
     * @param file    file
     * @return keyName 唯一文件标识
     * @throws InterruptedException
     */
    String highLevelMultipartUpload(String keyName, File file) throws InterruptedException;


    /**
     * 文件分片上传
     *
     * @param keyName    唯一文件标识
     * @param bucketName 桶名称
     * @param file       file
     * @return CompleteMultipartUploadResult
     */
    CompleteMultipartUploadResult bigFileListShardingUpload(String keyName, String bucketName, File file);


    /**
     * 文件分片上传
     *
     * @param keyName 唯一文件标识
     * @param file    file
     * @return CompleteMultipartUploadResult
     */
    CompleteMultipartUploadResult bigFileListShardingUpload(String keyName, File file);


    /**
     * 文件分片上传 指定桶
     *
     * @param bucketName        桶名称
     * @param fileInputStream   文件流
     * @param fileContentLength 原始文件的length
     * @param keyName           唯一文件标识
     * @return CompleteMultipartUploadResult
     */
    CompleteMultipartUploadResult uploadLargeFileToS3(String bucketName, FileInputStream fileInputStream, long fileContentLength, String keyName);


    /**
     * 文件分片上传 默认桶
     *
     * @param fileInputStream 文件流
     * @param fileLength      原始文件的length
     * @param key             唯一文件标识
     * @return CompleteMultipartUploadResult
     */
    CompleteMultipartUploadResult uploadLargeFileToS3(FileInputStream fileInputStream, long fileLength, String key);

    /**
     * 文件下载
     *
     * @param key        唯一文件标识
     * @param bucketName 桶名称
     * @return InputStream
     */
    InputStream downloadToEDS(String key, String bucketName);


    /**
     * 文件下载  默认桶
     *
     * @param key 唯一文件标识
     * @return InputStream
     */
    InputStream downloadToEDS(String key);


    /**
     * 简单文件上传 --流 指定桶
     *
     * @param keyName     唯一文件标识
     * @param inputStream 文件流
     * @param bucketName  桶名称
     * @return PutObjectResult
     */
    PutObjectResult streamUploadToEDS(String keyName, InputStream inputStream, String bucketName);


    /**
     * 简单文件上传 --流 默认桶
     *
     * @param keyName     唯一文件标识
     * @param inputStream 文件流
     * @return PutObjectResult
     */
    PutObjectResult streamUploadToEDS(String keyName, InputStream inputStream);


    /**
     * 简单文件上传 --file 指定桶
     *
     * @param keyName    唯一文件标识
     * @param file       文件
     * @param bucketName 桶名称
     * @return PutObjectResult
     */
    PutObjectResult simpleUploadToEDS(String keyName, File file, String bucketName);


    /**
     * 简单文件上传 --file 默认桶
     *
     * @param keyName 唯一文件标识
     * @param file    文件
     * @return PutObjectResult
     */
    PutObjectResult simpleUploadToEDS(String keyName, File file);

    /**
     * 创建文件上传的预签名--URL
     *
     * @param bucketName 桶名称
     * @param keyName    唯一文件标识
     * @return URL
     */
    URL createSignedUrlForStringPut(String bucketName, String keyName);

    /**
     * 创建文件下载的预签名--URL
     *
     * @param bucketName 桶名称
     * @param keyName    唯一文件标识
     * @return URL
     */
    URL createSignedUrlForStringGet(String bucketName, String keyName);

    /**
     * 大文件分片下载
     *
     * @param key
     */
    void downloadBigFile(String key) throws IOException;
}


