package com.hzqserver.auth.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hzqserver.auth.entity.SysRolePermission;
import org.apache.ibatis.annotations.Mapper;

/**
 * 角色权限关联数据访问接口
 */
@Mapper
public interface SysRolePermissionMapper extends BaseMapper<SysRolePermission> {
}