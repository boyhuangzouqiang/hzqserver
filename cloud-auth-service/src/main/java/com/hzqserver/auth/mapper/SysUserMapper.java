package com.hzqserver.auth.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hzqserver.auth.entity.SysUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Optional;

/**
 * 用户数据访问接口
 */
@Mapper
public interface SysUserMapper extends BaseMapper<SysUser> {

    /**
     * 根据用户名查找用户
     */
    SysUser selectByUsername(@Param("username") String username);

    /**
     * 根据邮箱查找用户
     */
    SysUser selectByEmail(@Param("email") String email);

    /**
     * 根据手机号查找用户
     */
    SysUser selectByPhone(@Param("phone") String phone);
}