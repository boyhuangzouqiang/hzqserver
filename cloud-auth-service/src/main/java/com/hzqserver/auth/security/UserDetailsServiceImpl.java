package com.hzqserver.auth.security;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hzqserver.auth.entity.SysUser;
import com.hzqserver.auth.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * 用户详情服务实现类
 * 实现Spring Security的UserDetailsService接口，用于加载用户认证和授权信息
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserService userService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 通过UserService查询用户信息
        Optional<SysUser> userOpt = userService.getUserByUsername(username);
        
        if (!userOpt.isPresent()) {
            throw new UsernameNotFoundException("用户不存在: " + username);
        }
        
        SysUser sysUser = userOpt.get();

        // 构建用户权限列表（这里可以根据实际业务从数据库中获取用户的角色/权限）
        List<GrantedAuthority> authorities = new ArrayList<>();
        // 实际应用中应该从数据库获取用户角色
        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));

        // 创建Spring Security的UserDetails对象
        return new User(
                sysUser.getUsername(),
                sysUser.getPassword(),
                true, // enabled
                true, // accountNonExpired
                true, // credentialsNonExpired
                true, // accountNonLocked
                authorities
        );
    }
}