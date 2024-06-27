//package com.paymen.utils.minio;
//
///**
// * @description: minio上传的文件加解密工具类
// * 这个客户端minio版本7.0.2版本，本项目用aws-s3依赖，这里都不用了
// * @author: huangzouqiang
// * @create: 2024-06-27 14:55
// * @Version 1.0
// **/
//
//import cn.hutool.crypto.symmetric.SymmetricAlgorithm;
//import cn.hutool.crypto.symmetric.SymmetricCrypto;
//import io.minio.MinioClient;
//import io.minio.PutObjectOptions;
//import lombok.SneakyThrows;
//
//import javax.crypto.SecretKey;
//import javax.crypto.spec.SecretKeySpec;
//import java.io.ByteArrayInputStream;
//import java.io.InputStream;
//import java.nio.charset.StandardCharsets;
//import java.util.Base64;
//
//public class MinioEncryptionUtil {
//    static final String originKeyStr = "0123456789Abc@@@"; // 必须16个字符
//    private static SymmetricCrypto aes;
//
//
//    // 加密并编码字符串
//    public static String encryptURLEncodeStr(String str) {
//        try {
//            if (aes == null) {
//                SecretKey aesKey = new SecretKeySpec(originKeyStr.getBytes(StandardCharsets.UTF_8), "AES");
//                byte[] key = aesKey.getEncoded();
////构建
//                aes = new SymmetricCrypto(SymmetricAlgorithm.AES, key);
//            }
//
////加密
//            byte[] encrypt = aes.encrypt(str.getBytes(StandardCharsets.UTF_8));
//            String s = Base64.getUrlEncoder().encodeToString(encrypt);
//            return s;
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return "";
//    }
//
//    // 解码并解密字符串
//    public static String decryptURLDecodeStr(String encStr) {
//        try {
//            if (aes == null) {
//                SecretKey aesKey = new SecretKeySpec(originKeyStr.getBytes(StandardCharsets.UTF_8), "AES");
//                byte[] key = aesKey.getEncoded();
//                //构建
//                aes = new SymmetricCrypto(SymmetricAlgorithm.AES, key);
//            }
//
//            //加密
//            byte[] bytes = Base64.getUrlDecoder().decode(encStr);
//            String s = aes.decryptStr(bytes);
//            return s;
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return "";
//    }
//
//
//    // 加密并上传文件
//    @SneakyThrows
//    public static void encryptUpload(MinioClient minioClient, String bucketName, String objectName, InputStream inputStream) {
//        if (aes == null) {
//            SecretKey aesKey = new SecretKeySpec(originKeyStr.getBytes(StandardCharsets.UTF_8), "AES");
//            byte[] key = aesKey.getEncoded();
////构建
//            aes = new SymmetricCrypto(SymmetricAlgorithm.AES, key);
//        }
//
//        byte[] bytes = new byte[inputStream.available()];
//        inputStream.read(bytes, 0, bytes.length);
////加密
//        byte[] encrypt = aes.encrypt(bytes);
//        ByteArrayInputStream bais = new ByteArrayInputStream(encrypt);
//
//        PutObjectOptions putObjectOptions = new PutObjectOptions(bais.available(), bais.available() < 5 * 1024 * 1024 ? 5 * 1024 * 1024 : bais.available() * 8);
//        minioClient.putObject(bucketName, objectName, bais, putObjectOptions);
//    }
//
//
//    // 下载文件并解密
//    @SneakyThrows
//    public static byte[] decryptDownload(MinioClient minioClient, String bucketName, String objectName) {
//        if (aes == null) {
//            SecretKey aesKey = new SecretKeySpec(originKeyStr.getBytes(StandardCharsets.UTF_8), "AES");
//            byte[] key = aesKey.getEncoded();
//            aes = new SymmetricCrypto(SymmetricAlgorithm.AES, key);
//        }
//
//        InputStream inputStream = minioClient.getObject(bucketName, objectName);
//        byte[] decrypt = aes.decrypt(inputStream);
//        return decrypt;
//    }
//
//}
