package com.paymen.config.s3;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.Protocol;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.TransferManagerBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

/**
 * @description: aws-s3客户端配置类
 * @author: huangzouqiang
 * @create: 2024-06-27 15:32
 * @Version 1.0
 **/
@Configuration
@Component
public class AwsConfig {
    @Value("${minio.access-key}")
    private String accessKey;

    @Value("${minio.secret-key}")
    private String secretKey;

    /**
     * 对象存储的地址:对象存储服务的端口号：示例http://10.212.27.56:12001
     */
    @Value("${minio.url}")
    private String endPoint;

    /**
     * EDS对象存储的bucket名称:示例eds-cloud-oss
     */
    @Value("${minio.bucket-name}")
    private String bucketName;

    /**
     * EDS对象存储的region:示例cn-north-1
     */
    @Value("${eds.cloud.oss.region:}")
    private String region;

    @Bean("amazonS3Client")
    public AmazonS3 intiAmazonS3Client() {
        AWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);
        ClientConfiguration clientConfig = new ClientConfiguration();
        clientConfig.setProtocol(Protocol.HTTP);
        clientConfig.setConnectionTimeout(300000);
        clientConfig.setClientExecutionTimeout(300000);
        clientConfig.setConnectionMaxIdleMillis(300000);
        // 初始化AmazonS3Client
        return AmazonS3ClientBuilder.standard().
                withClientConfiguration(clientConfig).
                withCredentials(new AWSStaticCredentialsProvider(credentials)).
                withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(endPoint, region)).
                build();
    }

    @Bean("transferManager")
    public TransferManager transferManager() {
        return TransferManagerBuilder.standard().withS3Client(intiAmazonS3Client()).build();
    }

}
