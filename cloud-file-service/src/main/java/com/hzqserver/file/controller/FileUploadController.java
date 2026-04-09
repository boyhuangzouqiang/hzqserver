package com.hzqserver.file.controller;

import com.core.resp.ResponseResult;
import com.hzqserver.file.config.FileUploadConfig;
import com.hzqserver.file.entity.ChunkUploadRequest;
import com.hzqserver.file.entity.FileUploadRecord;
import com.hzqserver.file.entity.FileUploadResponse;
import com.hzqserver.file.factory.StorageFactory;
import com.hzqserver.file.config.StorageProperties;
import com.hzqserver.file.service.FileUploadService;
import com.hzqserver.file.service.storage.StorageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * 文件上传控制器
 * 支持分片上传、断点续传、秒传功能
 */
@Slf4j
@RestController
@RequestMapping("/api/file")
public class FileUploadController {
    
    @Autowired
    private FileUploadService fileUploadService;
    
    @Autowired
    private FileUploadConfig fileUploadConfig;
    
    @Autowired
    private StorageFactory storageFactory;
    
    @Autowired
    private StorageProperties storageProperties;
    
    /**
     * 初始化文件上传
     */
    @PostMapping("/upload/init")
    public ResponseResult initUpload(@RequestBody ChunkUploadRequest request) {
        log.info("初始化文件上传: {}", request.getFileName());
        try {
            FileUploadResponse response = fileUploadService.initUpload(request);
            return ResponseResult.success(response);
        } catch (Exception e) {
            log.error("初始化文件上传失败", e);
            return ResponseResult.error(e.getMessage());
        }
    }
    
    /**
     * 上传分片
     */
    @PostMapping("/upload/chunk")
    public ResponseResult uploadChunk(
            @ModelAttribute ChunkUploadRequest request,
            @RequestParam("file") MultipartFile file) {
        log.info("上传分片: {} - {}/{}", request.getFileMd5(), request.getChunkIndex(), request.getTotalChunks());
        try {
            FileUploadResponse response = fileUploadService.uploadChunk(request, file);
            return ResponseResult.success(response);
        } catch (Exception e) {
            log.error("上传分片失败", e);
            return ResponseResult.error(e.getMessage());
        }
    }
    
    /**
     * 合并分片
     */
    @PostMapping("/upload/merge")
    public ResponseResult mergeChunks(
            @RequestParam("fileMd5") String fileMd5) {
        log.info("合并分片: {}", fileMd5);
        try {
            FileUploadResponse response = fileUploadService.mergeChunks(fileMd5);
            return ResponseResult.success(response);
        } catch (Exception e) {
            log.error("合并分片失败", e);
            return ResponseResult.error(e.getMessage());
        }
    }
    
    /**
     * 检查文件上传状态（用于断点续传）
     */
    @GetMapping("/upload/status")
    public ResponseResult checkUploadStatus(
            @RequestParam("fileMd5") String fileMd5) {
        log.info("检查文件上传状态: {}", fileMd5);
        try {
            FileUploadResponse response = fileUploadService.checkUploadStatus(fileMd5);
            return ResponseResult.success(response);
        } catch (Exception e) {
            log.error("检查上传状态失败", e);
            return ResponseResult.error(e.getMessage());
        }
    }
    
    /**
     * 取消上传
     */
    @DeleteMapping("/upload/cancel")
    public ResponseResult cancelUpload(
            @RequestParam("fileMd5") String fileMd5) {
        log.info("取消文件上传: {}", fileMd5);
        try {
            fileUploadService.cancelUpload(fileMd5);
            return ResponseResult.success();
        } catch (Exception e) {
            log.error("取消上传失败", e);
            return ResponseResult.error(e.getMessage());
        }
    }
    
    /**
     * 获取上传配置
     * 前端可根据文件大小和网络情况动态调整分片大小
     */
    @GetMapping("/upload/config")
    public ResponseResult getUploadConfig() {
        try {
            Map<String, Object> config = new HashMap<>();
            // 推荐分片大小：5MB（适用于大多数场景）
            config.put("recommendedChunkSize", 5242880L);
            // 最大分片大小：10MB
            config.put("maxChunkSize", 10485760L);
            // 最小分片大小：1MB
            config.put("minChunkSize", 1048576L);
            // 最大文件大小：1GB
            config.put("maxFileSize", 1073741824L);
            return ResponseResult.success(config);
        } catch (Exception e) {
            log.error("获取上传配置失败", e);
            return ResponseResult.error(e.getMessage());
        }
    }
    
    /**
     * 获取上传历史记录
     */
    @GetMapping("/upload/history")
    public ResponseResult getUploadHistory(
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "pageSize", defaultValue = "20") Integer pageSize) {
        log.info("获取上传历史: page={}, pageSize={}", page, pageSize);
        try {
            com.baomidou.mybatisplus.extension.plugins.pagination.Page<FileUploadRecord> pageParam = 
                new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(page, pageSize);
            com.baomidou.mybatisplus.extension.plugins.pagination.Page<FileUploadRecord> resultPage = 
                fileUploadService.getCompletedRecords(pageParam);
            
            Map<String, Object> result = new HashMap<>();
            result.put("list", resultPage.getRecords());
            result.put("total", resultPage.getTotal());
            result.put("page", resultPage.getCurrent());
            result.put("pageSize", resultPage.getSize());
            return ResponseResult.success(result);
        } catch (Exception e) {
            log.error("获取上传历史失败", e);
            return ResponseResult.error(e.getMessage());
        }
    }
    
    /**
     * 下载文件（支持HTTP Range断点续传）
     */
    @GetMapping("/download/{fileMd5}")
    public void downloadFile(@PathVariable("fileMd5") String fileMd5, 
                            HttpServletRequest request,
                            HttpServletResponse response) {
        log.info("下载文件: fileMd5={}", fileMd5);
        
        try {
            // 查询文件记录
            FileUploadRecord record = fileUploadService.getFileRecordByMd5(fileMd5);
            
            if (record == null || record.getStatus() != 2) {
                response.setStatus(404);
                response.getWriter().write("文件不存在");
                return;
            }
            
            // 通过存储服务下载文件
            StorageService storageService = storageFactory.getStorageService();
            String bucketName = storageProperties.getDefaultBucket();
            
            // 获取Range请求头
            String rangeHeader = request.getHeader("Range");
            long startByte = 0;
            long endByte = record.getFileSize() - 1;
            long contentLength = record.getFileSize();
            
            // 解析Range请求
            if (rangeHeader != null && rangeHeader.startsWith("bytes=")) {
                String range = rangeHeader.substring(6);
                String[] ranges = range.split("-");
                startByte = Long.parseLong(ranges[0]);
                if (ranges.length > 1 && !ranges[1].isEmpty()) {
                    endByte = Long.parseLong(ranges[1]);
                }
                contentLength = endByte - startByte + 1;
                
                // 返回206 Partial Content
                response.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT);
                response.setHeader("Content-Range", 
                    String.format("bytes %d-%d/%d", startByte, endByte, record.getFileSize()));
                log.info("处理Range请求: {}-{}/{}", startByte, endByte, record.getFileSize());
            } else {
                response.setStatus(HttpServletResponse.SC_OK);
            }
            
            // 设置响应头
            response.setContentType(record.getContentType());
            response.setHeader("Accept-Ranges", "bytes");  // 支持Range请求
            response.setHeader("Content-Length", String.valueOf(contentLength));
            response.setHeader("Content-Disposition", 
                "attachment; filename=" + URLEncoder.encode(record.getFileName(), "UTF-8"));
            
            // 下载文件并写入响应
            try (InputStream inputStream = storageService.downloadFileRange(
                        bucketName, record.getStoragePath(), startByte, endByte);
                 OutputStream outputStream = response.getOutputStream()) {
                
                byte[] buffer = new byte[8192];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
                outputStream.flush();
            }
            
        } catch (Exception e) {
            log.error("下载文件失败", e);
            try {
                response.setStatus(500);
                response.getWriter().write("下载失败: " + e.getMessage());
            } catch (Exception ex) {
                log.error("写入错误响应失败", ex);
            }
        }
    }
}
