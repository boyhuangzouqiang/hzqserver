package com.hzqserver.auth.repository;

import com.hzqserver.auth.entity.SysUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 系统用户仓库接口
 * 提供对sys_user表的数据库操作方法
 */
@Repository
public interface SysUserRepository extends JpaRepository<SysUser, Long> {
    /**
     * 根据用户名查找用户
     * 
     * @param username 用户名
     * @return 用户对象的Optional包装
     */
    Optional<SysUser> findByUsername(String username);
}