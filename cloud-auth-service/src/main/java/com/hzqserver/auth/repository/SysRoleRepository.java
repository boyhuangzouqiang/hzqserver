package com.hzqserver.auth.repository;

import com.hzqserver.auth.entity.SysRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 系统角色仓库接口
 * 提供对sys_role表的数据库操作方法
 */
@Repository
public interface SysRoleRepository extends JpaRepository<SysRole, Long> {
    /**
     * 根据角色编码查找角色
     * 
     * @param roleCode 角色编码
     * @return 角色对象的Optional包装
     */
    Optional<SysRole> findByRoleCode(String roleCode);
}