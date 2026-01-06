package com.hzqserver.auth.entity;

import javax.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 系统权限实体类
 * 对应数据库中的sys_permission表，存储系统权限信息
 */
@Entity
@Table(name = "sys_permission")
@Data
public class SysPermission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String permissionName;
    
    @Column(unique = true, nullable = false)
    private String permissionCode;
    
    private String permissionDesc;
    
    private Long parentId = 0L;
    
    @Column(columnDefinition = "TINYINT")
    private Integer status = 1;
    
    private LocalDateTime createdTime;
    
    private LocalDateTime updatedTime;
    
    @ManyToMany(mappedBy = "permissions")
    private List<SysRole> roles;
}