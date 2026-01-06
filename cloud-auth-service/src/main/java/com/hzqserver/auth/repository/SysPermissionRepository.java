package com.hzqserver.auth.repository;

import com.hzqserver.auth.entity.SysPermission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 系统权限仓库接口
 * 提供对sys_permission表的数据库操作方法
 */
@Repository
public interface SysPermissionRepository extends JpaRepository<SysPermission, Long> {
    /**
     * 根据角色编码查找权限列表
     * 
     * @param roleCode 角色编码
     * @return 权限列表
     */
    @Query("SELECT p FROM SysPermission p JOIN p.roles r WHERE r.roleCode = :roleCode")
    List<SysPermission> findByRoleCode(@Param("roleCode") String roleCode);
}