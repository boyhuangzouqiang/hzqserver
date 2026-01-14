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
 * 角色实体类
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("sys_role")
public class SysRole {
    
    @TableId(value = "id", type = IdType.AUTO)
    @TableField(value = "id")
    private Long id;
    
    @TableField(value = "role_name")
    private String roleName;
    
    @TableField(value = "role_code")
    private String roleCode;
    
    @TableField(value = "role_desc")
    private String roleDesc;
    
    @TableField(value = "status")
    private Integer status = 1;
    
    @TableField(value = "created_time")
    private LocalDateTime createdTime;
    
    @TableField(value = "updated_time")
    private LocalDateTime updatedTime;
}