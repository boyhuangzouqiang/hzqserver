package com.hzqserver.file.entity;

import lombok.Data;
import java.io.Serializable;

/**
 * 分片上传请求DTO
 */
@Data
public class ChunkUploadRequest implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
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
     * 内容类型
     */
    private String contentType;
    
    /**
     * 当前分片索引（从0开始）
     */
    private Integer chunkIndex;
    
    /**
     * 分片总数
     */
    private Integer totalChunks;
    
    /**
     * 分片大小
     */
    private Long chunkSize;
    
    /**
     * 当前分片的MD5
     */
    private String chunkMd5;
}
