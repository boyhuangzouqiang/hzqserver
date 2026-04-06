package com.hzqserver.file.service.impl;

import cn.hutool.core.util.IdUtil;
import cn.hutool.crypto.digest.DigestUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hzqserver.file.config.FileUploadConfig;
import com.hzqserver.file.config.StorageProperties;
import com.hzqserver.file.entity.ChunkUploadRequest;
import com.hzqserver.file.entity.FileChunkRecord;
import com.hzqserver.file.entity.FileUploadRecord;
import com.hzqserver.file.entity.FileUploadResponse;
import com.hzqserver.file.factory.StorageFactory;
import com.hzqserver.file.mapper.FileChunkRecordMapper;
import com.hzqserver.file.mapper.FileUploadRecordMapper;
import com.hzqserver.file.service.FileUploadService;
import com.hzqserver.file.service.storage.StorageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 文件上传服务实现类
 */
@Slf4j
@Service
public class FileUploadServiceImpl implements FileUploadService {
    
    @Autowired
    private StorageFactory storageFactory;
    
    @Autowired
    private StorageProperties storageProperties;
    
    @Autowired
    private FileUploadConfig fileUploadConfig;
    
    @Autowired
    private FileUploadRecordMapper fileUploadRecordMapper;
    
    @Autowired
    private FileChunkRecordMapper fileChunkRecordMapper;
    
    /**
     * 初始化文件上传
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public FileUploadResponse initUpload(ChunkUploadRequest request) {
        log.info("初始化文件上传: fileName={}, fileSize={}, totalChunks={}, chunkSize={}", 
                request.getFileName(), request.getFileSize(), request.getTotalChunks(), request.getChunkSize());
        
        // 验证文件大小
        if (request.getFileSize() > fileUploadConfig.getMaxFileSize()) {
            throw new RuntimeException("文件大小超过限制：" + fileUploadConfig.getMaxFileSize() / 1024 / 1024 + "MB");
        }
        
        // 验证分片大小是否合理（前端控制分片，后端只做范围校验）
        Long chunkSize = request.getChunkSize();
        if (chunkSize != null) {
            if (chunkSize > fileUploadConfig.getMaxChunkSize()) {
                throw new RuntimeException(String.format("分片大小超过限制：最大 %dMB", 
                        fileUploadConfig.getMaxChunkSize() / 1024 / 1024));
            }
            if (chunkSize < fileUploadConfig.getMinChunkSize()) {
                throw new RuntimeException(String.format("分片大小过小：最小 %dMB", 
                        fileUploadConfig.getMinChunkSize() / 1024 / 1024));
            }
        } else {
            throw new RuntimeException("分片大小不能为空");
        }
        
        // 检查文件是否已存在
        FileUploadRecord existingRecord = fileUploadRecordMapper.selectByFileMd5(request.getFileMd5());
        if (existingRecord != null && existingRecord.getStatus() == 2) {
            // 文件已上传完成，直接返回（秒传）
            return FileUploadResponse.builder()
                    .fileId(existingRecord.getId())
                    .fileMd5(existingRecord.getFileMd5())
                    .fileName(existingRecord.getFileName())
                    .fileSize(existingRecord.getFileSize())
                    .accessUrl(existingRecord.getAccessUrl())
                    .status(2)
                    .message("文件已存在")
                    .build();
        }
        
        // 如果存在未完成的上传记录，保留并返回已上传的分片列表（断点续传）
        if (existingRecord != null && existingRecord.getStatus() != 2) {
            log.info("发现未完成的上传记录，准备断点续传: id={}, status={}", existingRecord.getId(), existingRecord.getStatus());
            
            // 查询已上传的分片
            List<FileChunkRecord> chunkRecords = fileChunkRecordMapper.selectByFileMd5(request.getFileMd5());
            List<Integer> uploadedChunks = chunkRecords.stream()
                    .map(FileChunkRecord::getChunkIndex)
                    .collect(Collectors.toList());
            
            log.info("已上传分片数: {}/{}", uploadedChunks.size(), existingRecord.getTotalChunks());
            
            return FileUploadResponse.builder()
                    .fileId(existingRecord.getId())
                    .fileMd5(existingRecord.getFileMd5())
                    .fileName(existingRecord.getFileName())
                    .fileSize(existingRecord.getFileSize())
                    .status(existingRecord.getStatus())
                    .uploadedChunks(uploadedChunks)
                    .message(String.format("检测到未完成的上传，已上传 %d/%d 个分片", 
                            uploadedChunks.size(), existingRecord.getTotalChunks()))
                    .build();
        }
        
        // 创建上传记录
        FileUploadRecord record = new FileUploadRecord();
        record.setFileMd5(request.getFileMd5());
        record.setFileName(request.getFileName());
        record.setFileExt(getFileExtension(request.getFileName()));
        record.setFileSize(request.getFileSize());
        record.setContentType(request.getContentType());
        record.setTotalChunks(request.getTotalChunks());
        record.setUploadedChunks(0);
        record.setChunkSize(request.getChunkSize());
        record.setStatus(0); // 初始化状态
        
        fileUploadRecordMapper.insert(record);
        
        log.info("文件上传记录创建成功: id={}, fileMd5={}", record.getId(), request.getFileMd5());
        
        return FileUploadResponse.builder()
                .fileId(record.getId())
                .fileMd5(request.getFileMd5())
                .fileName(request.getFileName())
                .fileSize(request.getFileSize())
                .status(0)
                .uploadedChunks(new ArrayList<>())
                .message("初始化成功")
                .build();
    }
    
    /**
     * 上传分片
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public FileUploadResponse uploadChunk(ChunkUploadRequest request, MultipartFile file) {
        log.info("上传分片: fileMd5={}, chunkIndex={}/{}", 
                request.getFileMd5(), request.getChunkIndex(), request.getTotalChunks());
        
        try {
            // 验证分片
            if (file.isEmpty()) {
                throw new RuntimeException("分片文件不能为空");
            }
            
            // 计算分片MD5
            String chunkMd5 = DigestUtil.md5Hex(file.getInputStream());
            if (request.getChunkMd5() != null && !request.getChunkMd5().equals(chunkMd5)) {
                throw new RuntimeException("分片MD5校验失败");
            }
            
            // 生成分片存储路径
            String chunkObjectName = String.format("chunks/%s/%d", 
                    request.getFileMd5(), request.getChunkIndex());
            
            // 上传分片到对象存储
            StorageService storageService = storageFactory.getStorageService();
            String bucketName = storageProperties.getDefaultBucket();
            
            storageService.uploadFile(
                    bucketName,
                    chunkObjectName,
                    file.getInputStream(),
                    file.getSize(),
                    "application/octet-stream"
            );
            
            // 保存分片记录
            FileChunkRecord chunkRecord = new FileChunkRecord();
            chunkRecord.setFileMd5(request.getFileMd5());
            chunkRecord.setChunkIndex(request.getChunkIndex());
            chunkRecord.setChunkMd5(chunkMd5);
            chunkRecord.setChunkSize(file.getSize());
            chunkRecord.setStoragePath(chunkObjectName);
            fileChunkRecordMapper.insert(chunkRecord);
            
            // 更新上传记录的已上传分片数
            FileUploadRecord record = fileUploadRecordMapper.selectByFileMd5(request.getFileMd5());
            if (record != null) {
                record.setUploadedChunks(record.getUploadedChunks() + 1);
                record.setStatus(1); // 上传中
                fileUploadRecordMapper.updateById(record);
            }
            
            log.info("分片上传成功: chunkIndex={}", request.getChunkIndex());
            
            return FileUploadResponse.builder()
                    .fileMd5(request.getFileMd5())
                    .fileName(request.getFileName())
                    .fileSize(request.getFileSize())
                    .status(1)
                    .message(String.format("分片 %d/%d 上传成功", 
                            request.getChunkIndex() + 1, request.getTotalChunks()))
                    .build();
                    
        } catch (Exception e) {
            log.error("分片上传失败", e);
            throw new RuntimeException("分片上传失败: " + e.getMessage());
        }
    }
    
    /**
     * 合并分片
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public FileUploadResponse mergeChunks(String fileMd5) {
        log.info("开始合并分片: fileMd5={}", fileMd5);
        
        try {
            // 查询上传记录
            FileUploadRecord record = fileUploadRecordMapper.selectByFileMd5(fileMd5);
            if (record == null) {
                throw new RuntimeException("文件上传记录不存在");
            }
            
            // 查询所有分片记录
            List<FileChunkRecord> chunkRecords = fileChunkRecordMapper.selectByFileMd5(fileMd5);
            if (chunkRecords.size() != record.getTotalChunks()) {
                throw new RuntimeException(String.format("分片不完整: 已上传 %d/%d", 
                        chunkRecords.size(), record.getTotalChunks()));
            }
            
            // 生成最终文件路径
            String fileName = record.getFileName();
            String objectName = String.format("files/%s/%s", fileMd5, fileName);
            
            // 使用存储服务合并分片
            StorageService storageService = storageFactory.getStorageService();
            String bucketName = storageProperties.getDefaultBucket();
            
            // 获取分片对象名称列表（按顺序）
            List<String> sourceObjectNames = chunkRecords.stream()
                    .sorted((a, b) -> a.getChunkIndex().compareTo(b.getChunkIndex()))
                    .map(FileChunkRecord::getStoragePath)
                    .collect(Collectors.toList());
            
            // 合并分片
            storageService.composeFile(
                    bucketName,
                    objectName,
                    sourceObjectNames,
                    record.getContentType()
            );
            
            // 生成访问URL
            String accessUrl = storageService.getFileUrl(bucketName, objectName);
            
            // 更新上传记录
            record.setStoragePath(objectName);
            record.setAccessUrl(accessUrl);
            record.setStatus(2); // 已完成
            fileUploadRecordMapper.updateById(record);
            
            // 合并成功后，清理 MinIO/OSS 中的分片文件（节省存储空间）
            try {
                List<String> chunkObjectNames = chunkRecords.stream()
                        .map(FileChunkRecord::getStoragePath)
                        .collect(Collectors.toList());
                storageService.deleteFiles(bucketName, chunkObjectNames);
                log.info("分片文件清理成功: 共删除 {} 个分片", chunkObjectNames.size());
            } catch (Exception e) {
                // 清理失败不影响主流程，记录日志即可
                log.error("分片文件清理失败，建议手动清理或等待定时任务处理: {}", e.getMessage(), e);
            }
            
            // 删除分片记录
            fileChunkRecordMapper.deleteByFileMd5(fileMd5);
            
            log.info("文件合并成功: fileMd5={}, objectName={}", fileMd5, objectName);
            
            return FileUploadResponse.builder()
                    .fileId(record.getId())
                    .fileMd5(fileMd5)
                    .fileName(record.getFileName())
                    .fileSize(record.getFileSize())
                    .accessUrl(accessUrl)
                    .status(2)
                    .message("文件上传完成")
                    .build();
                    
        } catch (Exception e) {
            log.error("分片合并失败", e);
            // 更新状态为失败
            FileUploadRecord record = fileUploadRecordMapper.selectByFileMd5(fileMd5);
            if (record != null) {
                record.setStatus(3); // 失败
                fileUploadRecordMapper.updateById(record);
            }
            throw new RuntimeException("分片合并失败: " + e.getMessage());
        }
    }
    
    /**
     * 检查文件上传状态（用于断点续传）
     */
    @Override
    public FileUploadResponse checkUploadStatus(String fileMd5) {
        log.info("检查文件上传状态: fileMd5={}", fileMd5);
        
        FileUploadRecord record = fileUploadRecordMapper.selectByFileMd5(fileMd5);
        if (record == null) {
            return FileUploadResponse.builder()
                    .fileMd5(fileMd5)
                    .status(-1)
                    .uploadedChunks(new ArrayList<>())
                    .message("文件未初始化")
                    .build();
        }
        
        // 如果已完成，直接返回
        if (record.getStatus() == 2) {
            return FileUploadResponse.builder()
                    .fileId(record.getId())
                    .fileMd5(record.getFileMd5())
                    .fileName(record.getFileName())
                    .fileSize(record.getFileSize())
                    .accessUrl(record.getAccessUrl())
                    .status(2)
                    .uploadedChunks(new ArrayList<>())
                    .message("文件已上传完成")
                    .build();
        }
        
        // 查询已上传的分片
        List<FileChunkRecord> chunkRecords = fileChunkRecordMapper.selectByFileMd5(fileMd5);
        List<Integer> uploadedChunks = chunkRecords.stream()
                .map(FileChunkRecord::getChunkIndex)
                .collect(Collectors.toList());
        
        return FileUploadResponse.builder()
                .fileId(record.getId())
                .fileMd5(record.getFileMd5())
                .fileName(record.getFileName())
                .fileSize(record.getFileSize())
                .status(record.getStatus())
                .uploadedChunks(uploadedChunks)
                .message(String.format("已上传 %d/%d 个分片", uploadedChunks.size(), record.getTotalChunks()))
                .build();
    }
    
    /**
     * 取消上传
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancelUpload(String fileMd5) {
        log.info("取消文件上传: fileMd5={}", fileMd5);
        
        try {
            // 查询分片记录
            List<FileChunkRecord> chunkRecords = fileChunkRecordMapper.selectByFileMd5(fileMd5);
            
            // 删除对象存储中的分片
            StorageService storageService = storageFactory.getStorageService();
            String bucketName = storageProperties.getDefaultBucket();
            
            List<String> chunkObjectNames = chunkRecords.stream()
                    .map(FileChunkRecord::getStoragePath)
                    .collect(Collectors.toList());
            storageService.deleteFiles(bucketName, chunkObjectNames);
            
            // 删除分片记录
            fileChunkRecordMapper.deleteByFileMd5(fileMd5);
            
            // 删除上传记录
            FileUploadRecord record = fileUploadRecordMapper.selectByFileMd5(fileMd5);
            if (record != null) {
                fileUploadRecordMapper.deleteById(record.getId());
            }
            
            log.info("文件上传已取消: fileMd5={}", fileMd5);
            
        } catch (Exception e) {
            log.error("取消上传失败", e);
            throw new RuntimeException("取消上传失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取文件扩展名
     */
    private String getFileExtension(String fileName) {
        if (fileName == null || !fileName.contains(".")) {
            return "";
        }
        return fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
    }
    
    /**
     * 分页查询已完成的上传记录
     */
    @Override
    public Page<FileUploadRecord> getCompletedRecords(Page<FileUploadRecord> page) {
        log.info("查询已完成上传记录: page={}, size={}", page.getCurrent(), page.getSize());
        return fileUploadRecordMapper.selectCompletedRecords(page);
    }
    
    /**
     * 根据MD5查询文件记录
     */
    @Override
    public FileUploadRecord getFileRecordByMd5(String fileMd5) {
        return fileUploadRecordMapper.selectByFileMd5(fileMd5);
    }
}
