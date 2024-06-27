package com.paymen.controller.s3;

import cn.hutool.core.date.StopWatch;
import com.amazonaws.services.s3.model.CompleteMultipartUploadResult;
import com.paymen.service.s3.AwsFileUploadService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.UUID;

/**
 * @description: aws-s3方式操作minio
 * @author: huangzouqiang
 * @create: 2024-06-27 15:56
 * @Version 1.0
 **/
@Slf4j
@RestController
@RequestMapping("aws")
public class AwsController {

    @Autowired
    AwsFileUploadService awsFileUploadService;

    /**
     * 单个文件上传
     */
    @GetMapping("uploadFile")
    public void contextLoads(@RequestParam("filePah") String filePath) {
        try {
            String x = "C:\\Users\\hzq\\Desktop\\大型车辆\\02c4200d08123776e2ca9673bfb8f74.jpg";
            File fileLocal = new File(filePath);
            String originalFilename = fileLocal.getName();
            String key = UUID.randomUUID() + "/" + originalFilename;
            log.info("上传生成的=" + key);
            StopWatch stopWatch = new StopWatch();
            stopWatch.start();
            CompleteMultipartUploadResult result = awsFileUploadService.bigFileListShardingUpload(key, fileLocal);
            stopWatch.stop();
            log.info("上传执行时间（ms）：" + stopWatch.getTotalTimeMillis());
            // 处理上传结果
            String eTag = result.getETag();
            // ...
            log.info("上传成功：" + eTag);
        } catch (Exception e) {
            log.error("上传失败", e);
        }
    }

    /**
     * 高级API大文件上传EDS云对象存储测试
     * todo 这个文件就在后端服务器上面，内部api分片传到minio，如果是页面上，就别用这个方法，
     * todo 页面的文件需要全部存储到后台才能上传，也有可能前端全部给到后台过程中直接后端内存溢出了，
     * todo 典型的从页面上下载minio大文件直接会内存溢出
     */
    @GetMapping("uploadBigFile")
    public void contextLoadsBigFile(@RequestParam("filePah") String filePath) {
        try {
            File fileLocal = new File(filePath);
            String originalFilename = fileLocal.getName();
            String key = UUID.randomUUID() + "/" + originalFilename;
            log.info("上传生成的=" + key);
            StopWatch stopWatch = new StopWatch();
            stopWatch.start();
            String eTag = awsFileUploadService.highLevelMultipartUpload(key, fileLocal);
            stopWatch.stop();
            log.info("上传执行时间（ms）：" + stopWatch.getTotalTimeMillis());
            // ...
            log.info("上传成功：" + eTag);
        } catch (Exception e) {
            log.error("上传失败", e);
        }
    }

    /**
     * 分片API大文件上传EDS云对象存储测试
     * todo 这个文件就在后端服务器上面，分片传到minio，如果是页面上，就通过页面分片上传到minio上
     */
    @GetMapping("uploadPartBigFile")
    public void contextLoadsBigFileShare() {
        try {
            File fileLocal = new File("D:\\fileTemp\\深信服OSS\\aws-java-sdk-1.12.643.zip");
            String originalFilename = fileLocal.getName();
            String key = UUID.randomUUID() + "/" + originalFilename;
            log.info("上传生成的=" + key);
            StopWatch stopWatch = new StopWatch();
            stopWatch.start();
            CompleteMultipartUploadResult result = awsFileUploadService.uploadLargeFileToS3(new FileInputStream(fileLocal), fileLocal.length(), key);
            stopWatch.stop();
            log.info("上传执行时间（ms）：" + stopWatch.getTotalTimeMillis());
            // 处理上传结果
            String eTag = result.getETag();
            // ...
            log.info("上传成功：" + eTag);
        } catch (Exception e) {
            log.error("上传失败", e);
        }
    }

    /**
     * 文件下载
     */
    @GetMapping("downloadFile")
    public void contextLoadsDownFile(@RequestParam("key") String key) {
        try {
//            String key = "26438e0c-e379-4343-b645-03e8056baaf6/硬件检测.jpg";
            log.info("文件下载的key=" + key);
            StopWatch stopWatch = new StopWatch();
            stopWatch.start();
            InputStream inputStream = awsFileUploadService.downloadToEDS(key);
            stopWatch.stop();
            log.info("文件下载执行时间（ms）：" + stopWatch.getTotalTimeMillis());
            // 处理结果
            if (inputStream != null) {
                // ...
                log.info("文件下载：");
            }
        } catch (Exception e) {
            log.error("文件下载", e);
        }
    }

    /**
     * 预签名URL+文件下载： 测试通过
     * 2024-03-06 14:10:26.721  INFO 29356 --- [           main] c.c.c.f.FilestoreApplicationTest         : 文件下载的key=26438e0c-e379-4343-b645-03e8056baaf6/硬件检测.jpg
     * 2024-03-06 14:10:26.763  INFO 29356 --- [           main] c.c.c.f.FilestoreApplicationTest         : 文件下载执行时间（ms）：41
     * 2024-03-06 14:10:26.763  INFO 29356 --- [           main] c.c.c.f.FilestoreApplicationTest         : 文件下载：http://10.1.2.16:12001/digtal_resources/26438e0c-e379-4343-b645-03e8056baaf6/%E7%A1%AC%E4%BB%B6%E6%A3%80%E6%B5%8B.jpg?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Date=20240306T061026Z&X-Amz-SignedHeaders=host&X-Amz-Expires=3599&X-Amz-Credential=MOTNMUS8FJ8P3YTXQSB5%2F20240306%2F%2Fs3%2Faws4_request&X-Amz-Signature=9536999bbc0f1b6de5bb3cf910af0def5ee1f04389446a1688724f27d5655e65
     */
    public void createSignedUrlForStringGet() {
        try {

            String key = "26438e0c-e379-4343-b645-03e8056baaf6/硬件检测.jpg";
            log.info("文件下载的key=" + key);
            StopWatch stopWatch = new StopWatch();
            stopWatch.start();
            URL url = awsFileUploadService.createSignedUrlForStringGet("digtal_resources", key);
            stopWatch.stop();
            log.info("文件下载执行时间（ms）：" + stopWatch.getTotalTimeMillis());
            // 处理结果
            if (url != null) {
                // ...
                log.info("文件下载：" + url);
            }
        } catch (Exception e) {
            log.error("文件下载", e);
        }
    }

    /**
     * 预签名文件上传
     */
    public void createSignedUrlForStringPut() {
        try {

            String key = "26438e0c-e379-4343-b645-03e8056baaf6/硬件检测.jpg";
            log.info("文件上传的key=" + key);
            StopWatch stopWatch = new StopWatch();
            stopWatch.start();
            URL url = awsFileUploadService.createSignedUrlForStringPut("digtal_resources", key);
            stopWatch.stop();
            log.info("文件上传执行时间（ms）：" + stopWatch.getTotalTimeMillis());
            // 处理结果
            if (url != null) {
                // ...
                log.info("文件上传：" + url);
            }
        } catch (Exception e) {
            log.error("文件上传", e);
        }
    }

    /**
     * 大文件分片下载
     */
    @GetMapping("downloadBigFile")
    public void downloadBigFile(@RequestParam("key") String key) {
        try {
//            String key = "26438e0c-e379-4343-b645-03e8056baaf6/硬件检测.jpg";
            log.info("文件下载的key=" + key);
            StopWatch stopWatch = new StopWatch();
            stopWatch.start();
            awsFileUploadService.downloadBigFile(key);
            stopWatch.stop();
            log.info("文件下载执行时间（ms）：" + stopWatch.getTotalTimeMillis());
        } catch (Exception e) {
            log.error("文件下载", e);
        }
    }
}
