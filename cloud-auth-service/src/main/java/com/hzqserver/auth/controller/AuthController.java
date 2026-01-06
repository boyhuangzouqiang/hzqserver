package com.hzqserver.auth.controller;

import com.hzqserver.auth.entity.SysUser;
import com.hzqserver.auth.service.AuthService;
import com.hzqserver.auth.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

/**
 * 认证控制器
 * 提供用户登录和注册的REST API接口
 */
@Slf4j
@RestController
@RequestMapping("/auth")
public class AuthController {
    
    @Autowired
    private AuthService authService;
    
    @Autowired
    private UserService userService;
    
    /**
     * 用户登录接口
     * 接收用户名和密码，验证后返回JWT令牌
     * 
     * @param username 用户名
     * @param password 密码
     * @return 包含JWT令牌的响应
     */
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestParam String username, @RequestParam String password) {
        try {
            String token = authService.authenticateUser(username, password);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("token", token);
            response.put("message", "Login successful");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error(e.getMessage());
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "用户名或密码错误");
            return ResponseEntity.ok(response);
        }
    }
    
    /**
     * 用户注册接口
     * 创建新用户账户
     * 
     * @param user 用户信息
     * @return 注册结果
     */
    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(@Valid @RequestBody SysUser user) {
        try {
            SysUser registeredUser = userService.registerUser(user);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", registeredUser);
            response.put("message", "User registered successfully");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Registration failed: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}