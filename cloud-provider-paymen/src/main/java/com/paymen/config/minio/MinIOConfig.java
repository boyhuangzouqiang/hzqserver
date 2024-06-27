//package com.paymen.config.minio;
//
//import io.minio.MinioClient;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
///**
// * @description:
// * @author: huangzouqiang
// * @create: 2024-06-26 20:18
// * @Version 1.0
// **/
//@Configuration
//public class MinIOConfig {
//
//    @Value("${minio.url}")
//    private String url;
//
//    @Value("${minio.access-key}")
//    private String accessKey;
//
//    @Value("${minio.secret-key}")
//    private String secretKey;
//
//    @Bean
//    public MinioClient minioClient() {
//        return MinioClient.builder()
//                .endpoint(url)
//                .credentials(accessKey, secretKey)
//                .build();
//    }
//}
