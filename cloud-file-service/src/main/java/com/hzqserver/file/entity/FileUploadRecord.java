package com.hzqserver.file.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 文件上传记录实体类
 */
@Data
@TableName("file_upload_record")
public class FileUploadRecord implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    
    /**
     * 文件唯一标识（MD5）
     */
    private String fileMd5;
    
    /**
     * 原始文件名
     */
    private String fileName;
    
    /**
     * 文件扩展名
     */
    private String fileExt;
    
    /**
     * 文件大小（字节）
     */
    private Long fileSize;
    
    /**
     * 内容类型（MIME Type）
     */
    private String contentType;
    
    /**
     * 分片总数
     */
    private Integer totalChunks;
    
    /**
     * 已上传分片数
     */
    private Integer uploadedChunks;
    
    /**
     * 分片大小
     */
    private Long chunkSize;
    
    /**
     * 存储路径（MinIO中的对象键）
     */
    private String storagePath;
    
    /**
     * 访问URL
     */
    private String accessUrl;
    
    /**
     * 上传状态：0-初始化，1-上传中，2-已完成，3-失败
     */
    private Integer status;
    
    /**
     * 上传人ID
     */
    private Long uploadUserId;
    
    /**
     * 上传人名称
     */
    private String uploadUserName;
    
    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    
    /**
     * 逻辑删除：0-未删除，1-已删除
     */
    @TableLogic
    private Integer deleted;
    
    /**
     * 删除标识;0-未删除 1-已删除
     */
    private Integer deleteFlag;
    
    /**
     * 创建人
     */
    private Integer createdBy;
    
    /**
     * 创建日期
     */
    private LocalDateTime creationDate;
    
    /**
     * 更新人
     */
    private Integer lastUpdatedBy;
    
    /**
     * 更新日期
     */
    private LocalDateTime lastUpdateDate;
    
    /**
     * 备注说明
     */
    private String remark;
}
