package com.hzqserver.auth.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.LocalDateTime;

/**
 * 基础实体类
 * 包含所有实体类共有的字段
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
public abstract class BaseEntity {
    
    @TableId(type = IdType.AUTO)
    @TableField(value = "id")
    private Long id;
    
    @TableField(value = "created_time")
    private LocalDateTime createdTime = LocalDateTime.now();
    
    @TableField(value = "updated_time")
    private LocalDateTime updatedTime = LocalDateTime.now();
}