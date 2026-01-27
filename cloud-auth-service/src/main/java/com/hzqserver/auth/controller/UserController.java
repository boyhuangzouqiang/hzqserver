package com.hzqserver.auth.controller;

import com.hzqserver.auth.entity.SysUser;
import com.hzqserver.auth.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户控制器
 * 提供用户管理的REST API接口
 */
@RestController
@RequestMapping("/user")
public class UserController {
    
    @Autowired
    private UserService userService;
    
    /**
     * 获取所有用户
     * 
     * @return 用户列表
     */
    @GetMapping("/list")
    public ResponseEntity<List<SysUser>> getAllUsers() {
        List<SysUser> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }
    
    /**
     * 根据ID获取用户
     * 
     * @param id 用户ID
     * @return 用户信息
     */
    @GetMapping("/{id}")
    public ResponseEntity<SysUser> getUserById(@PathVariable Long id) {
        if (!userService.getUserById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(userService.getUserById(id).get());
    }
    
    /**
     * 创建用户
     * 
     * @param user 用户信息
     * @return 创建后的用户信息
     */
    @PostMapping
    public ResponseEntity<SysUser> createUser(@RequestBody SysUser user) {
        SysUser createdUser = userService.createUser(user);
        return ResponseEntity.ok(createdUser);
    }
    
    /**
     * 更新用户
     * 
     * @param id 用户ID
     * @param user 用户信息
     * @return 更新后的用户信息
     */
    @PutMapping("/{id}")
    public ResponseEntity<SysUser> updateUser(@PathVariable Long id, @RequestBody SysUser user) {
        SysUser updatedUser = userService.updateUser(id, user);
        if (updatedUser == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updatedUser);
    }
    
    /**
     * 删除用户
     * 
     * @param id 用户ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        boolean deleted = userService.deleteUser(id);
        if (!deleted) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build();
    }
}