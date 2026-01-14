package com.hzqserver.auth.controller;

import com.hzqserver.auth.entity.SysRole;
import com.hzqserver.auth.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 角色控制器
 * 提供角色管理的REST API接口
 */
@RestController
@RequestMapping("/role")
public class RoleController {
    
    @Autowired
    private RoleService roleService;
    
    /**
     * 获取所有角色
     * 
     * @return 所有角色列表
     */
    @GetMapping("/list")
    public ResponseEntity<List<SysRole>> listRoles() {
        List<SysRole> roles = roleService.getAllRoles();
        return ResponseEntity.ok(roles);
    }
    
    /**
     * 根据ID获取角色
     * 
     * @param id 角色ID
     * @return 角色信息
     */
    @GetMapping("/{id}")
    public ResponseEntity<SysRole> getRoleById(@PathVariable Long id) {
        if (!roleService.getRoleById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(roleService.getRoleById(id).get());
    }
    
    /**
     * 创建新角色
     * 
     * @param role 角色信息
     * @return 创建后的角色
     */
    @PostMapping("/create")
    public ResponseEntity<SysRole> createRole(@RequestBody SysRole role) {
        SysRole createdRole = roleService.createRole(role);
        return ResponseEntity.ok(createdRole);
    }
    
    /**
     * 更新角色
     * 
     * @param id 角色ID
     * @param role 角色信息
     * @return 更新后的角色
     */
    @PutMapping("/{id}")
    public ResponseEntity<SysRole> updateRole(@PathVariable Long id, @RequestBody SysRole role) {
        SysRole updatedRole = roleService.updateRole(id, role);
        if (updatedRole == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updatedRole);
    }
    
    /**
     * 删除角色
     * 
     * @param id 角色ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteRole(@PathVariable Long id) {
        boolean deleted = roleService.deleteRole(id);
        if (!deleted) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok("角色删除成功");
    }
}