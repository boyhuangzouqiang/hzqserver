package com.hzqserver.auth.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hzqserver.auth.entity.SysPermission;
import com.hzqserver.auth.mapper.SysPermissionMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * 权限服务类
 * 提供权限相关的业务逻辑操作
 */
@Service
public class PermissionService {
    
    @Autowired
    private SysPermissionMapper permissionMapper;
    
    /**
     * 获取所有权限列表
     * 
     * @return 权限列表
     */
    public List<SysPermission> getAllPermissions() {
        return permissionMapper.selectList(null);
    }
    
    /**
     * 根据ID获取权限
     * 
     * @param id 权限ID
     * @return 权限对象
     */
    public Optional<SysPermission> getPermissionById(Long id) {
        SysPermission permission = permissionMapper.selectById(id);
        return Optional.ofNullable(permission);
    }
    
    /**
     * 创建新权限
     * 
     * @param permission 权限对象
     * @return 创建后的权限对象
     */
    public SysPermission createPermission(SysPermission permission) {
        permissionMapper.insert(permission);
        return permission;
    }
    
    /**
     * 更新权限
     * 
     * @param id 权限ID
     * @param permission 权限对象
     * @return 更新后的权限对象
     */
    public SysPermission updatePermission(Long id, SysPermission permission) {
        SysPermission existingPermission = permissionMapper.selectById(id);
        if (existingPermission != null) {
            permission.setId(id);
            permissionMapper.updateById(permission);
            return permission;
        }
        return null;
    }
    
    /**
     * 删除权限
     *
     * @param id 权限ID
     * @return
     */
    public boolean deletePermission(Long id) {
        permissionMapper.deleteById(id);
        return true;
    }
    
    /**
     * 根据角色ID获取权限列表
     * 
     * @param roleId 角色ID
     * @return 权限列表
     */
    public List<SysPermission> getPermissionsByRoleId(Long roleId) {
        // 由于角色权限关系在SysRolePermission中维护，这里需要通过关联查询
        // 可能需要额外的查询或自定义SQL来实现
        throw new UnsupportedOperationException("该方法需要根据具体业务需求实现");
    }
}