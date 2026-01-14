package com.hzqserver.auth.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hzqserver.auth.entity.SysRole;
import org.apache.ibatis.annotations.Mapper;

/**
 * 角色数据访问接口
 */
@Mapper
public interface SysRoleMapper extends BaseMapper<SysRole> {
}