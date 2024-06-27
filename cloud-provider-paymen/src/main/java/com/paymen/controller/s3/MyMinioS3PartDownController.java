package com.paymen.controller.s3;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

/**
 * @description: aws-s3的方式分片下载
 * @author: huangzouqiang
 * @create: 2024-06-27 15:24
 * @Version 1.0
 **/
@RestController
public class MyMinioS3PartDownController {

    private String down_path = "D:\\testminio";

    /**
     * 定义每个分片是20kb
     */
    private Long per_page = 20000L;

//    @Autowired
//    MinioClient minioClient;
//    @Autowired
//    private AsyncConfig asyncConfig;
//
//    /**
//     * 下载分片数据并合并
//     */
//    @GetMapping("/partDownloadMerge")
//    public void downloadAndMerge(@RequestParam("fileName") String fileName) throws Exception {
//        FileInfo fileInfo = findMinioFileInfo(fileName);
//        long pages = fileInfo.getFSize() / per_page;
//        System.out.println("文件分页个数:" + pages + "， 文件大小：" + fileInfo.getFSize());
//        TaskExecutor asyncExecutor = asyncConfig.getAsyncExecutor();
//        for (int i = 0; i <= pages; i++) {
//            int finalI = i;
//            asyncExecutor.execute(() -> {
//                try {
//                    download(finalI * per_page, (finalI + 1) * per_page - 1, finalI, fileInfo);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            });
//        }
//    }
//
//    /**
//     * 从minio文件基本信息，用于分片处理
//     *
//     * @param fileName
//     * @throws Exception
//     */
//    private FileInfo findMinioFileInfo(String fileName) throws Exception {
//        // 获取桶里文件信息
//        StatObjectResponse statObjectResponse = minioClient.statObject(
//                StatObjectArgs.builder()
//                        .bucket("hzq")
//                        .object("testminio/" + fileName)
//                        .build());
//        // 分片下载
//        long fSize = statObjectResponse.size();// 获取长度
//        return new FileInfo(fSize, fileName);
//    }
//
//    /**
//     * 保存单个分片数据落盘
//     *
//     * @param start
//     * @param end
//     * @param page
//     * @param fileInfo
//     * @return
//     * @throws Exception
//     */
//    private String download(long start, long end, long page, FileInfo fileInfo) throws Exception {
//        long fSize = fileInfo.getFSize();
//        String fileName = fileInfo.getFName();
//        // 断点下载 文件存在不需要下载
//        File file = new File(down_path, page + "-" + fileName);
//        // 探测必须放行 若下载分片只下载一半就需要重新下载 所以需要判断文件是否完整
//        if (file.exists() && page != -1 && file.length() == per_page) {
//            System.out.println("文件存在了咯，不处理了");
//            return null;
//        }
//        // 获取桶里文件信息
//        StatObjectResponse statObjectResponse = minioClient.statObject(
//                StatObjectArgs.builder()
//                        .bucket("hzq")
//                        .object("testminio/" + fileName)
//                        .build());
//        // 需要知道 开始-结束 = 分片大小
//        long rangeLenght = end - start + 1;
//        GetObjectResponse stream = minioClient.getObject(
//                GetObjectArgs.builder()
//                        .bucket(statObjectResponse.bucket())   //文件所在的桶
//                        .object(statObjectResponse.object())   //文件的名称
//                        .offset(start)   //文件的开始位置 默认从0开始
//                        .length(rangeLenght)   //文件需要下载的长度
//                        .build());
//        //临时存储分片文件
//        FileOutputStream fos = new FileOutputStream(file);
//        // 定义缓冲区
//        byte[] buffer = new byte[1024];
//        int readLength;
//        //写文件
//        while ((readLength = stream.read(buffer)) != -1) {
//            fos.write(buffer, 0, readLength);
//        }
//        stream.close();
//        fos.flush();
//        fos.close();
//        //判断是不是最后一个分片，如果不是最后一个分片不执行
//        if (end - fSize > 0) {
//            try {
//                System.out.println("开始合并了");
//                this.mergeAllPartFile(fileName, page);
//                System.out.println("文件合并结束了");
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//        return "ok";
//    }
//
//    /**
//     * 合并文件
//     *
//     * @param fName 文件名称
//     * @param page  分片的文件的页
//     */
//    private void mergeAllPartFile(String fName, long page) throws Exception {
//        // 归并文件位置
//        File file = new File(down_path, fName);
//        BufferedOutputStream os = new BufferedOutputStream(new FileOutputStream(file));
//        for (int i = 0; i <= page; i++) {
//            File tempFile = new File(down_path, i + "-" + fName);
//            // 分片没下载或者没下载完需要等待
//            while (!file.exists() || (i != page && tempFile.length() < per_page)) {
//                Thread.sleep(1000);
//                System.out.println("异步线程下载分片数据等待结束，再次查询文件等候已经下载完成了");
//            }
//            byte[] bytes = FileUtils.readFileToByteArray(tempFile);
//            os.write(bytes);
//            os.flush();
//            tempFile.delete();
//        }
//        os.flush();
//        os.close();
//    }
}

