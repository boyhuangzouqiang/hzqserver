package com.hzqserver.auth.service;

import com.hzqserver.auth.entity.SysUser;
import com.hzqserver.auth.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

/**
 * 认证服务类
 * 处理用户身份验证和JWT令牌生成
 */
@Service
public class AuthService {
    
    @Autowired
    private AuthenticationManager authenticationManager;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @Autowired
    private UserService userService;
    
    /**
     * 验证用户凭据并生成JWT令牌
     * 
     * @param username 用户名
     * @param password 密码
     * @return JWT令牌字符串
     * @throws RuntimeException 验证失败时抛出异常
     */
    public String authenticateUser(String username, String password) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password)
        );
        
        SecurityContextHolder.getContext().setAuthentication(authentication);
        
        SysUser user = userService.findByUsername(username);
        List<String> roles = Arrays.asList("ROLE_USER"); // 实际应从数据库获取用户角色
        
        return jwtUtil.generateToken(user.getUsername(), roles);
    }
}