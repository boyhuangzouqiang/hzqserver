-- 用户表
CREATE TABLE sys_user (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) UNIQUE NOT NULL COMMENT '用户名',
    password VARCHAR(255) NOT NULL COMMENT '密码',
    email VARCHAR(100) COMMENT '邮箱',
    phone VARCHAR(20) COMMENT '手机号',
    status TINYINT DEFAULT 1 COMMENT '状态: 0-禁用, 1-启用',
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_username (username)
) COMMENT='用户表';

-- 角色表
CREATE TABLE sys_role (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    role_name VARCHAR(50) UNIQUE NOT NULL COMMENT '角色名称',
    role_code VARCHAR(50) UNIQUE NOT NULL COMMENT '角色编码',
    role_desc VARCHAR(255) COMMENT '角色描述',
    status TINYINT DEFAULT 1 COMMENT '状态: 0-禁用, 1-启用',
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_role_code (role_code)
) COMMENT='角色表';

-- 用户角色关联表
CREATE TABLE sys_user_role (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL COMMENT '用户ID',
    role_id BIGINT NOT NULL COMMENT '角色ID',
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    FOREIGN KEY (user_id) REFERENCES sys_user(id),
    FOREIGN KEY (role_id) REFERENCES sys_role(id),
    UNIQUE KEY uk_user_role (user_id, role_id)
) COMMENT='用户角色关联表';

-- 权限表
CREATE TABLE sys_permission (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    permission_name VARCHAR(100) NOT NULL COMMENT '权限名称',
    permission_code VARCHAR(100) UNIQUE NOT NULL COMMENT '权限编码',
    permission_desc VARCHAR(255) COMMENT '权限描述',
    parent_id BIGINT DEFAULT 0 COMMENT '父权限ID',
    status TINYINT DEFAULT 1 COMMENT '状态: 0-禁用, 1-启用',
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_permission_code (permission_code)
) COMMENT='权限表';

-- 角色权限关联表
CREATE TABLE sys_role_permission (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    role_id BIGINT NOT NULL COMMENT '角色ID',
    permission_id BIGINT NOT NULL COMMENT '权限ID',
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    FOREIGN KEY (role_id) REFERENCES sys_role(id),
    FOREIGN KEY (permission_id) REFERENCES sys_permission(id),
    UNIQUE KEY uk_role_permission (role_id, permission_id)
) COMMENT='角色权限关联表';

-- 初始数据
INSERT INTO sys_user (username, password, email, phone) VALUES 
('admin', '$2a$10$NQq1bU5Rb1bU5Rb1bU5RbOQdZyYdZyYdZyYdZyYdZyYdZyYdZyYdZ', 'admin@example.com', '13800138000'),
('user', '$2a$10$NQq1bU5Rb1bU5Rb1bU5RbOQdZyYdZyYdZyYdZyYdZyYdZyYdZyYdZ', 'user@example.com', '13800138001');

INSERT INTO sys_role (role_name, role_code, role_desc) VALUES 
('管理员', 'ROLE_ADMIN', '系统管理员'),
('普通用户', 'ROLE_USER', '普通用户');

INSERT INTO sys_permission (permission_name, permission_code, permission_desc, parent_id) VALUES 
('用户管理', 'USER_MANAGE', '用户管理权限', 0),
('角色管理', 'ROLE_MANAGE', '角色管理权限', 0),
('权限管理', 'PERMISSION_MANAGE', '权限管理权限', 0);

INSERT INTO sys_user_role (user_id, role_id) VALUES (1, 1), (2, 2);

INSERT INTO sys_role_permission (role_id, permission_id) VALUES (1, 1), (1, 2), (1, 3);