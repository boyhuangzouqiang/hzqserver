package com.hzqserver.file.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hzqserver.file.entity.ChunkUploadRequest;
import com.hzqserver.file.entity.FileUploadRecord;
import com.hzqserver.file.entity.FileUploadResponse;
import org.springframework.web.multipart.MultipartFile;

/**
 * 文件上传服务接口
 */
public interface FileUploadService {
    
    /**
     * 初始化文件上传
     * @param request 上传请求
     * @return 上传响应
     */
    FileUploadResponse initUpload(ChunkUploadRequest request);
    
    /**
     * 上传分片
     * @param request 分片上传请求
     * @param file 分片文件
     * @return 上传响应
     */
    FileUploadResponse uploadChunk(ChunkUploadRequest request, MultipartFile file);
    
    /**
     * 合并分片
     * @param fileMd5 文件MD5
     * @return 上传响应
     */
    FileUploadResponse mergeChunks(String fileMd5);
    
    /**
     * 检查文件上传状态（用于断点续传）
     * @param fileMd5 文件MD5
     * @return 上传响应，包含已上传的分片列表
     */
    FileUploadResponse checkUploadStatus(String fileMd5);
    
    /**
     * 取消上传
     * @param fileMd5 文件MD5
     */
    void cancelUpload(String fileMd5);
    
    /**
     * 分页查询已完成的上传记录
     * @param page 分页参数
     * @return 分页结果
     */
    Page<FileUploadRecord> getCompletedRecords(Page<FileUploadRecord> page);
    
    /**
     * 根据MD5查询文件记录
     */
    FileUploadRecord getFileRecordByMd5(String fileMd5);
}
