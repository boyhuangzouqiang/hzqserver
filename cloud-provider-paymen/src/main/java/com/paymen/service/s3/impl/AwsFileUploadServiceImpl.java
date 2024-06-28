package com.paymen.service.s3.impl;

import com.amazonaws.HttpMethod;
import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.Upload;
import com.paymen.config.AsyncConfig;
import com.paymen.service.s3.AwsFileUploadService;
import com.paymen.utils.FileUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * @description:
 * @author: huangzouqiang
 * @create: 2024-06-27 15:43
 * @Version 1.0
 **/
@Slf4j
@Service
public class AwsFileUploadServiceImpl implements AwsFileUploadService {

    /**
     * 设置分片大小
     */
    private long partSize = 5 * 1024 * 1024; // 5MB;

    private String down_path = "D:\\testminio";

    @Value("${minio.bucket-name}")
    private String bucketName;

    @Autowired
    private AsyncConfig asyncConfig;

    @Autowired
    private AmazonS3 amazonS3Client;

    @Autowired
    private TransferManager transferManager;

    /**
     * AWS SDK 公开了一个名为 TransferManager 的高级别 API，用于简化分段上传。有关更多信息，请参阅 使用分段上传来上传和复制对象。
     * <p>
     * 您可以从文件或流上传数据。您还可以设置高级选项，例如，您想要用于分段上传的分段大小或在上传分段时要使用的并发线程数。
     * 您也可以设置可选的对象属性、存储类或访问控制列表 (ACL)。您可以使用 PutObjectRequest 和 TransferManagerConfiguration 类来设置这些高级选项。
     *
     * @param keyName keyName
     * @param file    file
     * @return CompleteMultipartUploadResult
     */
    @Override
    public String highLevelMultipartUpload(String keyName, File file) throws InterruptedException {
        String upPrefix = UUID.randomUUID().toString();
        String key = upPrefix + "/" + keyName;
        // 使用 TransferManager 上传
        Upload upload = transferManager.upload(bucketName, key, file);
        upload.waitForCompletion();
        log.info("Object upload complete");
        return key;
    }


    /**
     * EDS提供的分片上传(Multipart Upload)功能，将要上传的较大文件(object)分成多个分片(Part)来分别上传
     * 上传完成后再调用completeMultipartUpload接口将这些Part组合成一个object来达到断点续传的效果。
     *
     * @param keyName    keyName
     * @param bucketName yourBucketName
     * @param file       file
     * @return CompleteMultipartUploadResult
     */
    @Override
    public CompleteMultipartUploadResult bigFileListShardingUpload(String keyName, String bucketName, File file) {
        List<PartETag> partETags = new ArrayList<>();
        long filePosition = 0;
        CompleteMultipartUploadResult result = null;
        String uploadId = null;
        InitiateMultipartUploadRequest initRequest = new InitiateMultipartUploadRequest(bucketName, keyName);
        InitiateMultipartUploadResult initResponse = amazonS3Client.initiateMultipartUpload(initRequest);
        uploadId = initResponse.getUploadId();
        try {
            long contentLength = file.length();
            long partSize = 25 * 1024 * 1024; // Set part size to 25 MB.
            log.info("-------开始只能进入分片上传阶段-------");
            for (int i = 1; filePosition < contentLength; i++) {
                partSize = Math.min(partSize, (contentLength - filePosition));
                UploadPartRequest uploadRequest = new UploadPartRequest()
                        .withBucketName(bucketName)
                        .withKey(keyName)
                        .withUploadId(uploadId)
                        .withPartNumber(i)
                        .withFileOffset(filePosition)
                        .withFile(file)
                        .withPartSize(partSize);
                UploadPartResult uploadResult = amazonS3Client.uploadPart(uploadRequest);
                // 返回文件的ETag值，用于验证文件是否被正确上传
                partETags.add(uploadResult.getPartETag());
                filePosition += partSize;
                log.info("文件分片上传--->" + filePosition);
            }
            log.info("-------所有分片上传完整------->进入分片合并阶段-------");
            // 完成分片上传
            CompleteMultipartUploadRequest compRequest = new CompleteMultipartUploadRequest(bucketName, keyName,
                    initResponse.getUploadId(), partETags);
            result = amazonS3Client.completeMultipartUpload(compRequest);
        } catch (SdkClientException e) {
            log.error("分片上传错误，第{}片发生意外", filePosition, e);
            throw new RuntimeException(e);
        }
        log.info("-------大文件分片上传完成--->" + filePosition);
        return result;
    }

    @Override
    public CompleteMultipartUploadResult bigFileListShardingUpload(String keyName, File file) {
        return bigFileListShardingUpload(keyName, bucketName, file);
    }

    /**
     * 分片上传
     *
     * @param bucketName        桶名称
     * @param fileInputStream   文件流
     * @param fileContentLength 原始文件的length
     * @param keyName           唯一文件标识
     * @return
     */
    @Override
    public CompleteMultipartUploadResult uploadLargeFileToS3(String bucketName, FileInputStream fileInputStream, long fileContentLength, String keyName) {
        InitiateMultipartUploadResult initResponse = null;
        InitiateMultipartUploadRequest initRequest = null;
        CompleteMultipartUploadResult completeMultipartUploadResult = null;
        try {
            // 初始化多部分上传
            initRequest = new InitiateMultipartUploadRequest(bucketName, keyName);
            initResponse = amazonS3Client.initiateMultipartUpload(initRequest);
            // 每个分片大小（例如：5MB）
            long partSize = 5 * 1024 * 1024;

            List<PartETag> partETags = new ArrayList<>();
            long bytePosition = 0;

            for (int i = 1; bytePosition < fileContentLength; i++) {
                long bytesRemaining = fileContentLength - bytePosition;
                partSize = Math.min(bytesRemaining, partSize);

                UploadPartRequest uploadRequest = new UploadPartRequest()
                        .withBucketName(bucketName)
                        .withKey(keyName)
                        .withUploadId(initResponse.getUploadId())
                        .withPartNumber(i)
                        .withFileOffset(bytePosition)
                        .withInputStream(fileInputStream)
                        .withPartSize(partSize);

                UploadPartResult uploadResult = amazonS3Client.uploadPart(uploadRequest);
                partETags.add(uploadResult.getPartETag());
                bytePosition += partSize;
            }
            // 完成多部分上传
            CompleteMultipartUploadRequest compRequest = new CompleteMultipartUploadRequest(
                    bucketName,
                    keyName,
                    initResponse.getUploadId(),
                    partETags);
            completeMultipartUploadResult = amazonS3Client.completeMultipartUpload(compRequest);
        } catch (SdkClientException e) {
            // 处理异常，可能需要回滚已上传的部分
            log.error("Error uploading file to S3", e);
            // 如果有错误，尝试取消上传
            AbortMultipartUploadRequest abortMPURequest = new AbortMultipartUploadRequest(
                    bucketName,
                    keyName,
                    initResponse.getUploadId());
            amazonS3Client.abortMultipartUpload(abortMPURequest);
        } finally {
            try {
                fileInputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return completeMultipartUploadResult;
    }

    @Override
    public CompleteMultipartUploadResult uploadLargeFileToS3(FileInputStream fileInputStream, long fileLength, String key) {
        return uploadLargeFileToS3(bucketName, fileInputStream, fileLength, key);
    }

    /**
     * 简单下载文件流
     *
     * @param key key
     * @return InputStream
     */
    @Override
    public InputStream downloadToEDS(String key, String bucketName) {
        log.info("Downloading {} from S3 bucket {}...\n", key, bucketName);
        S3Object object = amazonS3Client.getObject(new GetObjectRequest(bucketName, key));
        return object.getObjectContent();
    }

    @Override
    public InputStream downloadToEDS(String key) {
        return downloadToEDS(key, bucketName);
    }


    /**
     * 流式上传文件到EDS
     *
     * @param keyName     fileName
     * @param inputStream InputStream
     * @return PutObjectResult
     */
    @Override
    public PutObjectResult streamUploadToEDS(String keyName, InputStream inputStream, String bucketName) {
        try {
            //创建上传Object的Metadata
            ObjectMetadata metadata = new ObjectMetadata();
            //上传的文件的长度
            metadata.setContentLength(inputStream.available());
            //指定该Object被下载时的网页的缓存行为
            metadata.setCacheControl("no-cache");
            //指定该Object下设置Header
            metadata.setHeader("Pragma", "no-cache");
            //指定该Object被下载时的内容编码格式
            metadata.setContentEncoding("utf-8");
            //文件的MIME，定义文件的类型及网页编码，决定浏览器将以什么形式、什么编码读取文件。如果用户没有指定则根据Key或文件名的扩展名生成，
            //如果没有扩展名则填默认值application/octet-stream
            metadata.setContentType("application/octet-stream");
            return amazonS3Client.putObject(bucketName, keyName, inputStream, metadata);
        } catch (IOException e) {
            log.error("文件上传失败。。。。" + e.getMessage());
            throw new RuntimeException(e);
        } catch (SdkClientException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public PutObjectResult streamUploadToEDS(String keyName, InputStream inputStream) {
        return streamUploadToEDS(keyName, inputStream, bucketName);
    }


    /**
     * 简单上传文件到EDS
     *
     * @param keyName fileName
     * @param file    file
     * @return PutObjectResult
     */
    @Override
    public PutObjectResult simpleUploadToEDS(String keyName, File file, String bucketName) {
        return amazonS3Client.putObject(bucketName, keyName, file);
    }

    @Override
    public PutObjectResult simpleUploadToEDS(String keyName, File file) {
        return amazonS3Client.putObject(bucketName, keyName, file);
    }

    /**
     * 创建文件上传的预签名--URL
     *
     * @param bucketName 桶名称
     * @param keyName    唯一文件标识
     * @return URL
     */
    @Override
    public URL createSignedUrlForStringPut(String bucketName, String keyName) {
        Date expiration = new Date();
        long expirationNumber = expiration.getTime() + 3600 * 1000;
        expiration.setTime(expirationNumber);
        GeneratePresignedUrlRequest generatePresignedUrlRequest = new GeneratePresignedUrlRequest(bucketName, keyName, HttpMethod.PUT);
        generatePresignedUrlRequest.setExpiration(expiration);
        return amazonS3Client.generatePresignedUrl(generatePresignedUrlRequest);
    }

    /**
     * 创建文件下载的预签名--URL
     *
     * @param bucketName 桶名称
     * @param keyName    唯一文件标识
     * @return URL
     */
    @Override
    public URL createSignedUrlForStringGet(String bucketName, String keyName) {
        Date expiration = new Date();
        long expirationNumber = expiration.getTime() + 3600 * 1000;
        expiration.setTime(expirationNumber);
        GeneratePresignedUrlRequest generatePresignedUrlRequest = new GeneratePresignedUrlRequest(bucketName, keyName, HttpMethod.GET);
        generatePresignedUrlRequest.setExpiration(expiration);
        return amazonS3Client.generatePresignedUrl(generatePresignedUrlRequest);
    }

    /**
     * 大文件分片下载
     *
     * @param objectKey
     */
    @Override
    public void downloadBigFile(String objectKey) throws IOException {
        String fileName = "";
        if (objectKey.lastIndexOf("/") > 0) {
            fileName = objectKey.substring(objectKey.lastIndexOf("/") + 1);
        } else {
            fileName = objectKey;
        }

        GetObjectMetadataRequest getObjectRequest = new GetObjectMetadataRequest(bucketName, objectKey);
        // 获取对象的元数据
        ObjectMetadata objectMetadata = amazonS3Client.getObjectMetadata(getObjectRequest);
        // 获取对象的大小
        long objectSize = objectMetadata.getContentLength();
        // 判断是否需要分片下载
        if (objectSize <= partSize) {
            //正常下载
            S3ObjectInputStream objectContent = amazonS3Client.getObject(bucketName, objectKey).getObjectContent();
            //临时存储分片文件
            FileOutputStream fos = new FileOutputStream(down_path + File.separator + fileName);
            // 定义缓冲区
            byte[] buffer = new byte[1024];
            int readLength;
            //写文件
            while ((readLength = objectContent.read(buffer)) != -1) {
                fos.write(buffer, 0, readLength);
            }
            objectContent.close();
            fos.flush();
            fos.close();
        } else {
            long pages = objectSize / partSize;
            System.out.println("文件分页个数:" + pages + "， 文件大小：" + objectSize);
            TaskExecutor asyncExecutor = asyncConfig.getAsyncExecutor();
            //分片下载
            for (int i = 0; i <= pages; i++) {
                int finalI = i;
                String finalFileName = fileName;
                asyncExecutor.execute(() -> {
                    try {
                        download(finalI * partSize, (finalI + 1) * partSize - 1, finalI, objectKey, finalFileName, objectSize);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            }
        }
    }

    /**
     * 保存单个分片数据落盘
     *
     * @param start
     * @param end
     * @param page
     * @param objectKey 存储的key
     * @param fileName  存储文件的名称
     * @return
     * @throws Exception
     */
    private String download(long start, long end, long page, String objectKey, String fileName, long fSize) throws Exception {
        // 断点下载 文件存在不需要下载
        File file = new File(down_path, page + "-" + fileName);
        // 探测必须放行 若下载分片只下载一半就需要重新下载 所以需要判断文件是否完整
        if (file.exists() && page != -1 && file.length() == partSize) {
            System.out.println("文件存在了咯，不处理了");
            return null;
        }
        GetObjectRequest request = new GetObjectRequest(bucketName, objectKey);
        request.setRange(start, end);
        S3ObjectInputStream objectContent = amazonS3Client.getObject(request).getObjectContent();
        //临时存储分片文件
        FileOutputStream fos = new FileOutputStream(file);
        // 定义缓冲区
        byte[] buffer = new byte[1024];
        int readLength;
        //写文件
        while ((readLength = objectContent.read(buffer)) != -1) {
            fos.write(buffer, 0, readLength);
        }
        objectContent.close();
        fos.flush();
        fos.close();
        //判断是不是最后一个分片，如果不是最后一个分片不执行
        if (end - fSize > 0) {
            try {
                System.out.println("开始合并了");
                this.mergeAllPartFile(fileName, page);
                System.out.println("文件合并结束了");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return "ok";
    }

    /**
     * 合并文件
     *
     * @param fName 文件名称
     * @param page  分片的文件的页
     */
    private void mergeAllPartFile(String fName, long page) throws Exception {
        // 归并文件位置
        File file = new File(down_path, fName);
        BufferedOutputStream os = new BufferedOutputStream(new FileOutputStream(file));
        for (int i = 0; i <= page; i++) {
            File tempFile = new File(down_path, i + "-" + fName);
            // 分片没下载或者没下载完需要等待
            while (!file.exists() || (i != page && tempFile.length() < partSize)) {
                Thread.sleep(1000);
                System.out.println("异步线程下载分片数据等待结束，再次查询文件等候已经下载完成了");
            }
            byte[] bytes = FileUtils.readFileToByteArray(tempFile);
            os.write(bytes);
            os.flush();
            tempFile.delete();
        }
        os.flush();
        os.close();
    }


}
