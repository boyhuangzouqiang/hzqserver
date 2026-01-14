package com.hzqserver.auth.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 用户角色关联实体类
 */
@Data
@NoArgsConstructor
@TableName("sys_user_role")
public class SysUserRole {
    
    @TableId(value = "id", type = IdType.AUTO)
    @TableField(value = "id")
    private Long id;
    
    @TableField(value = "user_id")
    private Long userId;
    
    @TableField(value = "role_id")
    private Long roleId;
    
    @TableField(value = "created_time")
    private LocalDateTime createdTime = LocalDateTime.now();

}