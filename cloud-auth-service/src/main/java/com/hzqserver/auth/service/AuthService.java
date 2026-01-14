package com.hzqserver.auth.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hzqserver.auth.entity.SysUser;
import com.hzqserver.auth.mapper.SysUserMapper;
import com.hzqserver.auth.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

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
    
    @Autowired
    private SysUserMapper sysUserMapper;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    /**
     * 验证用户凭据并生成JWT令牌
     * 
     * @param username 用户名
     * @param password 密码
     * @return JWT令牌字符串
     * @throws RuntimeException 验证失败时抛出异常
     */
    public String authenticateUser(String username, String password) {
        // 直接通过数据库验证，避免循环引用
        QueryWrapper<SysUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", username);
        SysUser user = sysUserMapper.selectOne(queryWrapper);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("密码错误");
        }
        
        List<String> roles = Arrays.asList("ROLE_USER"); // 实际应从数据库获取用户角色
        
        return jwtUtil.generateToken(user.getUsername(), roles);
    }
}