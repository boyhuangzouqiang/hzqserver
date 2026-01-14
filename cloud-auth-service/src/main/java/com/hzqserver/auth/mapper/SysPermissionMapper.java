package com.hzqserver.auth.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hzqserver.auth.entity.SysPermission;
import org.apache.ibatis.annotations.Mapper;

/**
 * 权限数据访问接口
 */
@Mapper
public interface SysPermissionMapper extends BaseMapper<SysPermission> {
}