package com.hzqserver.auth.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 权限实体类
 */
@Data
@TableName("sys_permission")
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class SysPermission extends BaseEntity {
    
    @TableField(value = "permission_name")
    private String permissionName;
    
    @TableField(value = "permission_code")
    private String permissionCode;
    
    @TableField(value = "permission_desc")
    private String permissionDesc;
    
    @TableField(value = "parent_id")
    private Long parentId = 0L;
    
    @TableField(value = "status")
    private Integer status = 1;
    
    // 关联角色权限表
    @TableField(exist = false)
    private List<SysRolePermission> rolePermissions;
}