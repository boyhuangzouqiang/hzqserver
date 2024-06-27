//package com.paymen.controller.minio;
//
//import com.paymen.config.AsyncConfig;
//import com.paymen.myminio.FileInfo;
//import com.paymen.utils.FileUtils;
//import org.apache.http.HttpEntity;
//import org.apache.http.client.methods.CloseableHttpResponse;
//import org.apache.http.client.methods.HttpGet;
//import org.apache.http.impl.client.CloseableHttpClient;
//import org.apache.http.impl.client.HttpClients;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.core.task.TaskExecutor;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RestController;
//
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import java.io.*;
//import java.net.URLDecoder;
//import java.net.URLEncoder;
//
///**
// * @description: 这个案例是内部接口调用，性能不太好，重构下，这个类供参考
// * @author: huangzouqiang
// * @create: 2024-06-26 20:19
// * @Version 1.0
// **/
//@RestController
//public class minioController {
//
//    private static final String utf8 = "utf8";
//
//    private String down_path = "D:\\testminio";
//    private Long per_page = 20000L;
//
//    @Autowired
//    MinioClient minioClient;
//    @Autowired
//    private AsyncConfig asyncConfig;
//
//
//    @GetMapping("/singlePartFileDownload")
//    public void singlePartFileDownload(@RequestParam("fileName")String fileName, HttpServletRequest request, HttpServletResponse response) throws Exception {
//        InputStream is = null;
//        OutputStream os = null;
//        GetObjectResponse stream = null;
//        try {
//            // 获取桶里文件信息
//            StatObjectResponse statObjectResponse = minioClient.statObject(
//                    StatObjectArgs.builder()
//                            .bucket("hzq")
//                            .object(fileName)
//                            .build());
//            // 分片下载
//            long fSize = statObjectResponse.size();// 获取长度
//            response.setContentType("application/octet-stream");
//            fileName = URLEncoder.encode(fileName, utf8);
//            response.addHeader("Content-Disposition", "attachment;filename=" + fileName);
//            //根据前端传来的Range 判断支不支持分片下载
//            response.setHeader("Accept-Range", "bytes");
//            //文件大小
//            response.setHeader("fSize", String.valueOf(fSize));
//            //文件名称
//            response.setHeader("fName", fileName);
//            response.setCharacterEncoding(utf8);
//            // 定义下载的开始和结束位置
//            long startPos = 0;
//            long lastPos = fSize - 1;
//            //判断前端需不需要使用分片下载
//            if (null != request.getHeader("Range")) {
//                response.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT);
//                String numRange = request.getHeader("Range").replaceAll("bytes=", "");
//                System.out.println("请求头：" + request.getHeader("Range"));
//                String[] strRange = numRange.split("-");
//                if (strRange.length == 2) {
//                    startPos = Long.parseLong(strRange[0].trim());
//                    lastPos = Long.parseLong(strRange[1].trim());
//                    // 若结束字节超出文件大小 取文件大小
//                    if (lastPos >= fSize - 1) {
//                        lastPos = fSize - 1;
//                        System.out.println("请求头last："+ lastPos);
//                    }
//                } else {
//                    // 若只给一个长度 开始位置一直到结束
//                    startPos = Long.parseLong(numRange.replaceAll("-", "").trim());
//                }
//            }
//
//            //要下载的长度
//            long rangeLenght = lastPos - startPos + 1;
//            //组装断点下载基本信息
//            String contentRange = new StringBuffer("bytes").append(startPos).append("-").append(lastPos).append("/").append(fSize).toString();
//            response.setHeader("Content-Range", contentRange);
//            response.setHeader("Content-Lenght", String.valueOf(rangeLenght));
//            os = new BufferedOutputStream(response.getOutputStream());
//
//            //minio上获取文件信息
//            stream = minioClient.getObject(
//                    GetObjectArgs.builder()
//                            .bucket(statObjectResponse.bucket())   //文件所在的桶
//                            .object(statObjectResponse.object())   //文件的名称
//                            .offset(startPos)   //文件的开始位置 默认从0开始
//                            .length(rangeLenght)   //文件需要下载的长度
//                            .build());
//
//            os = new BufferedOutputStream(response.getOutputStream());
//            //将读取的文件写入到OutputStream中
//            byte[] buffer = new byte[1024];
//            long bytesWritten = 0;
//            int bytesRead = -1;
//            while ((bytesRead = stream.read(buffer)) != -1) {
//                //已经读取的长度和本次读取的长度之和是否大于需要读取的长度（实质就是判断是否最后一行）
//                if (bytesWritten + bytesRead > rangeLenght) {
//                    os.write(buffer, 0, (int) (rangeLenght - bytesWritten));
//                    break;
//                } else {
//                    os.write(buffer, 0, bytesRead);
//                    bytesWritten += bytesRead;
//                }
//            }
//            os.flush();
//            response.flushBuffer();
//        } finally {
//            if (is != null) {
//                is.close();
//            }
//            if (os != null) {
//                os.close();
//            }
//            if(stream != null){
//                stream.close();
//            }
//        }
//    }
//
//
//    /**
//     * 保存单个分片的下载数据
//     *
//     * @param start
//     * @param end
//     * @param page
//     * @param fName
//     * @return
//     * @throws Exception
//     */
//    private FileInfo download(long start, long end, long page, String fName) throws Exception {
//        // 断点下载 文件存在不需要下载
//        File file = new File(down_path, page + "-" + fName);
//        // 探测必须放行 若下载分片只下载一半就需要重新下载 所以需要判断文件是否完整
//        if (file.exists() && page != -1 && file.length() == per_page) {
//            System.out.println("文件存在了咯，不处理了");
//            return null;
//        }
//        // 需要知道 开始-结束 = 分片大小
//        CloseableHttpClient client = HttpClients.createDefault();
//        // httpclient进行请求
//        HttpGet httpGet = new HttpGet("http://localhost:8088/paymentservice/singlePartFileDownload?fileName=" + fName);
//        // 告诉服务端做分片下载，并且告诉服务器下载到那个位置
//        httpGet.setHeader("Range", "bytes=" + start + "-" + end);
//        CloseableHttpResponse response = client.execute(httpGet);
//        String fSize = response.getFirstHeader("fSize").getValue();
//        fName = URLDecoder.decode(response.getFirstHeader("fName").getValue(), "utf-8");
//        HttpEntity entity = response.getEntity();// 获取文件流对象
//        InputStream is = entity.getContent();
//        //临时存储分片文件
//        FileOutputStream fos = new FileOutputStream(file);
//        // 定义缓冲区
//        byte[] buffer = new byte[1024];
//        int readLength;
//        //写文件
//        while ((readLength = is.read(buffer)) != -1) {
//            fos.write(buffer, 0, readLength);
//        }
//        is.close();
//        fos.flush();
//        fos.close();
//        //判断是不是最后一个分片，如果不是最后一个分片不执行
//        if (end - Long.parseLong(fSize) > 0) {
//            try {
//                System.out.println("开始合并了");
//                this.mergeAllPartFile(fName, page);
//                System.out.println("文件合并结束了");
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//        return new FileInfo(Long.parseLong(fSize), fName);
//    }
//
//    /**
//     * 开启多线程下载分片
//     *
//     * @param fileName
//     * @return
//     * @throws Exception
//     */
//    @GetMapping("/fenPianDownloadFile")
//    public String fenPianDownloadFile(@RequestParam("fileName")String fileName) throws Exception {
//        TaskExecutor asyncExecutor = asyncConfig.getAsyncExecutor();
//        //探测文件信息
//        FileInfo fileInfo = download(0, 10, -1, fileName);
//        if (fileInfo != null) {
//            long pages = fileInfo.getFSize() / per_page;
//            System.out.println("文件分页个数:" + pages + "， 文件大小：" + fileInfo.getFSize());
//            for (int i = 0; i <= pages; i++) {
//                asyncExecutor.execute(new Download(i * per_page, (i + 1) * per_page - 1, i, fileInfo.getFName()));
//            }
//        }
//        return "success";
//    }
//
//    /**
//     * 异步线程下载分片数据
//     *
//     */
//    class Download implements Runnable {
//        //开始下载位置
//        long start;
//        //结束下载的位置
//        long end;
//        //当前的分片
//        long page;
//        //文件名称
//        String fName;
//
//        public Download(long start, long end, long page, String fName) {
//            this.start = start;
//            this.end = end;
//            this.page = page;
//            this.fName = fName;
//        }
//
//        @Override
//        public void run() {
//            try {
//                //下载单个分片
//                download(start, end, page, fName);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//    }
//
//    /**
//     * 合并文件
//     *
//     * @param fName 文件名称
//     * @param page 分片的文件的页
//     *
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
//                System.out.println("结束睡觉，再次查询文件等候已经下载完成了");
//            }
//            byte[] bytes = FileUtils.readFileToByteArray(tempFile);
//            os.write(bytes);
//            os.flush();
//            tempFile.delete();
//        }
//        //删除探测文件
//        File file1 = new File(down_path, -1 + "-" + fName);
//        file1.delete();
//        os.flush();
//        os.close();
//    }
//}
