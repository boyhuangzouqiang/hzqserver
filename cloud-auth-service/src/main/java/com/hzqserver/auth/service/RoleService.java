package com.hzqserver.auth.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hzqserver.auth.entity.SysRole;
import com.hzqserver.auth.mapper.SysRoleMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * 角色服务类
 * 提供角色管理的业务逻辑
 */
@Service
public class RoleService {
    
    @Autowired
    private SysRoleMapper roleMapper;
    
    /**
     * 获取所有角色
     * 
     * @return 角色列表
     */
    public List<SysRole> getAllRoles() {
        return roleMapper.selectList(null);
    }
    
    /**
     * 根据ID获取角色
     * 
     * @param id 角色ID
     * @return 角色对象
     */
    public Optional<SysRole> getRoleById(Long id) {
        SysRole role = roleMapper.selectById(id);
        return Optional.ofNullable(role);
    }
    
    /**
     * 创建新角色
     * 
     * @param role 角色对象
     * @return 创建后的角色对象
     */
    public SysRole createRole(SysRole role) {
        roleMapper.insert(role);
        return role;
    }
    
    /**
     * 更新角色
     * 
     * @param id 角色ID
     * @param role 角色对象
     * @return 更新后的角色对象
     */
    public SysRole updateRole(Long id, SysRole role) {
        SysRole existingRole = roleMapper.selectById(id);
        if (existingRole != null) {
            role.setId(id);
            roleMapper.updateById(role);
            return role;
        }
        return null;
    }
    
    /**
     * 删除角色
     *
     * @param id 角色ID
     * @return
     */
    public boolean deleteRole(Long id) {
        roleMapper.deleteById(id);
        return true;
    }
    
    /**
     * 根据角色编码获取角色
     * 
     * @param roleCode 角色编码
     * @return 角色对象
     */
    public Optional<SysRole> getRoleByCode(String roleCode) {
        QueryWrapper<SysRole> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("role_code", roleCode);
        SysRole role = roleMapper.selectOne(queryWrapper);
        return Optional.ofNullable(role);
    }
}