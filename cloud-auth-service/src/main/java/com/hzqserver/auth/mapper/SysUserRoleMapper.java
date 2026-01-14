package com.hzqserver.auth.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hzqserver.auth.entity.SysUserRole;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户角色关联数据访问接口
 */
@Mapper
public interface SysUserRoleMapper extends BaseMapper<SysUserRole> {
}