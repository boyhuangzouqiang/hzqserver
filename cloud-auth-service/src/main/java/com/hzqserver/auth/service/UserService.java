package com.hzqserver.auth.service;

import com.hzqserver.auth.entity.SysUser;
import com.hzqserver.auth.repository.SysUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 用户服务类
 * 提供用户管理功能，包括用户查询、注册和权限验证
 */
@Service
public class UserService implements UserDetailsService {
    
    @Autowired
    private SysUserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    /**
     * 根据用户名加载用户详细信息
     * 实现UserDetailsService接口，用于Spring Security用户认证
     * 
     * @param username 用户名
     * @return 用户详细信息
     * @throws UsernameNotFoundException 用户不存在时抛出异常
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        SysUser user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
        
        List<GrantedAuthority> authorities = getUserAuthorities(user.getId());
        
        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .authorities(authorities)
                .accountExpired(false)
                .accountLocked(false)
                .credentialsExpired(false)
                .disabled(false)
                .build();
    }
    
    /**
     * 获取用户权限列表
     * 根据用户ID查询用户的角色和权限
     * 
     * @param userId 用户ID
     * @return 权限列表
     */
    private List<GrantedAuthority> getUserAuthorities(Long userId) {
        List<GrantedAuthority> authorities = new ArrayList<>();
        
        // 根据数据库初始化脚本，admin用户ID为1，对应管理员角色
        if (userId.equals(1L)) {
            authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
        } else {
            authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        }
        
        return authorities;
    }
    
    /**
     * 根据用户名查找用户
     * 
     * @param username 用户名
     * @return 用户对象，如果不存在则返回null
     */
    public SysUser findByUsername(String username) {
        return userRepository.findByUsername(username).orElse(null);
    }
    
    /**
     * 注册新用户
     * 对用户密码进行加密后保存到数据库
     * 
     * @param user 用户信息
     * @return 保存后的用户对象
     */
    public SysUser registerUser(SysUser user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }
}