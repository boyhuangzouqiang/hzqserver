package com.hzqserver.auth.entity;

import javax.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 系统用户实体类
 * 对应数据库中的sys_user表，存储用户的基本信息
 */
@Entity
@Table(name = "sys_user")
@Data
public class SysUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false)
    private String username;
    
    @Column(nullable = false)
    private String password;
    
    private String email;
    
    private String phone;
    
    @Column(columnDefinition = "TINYINT")
    private Integer status = 1;
    
    private LocalDateTime createdTime;
    
    private LocalDateTime updatedTime;
}