package com.hzqserver.file.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;
import java.util.List;

/**
 * 文件上传响应DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileUploadResponse implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 文件ID
     */
    private Long fileId;
    
    /**
     * 文件MD5
     */
    private String fileMd5;
    
    /**
     * 文件名
     */
    private String fileName;
    
    /**
     * 文件大小
     */
    private Long fileSize;
    
    /**
     * 访问URL
     */
    private String accessUrl;
    
    /**
     * 上传状态：0-初始化，1-上传中，2-已完成，3-失败
     */
    private Integer status;
    
    /**
     * 已上传的分片索引列表（用于断点续传）
     */
    private List<Integer> uploadedChunks;
    
    /**
     * 消息
     */
    private String message;
}
