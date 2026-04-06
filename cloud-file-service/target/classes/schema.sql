-- 创建数据库
CREATE DATABASE IF NOT EXISTS `hzq_file` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE `hzq_file`;

-- 文件上传记录表
DROP TABLE IF EXISTS `file_upload_record`;
CREATE TABLE `file_upload_record` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `file_md5` VARCHAR(64) NOT NULL COMMENT '文件MD5',
  `file_name` VARCHAR(255) NOT NULL COMMENT '原始文件名',
  `file_ext` VARCHAR(20) DEFAULT NULL COMMENT '文件扩展名',
  `file_size` BIGINT(20) NOT NULL COMMENT '文件大小（字节）',
  `content_type` VARCHAR(100) DEFAULT NULL COMMENT '内容类型（MIME Type）',
  `total_chunks` INT(11) DEFAULT NULL COMMENT '分片总数',
  `uploaded_chunks` INT(11) DEFAULT 0 COMMENT '已上传分片数',
  `chunk_size` BIGINT(20) DEFAULT NULL COMMENT '分片大小',
  `storage_path` VARCHAR(500) DEFAULT NULL COMMENT '存储路径（MinIO中的对象键）',
  `access_url` VARCHAR(500) DEFAULT NULL COMMENT '访问URL',
  `status` TINYINT(4) NOT NULL DEFAULT 0 COMMENT '上传状态：0-初始化，1-上传中，2-已完成，3-失败',
  `upload_user_id` BIGINT(20) DEFAULT NULL COMMENT '上传人ID',
  `upload_user_name` VARCHAR(100) DEFAULT NULL COMMENT '上传人名称',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` TINYINT(4) NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_file_md5` (`file_md5`),
  KEY `idx_status` (`status`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='文件上传记录表';

-- 分片上传临时记录表（用于跟踪每个分片的上传状态）
DROP TABLE IF EXISTS `file_chunk_record`;
CREATE TABLE `file_chunk_record` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `file_md5` VARCHAR(64) NOT NULL COMMENT '文件MD5',
  `chunk_index` INT(11) NOT NULL COMMENT '分片索引（从0开始）',
  `chunk_md5` VARCHAR(64) DEFAULT NULL COMMENT '分片MD5',
  `chunk_size` BIGINT(20) DEFAULT NULL COMMENT '分片大小',
  `storage_path` VARCHAR(500) DEFAULT NULL COMMENT '分片存储路径',
  `upload_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '上传时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_file_chunk` (`file_md5`, `chunk_index`),
  KEY `idx_file_md5` (`file_md5`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='分片上传临时记录表';
