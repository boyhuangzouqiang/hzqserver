package com.hzqserver.auth.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * 用户实体类
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("sys_user")
public class SysUser {
    
    @TableId(value = "id", type = IdType.AUTO)
    @TableField(value = "id")
    private Long id;
    
    @TableField(value = "username")
    private String username;
    
    @TableField(value = "password")
    private String password;
    
    @TableField(value = "email")
    private String email;
    
    @TableField(value = "phone")
    private String phone;
    
    @TableField(value = "status")
    private Integer status = 1; // 0-禁用, 1-启用
    
    @TableField(value = "created_time")
    private LocalDateTime createdTime;
    
    @TableField(value = "updated_time")
    private LocalDateTime updatedTime;
}