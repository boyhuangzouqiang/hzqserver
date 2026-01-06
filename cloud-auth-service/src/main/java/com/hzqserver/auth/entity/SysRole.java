package com.hzqserver.auth.entity;

import javax.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 系统角色实体类
 * 对应数据库中的sys_role表，存储系统角色信息
 */
@Entity
@Table(name = "sys_role")
@Data
public class SysRole {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false)
    private String roleName;
    
    @Column(unique = true, nullable = false)
    private String roleCode;
    
    private String roleDesc;
    
    @Column(columnDefinition = "TINYINT")
    private Integer status = 1;
    
    private LocalDateTime createdTime;
    
    private LocalDateTime updatedTime;
    
    @ManyToMany
    @JoinTable(
        name = "sys_role_permission",
        joinColumns = @JoinColumn(name = "role_id"),
        inverseJoinColumns = @JoinColumn(name = "permission_id")
    )
    private List<SysPermission> permissions;
}