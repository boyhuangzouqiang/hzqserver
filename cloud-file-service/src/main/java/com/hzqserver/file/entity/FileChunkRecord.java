package com.hzqserver.file.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 分片上传记录实体类
 */
@Data
@TableName("file_chunk_record")
public class FileChunkRecord implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    
    /**
     * 文件MD5
     */
    private String fileMd5;
    
    /**
     * 分片索引（从0开始）
     */
    private Integer chunkIndex;
    
    /**
     * 分片MD5
     */
    private String chunkMd5;
    
    /**
     * 分片大小
     */
    private Long chunkSize;
    
    /**
     * 分片存储路径
     */
    private String storagePath;
    
    /**
     * 上传时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime uploadTime;
}
