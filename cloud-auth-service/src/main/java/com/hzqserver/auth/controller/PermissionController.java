package com.hzqserver.auth.controller;

import com.hzqserver.auth.entity.SysPermission;
import com.hzqserver.auth.service.PermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 权限控制器
 * 提供权限管理的REST API接口
 */
@RestController
@RequestMapping("/permission")
public class PermissionController {
    
    @Autowired
    private PermissionService permissionService;
    
    /**
     * 获取所有权限列表
     * 
     * @return 权限列表
     */
    @GetMapping("/list")
    public ResponseEntity<List<SysPermission>> listPermissions() {
        List<SysPermission> permissions = permissionService.getAllPermissions();
        return ResponseEntity.ok(permissions);
    }
    
    /**
     * 根据ID获取权限
     * 
     * @param id 权限ID
     * @return 权限信息
     */
    @GetMapping("/{id}")
    public ResponseEntity<SysPermission> getPermissionById(@PathVariable Long id) {
        if (!permissionService.getPermissionById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(permissionService.getPermissionById(id).get());
    }
    
    /**
     * 创建新权限
     * 
     * @param permission 权限信息
     * @return 创建结果
     */
    @PostMapping("/create")
    public ResponseEntity<SysPermission> createPermission(@RequestBody SysPermission permission) {
        SysPermission createdPermission = permissionService.createPermission(permission);
        return ResponseEntity.ok(createdPermission);
    }
    
    /**
     * 更新权限
     * 
     * @param id 权限ID
     * @param permission 权限信息
     * @return 更新结果
     */
    @PutMapping("/{id}")
    public ResponseEntity<SysPermission> updatePermission(@PathVariable Long id, @RequestBody SysPermission permission) {
        SysPermission updatedPermission = permissionService.updatePermission(id, permission);
        if (updatedPermission == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updatedPermission);
    }
    
    /**
     * 删除权限
     * 
     * @param id 权限ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePermission(@PathVariable Long id) {
        boolean deleted = permissionService.deletePermission(id);
        if (!deleted) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build();
    }
}