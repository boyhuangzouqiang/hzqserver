package com.hzqserver.auth.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hzqserver.auth.entity.SysUser;
import com.hzqserver.auth.mapper.SysUserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * 用户服务类
 * 提供用户相关的业务逻辑操作
 */
@Service
public class UserService {
    
    @Autowired
    private SysUserMapper userMapper;
    
    /**
     * 获取所有用户
     * 
     * @return 用户列表
     */
    public List<SysUser> getAllUsers() {
        return userMapper.selectList(null);
    }
    
    /**
     * 根据ID获取用户
     * 
     * @param id 用户ID
     * @return 用户对象
     */
    public Optional<SysUser> getUserById(Long id) {
        SysUser user = userMapper.selectById(id);
        return Optional.ofNullable(user);
    }
    
    /**
     * 创建新用户
     * 
     * @param user 用户对象
     * @return 保存后的用户对象
     */
    public SysUser createUser(SysUser user) {
        // 检查用户名是否已存在
        QueryWrapper<SysUser> usernameQuery = new QueryWrapper<>();
        usernameQuery.eq("username", user.getUsername());
        if (userMapper.selectOne(usernameQuery) != null) {
            throw new RuntimeException("用户名已存在");
        }
        
        // 检查邮箱是否已存在（如果提供了邮箱）
        if (user.getEmail() != null && !user.getEmail().isEmpty()) {
            QueryWrapper<SysUser> emailQuery = new QueryWrapper<>();
            emailQuery.eq("email", user.getEmail());
            if (userMapper.selectOne(emailQuery) != null) {
                throw new RuntimeException("邮箱已存在");
            }
        }
        
        // 检查手机号是否已存在（如果提供了手机号）
        if (user.getPhone() != null && !user.getPhone().isEmpty()) {
            QueryWrapper<SysUser> phoneQuery = new QueryWrapper<>();
            phoneQuery.eq("phone", user.getPhone());
            if (userMapper.selectOne(phoneQuery) != null) {
                throw new RuntimeException("手机号已存在");
            }
        }
        
        // 设置默认状态为启用
        if (user.getStatus() == null) {
            user.setStatus(1);
        }
        
        // 保存用户
        userMapper.insert(user);
        return user;
    }
    
    /**
     * 更新用户
     * 
     * @param id 用户ID
     * @param user 用户对象
     * @return 更新后的用户对象
     */
    public SysUser updateUser(Long id, SysUser user) {
        SysUser existingUser = userMapper.selectById(id);
        if (existingUser != null) {
            user.setId(id);
            userMapper.updateById(user);
            return user;
        }
        return null;
    }
    
    /**
     * 删除用户
     *
     * @param id 用户ID
     * @return
     */
    public boolean deleteUser(Long id) {
        userMapper.deleteById(id);
        return true;
    }
    
    /**
     * 注册新用户
     * 
     * @param user 用户信息
     * @return 注册后的用户对象
     */
    public SysUser registerUser(SysUser user) {
        // 检查用户名是否已存在
        QueryWrapper<SysUser> usernameQuery = new QueryWrapper<>();
        usernameQuery.eq("username", user.getUsername());
        if (userMapper.selectOne(usernameQuery) != null) {
            throw new RuntimeException("用户名已存在");
        }
        
        // 保存用户
        userMapper.insert(user);
        return user;
    }

    /**
     * 根据用户名查找用户
     *
     * @param username 用户名
     * @return 用户对象
     */
    public Optional<SysUser> getUserByUsername(String username) {
        QueryWrapper<SysUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", username);
        SysUser user = userMapper.selectOne(queryWrapper);
        return Optional.ofNullable(user);
    }
}