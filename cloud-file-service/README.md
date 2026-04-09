# File Service - 文件服务模块

## 📋 模块概述

文件服务模块提供文件上传、下载、断点续传等功能，支持 MinIO 和阿里云 OSS 两种存储方式。

### 核心特性

- ✅ 大文件分片上传（支持 1GB+ 文件）
- ✅ 断点续传（上传中断后可继续）
- ✅ 秒传功能（相同文件自动跳过）
- ✅ 分片合并（自动合并所有分片）
- ✅ 文件下载（支持超时配置）
- ✅ **断点续传下载**（HTTP Range 请求，多线程并行，速度限制，自动重试）

---

## 🏗️ 技术架构

### 后端技术栈

- **框架**: Spring Boot 3.x
- **存储**: MinIO / 阿里云 OSS（可切换）
- **数据库**: MySQL + MyBatis-Plus
- **工具**: Hutool、Lombok

### 前端技术栈

- **框架**: Vue 3 + Element Plus
- **MD5 计算**: spark-md5
- **分片上传**: 原生 File API

---

## 📁 目录结构

```
cloud-file-service/
├── src/main/java/com/hzqserver/file/
│   ├── config/                  # 配置类
│   │   ├── CorsConfig.java      # 跨域配置
│   │   ├── FileUploadConfig.java # 文件上传配置
│   │   ├── MinioConfig.java     # MinIO 配置
│   │   ├── OssConfig.java       # OSS 配置
│   │   └── StorageProperties.java # 存储配置
│   ├── controller/
│   │   └── FileUploadController.java  # 文件上传控制器
│   ├── entity/
│   │   ├── ChunkUploadRequest.java    # 分片上传请求
│   │   ├── FileChunkRecord.java       # 分片记录实体
│   │   ├── FileUploadRecord.java      # 上传记录实体
│   │   └── FileUploadResponse.java    # 上传响应
│   ├── enums/
│   │   └── StorageType.java           # 存储类型枚举
│   ├── factory/
│   │   └── StorageFactory.java        # 存储服务工厂
│   ├── mapper/
│   │   ├── FileChunkRecordMapper.java # 分片记录 Mapper
│   │   └── FileUploadRecordMapper.java # 上传记录 Mapper
│   ├── service/
│   │   ├── FileUploadService.java     # 文件上传服务接口
│   │   ├── impl/
│   │   │   └── FileUploadServiceImpl.java # 文件上传服务实现
│   │   └── storage/
│   │       ├── StorageService.java    # 存储服务接口
│   │       └── impl/
│   │           ├── MinioStorageServiceImpl.java # MinIO 实现
│   │           └── OssStorageServiceImpl.java   # OSS 实现
│   └── FileServiceApplication.java    # 启动类
├── src/main/resources/
│   ├── mapper/
│   │   ├── FileChunkRecordMapper.xml  # 分片记录 SQL
│   │   └── FileUploadRecordMapper.xml # 上传记录 SQL
│   ├── static/
│   │   └── upload.html               # 前端上传页面
│   ├── application.yml                # 应用配置
│   ├── bootstrap.yml                  # 启动配置
│   └── schema.sql                     # 数据库脚本
└── pom.xml                            # Maven 依赖
```

---

## 🔌 API 接口文档

### 基础信息

- **Base URL**: `http://localhost:8084/api/file`
- **Content-Type**: `application/json` 或 `multipart/form-data`

---

### 1. 初始化文件上传

**接口**: `POST /upload/init`

**请求体**:
```json
{
  "fileMd5": "80681fabe090cbc3db3734802625d51e",
  "fileName": "test.mp4",
  "fileSize": 104857600,
  "contentType": "video/mp4",
  "totalChunks": 20,
  "chunkSize": 5242880
}
```

**响应**:
```json
{
  "code": 100,
  "message": "初始化成功",
  "data": {
    "fileId": 1,
    "fileMd5": "80681fabe090cbc3db3734802625d51e",
    "fileName": "test.mp4",
    "fileSize": 104857600,
    "status": 0,
    "uploadedChunks": [],
    "message": "初始化成功"
  }
}
```

**特殊场景**:

1. **秒传**（文件已存在）:
```json
{
  "code": 100,
  "message": "操作成功",
  "data": {
    "fileId": 1,
    "status": 2,
    "accessUrl": "http://localhost:9000/file-upload/files/xxx/test.mp4",
    "message": "文件已存在"
  }
}
```

2. **断点续传**（有未完成记录）:
```json
{
  "code": 100,
  "message": "操作成功",
  "data": {
    "fileId": 1,
    "status": 1,
    "uploadedChunks": [0, 1, 2, ..., 69],
    "message": "检测到未完成的上传，已上传 70/100 个分片"
  }
}
```

---

### 2. 上传分片

**接口**: `POST /upload/chunk`

**请求方式**: `multipart/form-data`

**参数**:
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| file | File | 是 | 分片文件 |
| fileMd5 | String | 是 | 文件 MD5 |
| fileName | String | 是 | 文件名 |
| fileSize | Long | 是 | 文件总大小 |
| chunkIndex | Integer | 是 | 分片索引（从 0 开始） |
| totalChunks | Integer | 是 | 分片总数 |
| chunkSize | Long | 是 | 分片大小 |
| chunkMd5 | String | 否 | 分片 MD5（用于校验） |

**响应**:
```json
{
  "code": 100,
  "message": "分片 71/100 上传成功",
  "data": {
    "fileMd5": "80681fabe090cbc3db3734802625d51e",
    "fileName": "test.mp4",
    "fileSize": 104857600,
    "status": 1
  }
}
```

---

### 3. 合并分片

**接口**: `POST /upload/merge`

**参数**:
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| fileMd5 | String | 是 | 文件 MD5 |

**响应**:
```json
{
  "code": 100,
  "message": "文件上传完成",
  "data": {
    "fileId": 1,
    "fileMd5": "80681fabe090cbc3db3734802625d51e",
    "fileName": "test.mp4",
    "fileSize": 104857600,
    "accessUrl": "http://localhost:9000/file-upload/files/xxx/test.mp4",
    "status": 2
  }
}
```

---

### 4. 检查文件上传状态

**接口**: `GET /upload/status`

**参数**:
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| fileMd5 | String | 是 | 文件 MD5 |

**响应**:
```json
{
  "code": 100,
  "message": "已上传 70/100 个分片",
  "data": {
    "fileId": 1,
    "fileMd5": "80681fabe090cbc3db3734802625d51e",
    "fileName": "test.mp4",
    "fileSize": 104857600,
    "status": 1,
    "uploadedChunks": [0, 1, 2, ..., 69]
  }
}
```

---

### 5. 取消上传

**接口**: `DELETE /upload/cancel`

**参数**:
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| fileMd5 | String | 是 | 文件 MD5 |

**响应**:
```json
{
  "code": 100,
  "message": "操作成功"
}
```

---

### 6. 下载文件

**接口**: `GET /download/{fileMd5}`

**参数**:
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| fileMd5 | String | 是 | 文件 MD5（路径参数） |

**响应**: 文件流（二进制数据）

**配置说明**:
```yaml
server:
  tomcat:
    connection-timeout: 3600000  # 1 小时超时，支持大文件下载
```

---

### 7. 获取上传历史

**接口**: `GET /upload/history`

**参数**:
| 参数名 | 类型 | 必填 | 默认值 | 说明 |
|--------|------|------|--------|------|
| page | Integer | 否 | 1 | 页码 |
| pageSize | Integer | 否 | 20 | 每页条数 |

**响应**:
```json
{
  "code": 100,
  "data": {
    "list": [
      {
        "id": 1,
        "fileMd5": "80681fabe090cbc3db3734802625d51e",
        "fileName": "test.mp4",
        "fileSize": 104857600,
        "contentType": "video/mp4",
        "accessUrl": "http://localhost:9000/file-upload/files/xxx/test.mp4",
        "status": 2,
        "createTime": "2026-04-06T22:00:00"
      }
    ],
    "total": 100,
    "page": 1,
    "pageSize": 20
  }
}
```

---

### 8. 获取上传配置

**接口**: `GET /upload/config`

**响应**:
```json
{
  "code": 100,
  "data": {
    "recommendedChunkSize": 5242880,  // 5MB
    "maxChunkSize": 10485760,         // 10MB
    "minChunkSize": 1048576,          // 1MB
    "maxFileSize": 1073741824         // 1GB
  }
}
```

---

## 💾 数据库设计

### 1. 文件上传记录表（file_upload_record）

```sql
CREATE TABLE `file_upload_record` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `file_md5` VARCHAR(64) NOT NULL COMMENT '文件MD5',
  `file_name` VARCHAR(255) NOT NULL COMMENT '文件名',
  `file_ext` VARCHAR(50) COMMENT '文件扩展名',
  `file_size` BIGINT(20) NOT NULL COMMENT '文件大小',
  `content_type` VARCHAR(100) COMMENT 'MIME类型',
  `total_chunks` INT(11) COMMENT '分片总数',
  `uploaded_chunks` INT(11) COMMENT '已上传分片数',
  `chunk_size` BIGINT(20) COMMENT '分片大小',
  `storage_path` VARCHAR(500) COMMENT '存储路径',
  `access_url` VARCHAR(500) COMMENT '访问URL',
  `status` TINYINT(4) NOT NULL DEFAULT 0 COMMENT '状态：0-初始化，1-上传中，2-已完成，3-失败',
  `upload_user_id` BIGINT(20) COMMENT '上传人ID',
  `upload_user_name` VARCHAR(50) COMMENT '上传人姓名',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted` TINYINT(4) NOT NULL DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_file_md5` (`file_md5`),
  KEY `idx_status` (`status`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='文件上传记录表';
```

### 2. 分片上传记录表（file_chunk_record）

```sql
CREATE TABLE `file_chunk_record` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `file_md5` VARCHAR(64) NOT NULL COMMENT '文件MD5',
  `chunk_index` INT(11) NOT NULL COMMENT '分片索引（从0开始）',
  `chunk_md5` VARCHAR(64) COMMENT '分片MD5',
  `chunk_size` BIGINT(20) COMMENT '分片大小',
  `storage_path` VARCHAR(500) COMMENT '分片存储路径',
  `upload_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_file_chunk` (`file_md5`, `chunk_index`),
  KEY `idx_file_md5` (`file_md5`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='分片上传记录表';
```

---

## ⚙️ 配置说明

### application.yml 核心配置

```yaml
server:
  port: 8084
  # Tomcat 超时配置（大文件下载必需）
  tomcat:
    connection-timeout: 3600000  # 1 小时（3600 秒）
    max-connections: 10000       # 最大连接数

spring:
  servlet:
    multipart:
      max-file-size: 100MB      # 单个分片最大 100MB
      max-request-size: 100MB   # 请求最大 100MB

file:
  storage:
    type: minio                  # 存储类型：minio 或 oss
    default-bucket: file-upload  # 默认存储桶
  
  upload:
    chunk-size: 5242880          # 推荐分片大小：5MB
    max-file-size: 1073741824    # 最大文件大小：1GB
    allowed-types: jpg,jpeg,png,gif,pdf,doc,docx,xls,xlsx,zip,rar,mp4

minio:
  endpoint: http://localhost:9000
  access-key: minioadmin
  secret-key: minioadmin
  bucket-name: file-upload
```

---

## 🔄 断点续传流程

### 上传流程

```
1. 前端计算文件 MD5
   ↓
2. 调用 initUpload 接口
   ├─ 文件已完成 → 返回秒传结果
   ├─ 有未完成记录 → 返回已上传分片列表
   └─ 首次上传 → 创建上传记录
   ↓
3. 循环上传分片
   ├─ 跳过已上传的分片（断点续传）
   └─ 上传未完成的分片
   ↓
4. 所有分片上传完成 → 调用 mergeChunks 合并
   ↓
5. 清理分片文件（节省存储空间）
```

### 核心代码逻辑

**后端**（`FileUploadServiceImpl.java`）:

```java
// 初始化上传时，检查是否有未完成的记录
FileUploadRecord existingRecord = fileUploadRecordMapper.selectByFileMd5(fileMd5);
if (existingRecord != null && existingRecord.getStatus() != 2) {
    // 查询已上传的分片
    List<FileChunkRecord> chunkRecords = fileChunkRecordMapper.selectByFileMd5(fileMd5);
    List<Integer> uploadedChunks = chunkRecords.stream()
            .map(FileChunkRecord::getChunkIndex)
            .collect(Collectors.toList());
    
    // 返回已上传的分片列表给前端
    return FileUploadResponse.builder()
            .uploadedChunks(uploadedChunks)  // [0, 1, 2, ..., 69]
            .build();
}
```

**前端**（`upload.html`）:

```javascript
// 上传分片时，跳过已上传的
for (let i = 0; i < file.totalChunks; i++) {
    if (file.uploadedChunks.includes(i)) {
        continue;  // 跳过已上传的分片
    }
    await uploadChunk(index, i);
}
```

---

## 🚀 启动与部署

### 1. 启动前准备

- ✅ MySQL 数据库（创建 `hzqserver` 数据库并执行 `schema.sql`）
- ✅ MinIO 服务（或使用阿里云 OSS）
- ✅ Nacos 服务注册中心

### 2. 启动服务

```bash
cd cloud-file-service
mvn clean package
java -jar target/cloud-file-service-1.0.0.jar
```

### 3. 访问测试页面

浏览器访问：`http://localhost:8084/upload.html`

---

## 🧪 功能测试指南

### 测试断点续传下载

#### 1. 准备测试文件

上传一个大文件（建议 > 50MB）：
- 视频文件（MP4、AVI）
- 压缩文件（ZIP、RAR）
- 镜像文件（ISO）

#### 2. 测试正常下载

1. 打开浏览器开发者工具（F12）
2. 切换到 **Network** 标签
3. 点击“📥 下载文件”按钮
4. 观察请求头中的 `Range` 字段
5. 查看响应状态码是否为 `206 Partial Content`
6. 查看响应头中的 `Content-Range` 字段

#### 3. 测试断点续传

**方法一：刷新页面**
1. 开始下载文件
2. 在下载过程中（进度约 30%-50%）刷新页面
3. 再次点击下载按钮
4. 观察是否从上次位置继续下载
5. 检查 Network 面板，应该看到 `Range: bytes=xxx-` 请求头

**方法二：关闭浏览器**
1. 开始下载文件
2. 在下载过程中关闭浏览器标签页
3. 重新打开页面
4. 点击下载按钮
5. 系统应提示“恢复下载，已从 xxx MB 继续”

**方法三：模拟网络中断**
1. 打开开发者工具 → Network 标签
2. 点击 “Offline” 或 “No throttling” 下拉菜单
3. 选择 “Offline” 模拟断网
4. 等待几秒后恢复网络
5. 系统应自动重试并继续下载

#### 4. 验证多线程下载

1. 打开开发者工具 → Network 标签
2. 开始下载大文件
3. 观察同时发出的多个 Range 请求
4. 应该能看到 3 个并发的请求（可配置）

#### 5. 验证速度限制

1. 打开开发者工具 → Network 标签
2. 查看下载速度
3. 应该在配置的限速范围内（默认 10MB/s）
4. 可通过修改 `DOWNLOAD_SPEED_LIMIT` 常量调整

#### 6. 验证自动重试

1. 开始下载文件
2. 在开发者工具中右键请求 → Block request URL
3. 观察控制台日志，应该看到重试信息
4. 取消阻塞后，下载应自动恢复

### 预期结果

✅ **成功标志**:
- 下载可以暂停和恢复
- 刷新页面后仍能继续下载
- 网络中断后自动重试
- 下载完成后文件完整可用
- Network 面板显示 206 状态码
- 请求头包含 `Range: bytes=xxx-`
- 响应头包含 `Content-Range: bytes xxx-yyy/zzz`

❌ **失败标志**:
- 每次下载都从头开始
- 刷新页面后进度丢失
- 网络错误后无法恢复
- 下载的文件损坏或不完整

---

## ✅ 已实现功能：断点续传下载

### 功能描述

下载功能已完整支持**断点续传**，即使下载中断也可以从上次位置继续，无需重新下载整个文件。基于 **HTTP Range 请求**实现，支持：

- ✅ 暂停/恢复下载
- ✅ 只下载未完成的部分
- ✅ 支持大文件下载（避免超时）
- ✅ 多线程并行下载（类似迅雷）
- ✅ 下载速度限制
- ✅ 网络错误自动重试
- ✅ IndexedDB 存储进度（支持超大文件）

### 技术实现

#### 1. HTTP Range 请求支持

浏览器会在下载时发送 `Range` 请求头：

```http
GET /api/file/download/80681fabe090cbc3db3734802625d51e HTTP/1.1
Range: bytes=1048576-
```

表示请求从第 1MB 开始到文件末尾的数据。

服务端响应：

```http
HTTP/1.1 206 Partial Content
Accept-Ranges: bytes
Content-Range: bytes 1048576-10485759/10485760
Content-Length: 9437184
Content-Type: application/octet-stream
```

#### 2. 后端实现

**StorageService 接口扩展**:

```java
/**
 * 下载文件指定范围的数据（支持断点续传）
 */
InputStream downloadFileRange(String bucketName, String objectName, 
                              long startByte, long endByte);
```

**MinIO 实现** ([MinioStorageServiceImpl.java](file:///D:/5_java_project/hzqserver/cloud-file-service/src/main/java/com/hzqserver/file/service/storage/impl/MinioStorageServiceImpl.java)):

```java
@Override
public InputStream downloadFileRange(String bucketName, String objectName, 
                                     long startByte, long endByte) {
    try {
        // MinIO 支持通过 offset 和 length 参数实现 Range 请求
        long length = endByte - startByte + 1;
        return minioClient.getObject(
                GetObjectArgs.builder()
                        .bucket(bucketName)
                        .object(objectName)
                        .offset(startByte)
                        .length(length)
                        .build()
        );
    } catch (Exception e) {
        throw new RuntimeException("MinIO范围下载文件失败: " + e.getMessage(), e);
    }
}
```

**阿里云 OSS 实现** (OssStorageServiceImpl.java):

```java
@Override
public InputStream downloadFileRange(String bucketName, String objectName, 
                                     long startByte, long endByte) {
    OSS ossClient = createOssClient();
    try {
        // OSS 支持 Range 请求
        GetObjectRequest request = new GetObjectRequest(bucketName, objectName);
        request.setRange(startByte, endByte);
        
        OSSObject ossObject = ossClient.getObject(request);
        return ossObject.getObjectContent();
    } catch (Exception e) {
        throw new RuntimeException("OSS范围下载文件失败: " + e.getMessage(), e);
    }
}
```

**Controller 层处理** ([FileUploadController.java](file:///D:/5_java_project/hzqserver/cloud-file-service/src/main/java/com/hzqserver/file/controller/FileUploadController.java)):

```java
@GetMapping("/download/{fileMd5}")
public void downloadFile(@PathVariable("fileMd5") String fileMd5, 
                        HttpServletRequest request,
                        HttpServletResponse response) {
    // 1. 查询文件信息
    FileUploadRecord record = fileUploadService.getFileRecordByMd5(fileMd5);
    
    // 2. 获取 Range 请求头
    String rangeHeader = request.getHeader("Range");
    long startByte = 0;
    long endByte = record.getFileSize() - 1;
    long contentLength = record.getFileSize();
    
    // 3. 解析 Range 请求
    if (rangeHeader != null && rangeHeader.startsWith("bytes=")) {
        String range = rangeHeader.substring(6);
        String[] ranges = range.split("-");
        startByte = Long.parseLong(ranges[0]);
        if (ranges.length > 1 && !ranges[1].isEmpty()) {
            endByte = Long.parseLong(ranges[1]);
        }
        contentLength = endByte - startByte + 1;
        
        // 返回 206 Partial Content
        response.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT);
        response.setHeader("Content-Range", 
            String.format("bytes %d-%d/%d", startByte, endByte, record.getFileSize()));
    } else {
        response.setStatus(HttpServletResponse.SC_OK);
    }
    
    // 4. 设置响应头
    response.setContentType(record.getContentType());
    response.setHeader("Accept-Ranges", "bytes");  // 支持 Range 请求
    response.setHeader("Content-Length", String.valueOf(contentLength));
    
    // 5. 从对象存储下载指定范围的数据
    StorageService storageService = storageFactory.getStorageService();
    try (InputStream inputStream = storageService.downloadFileRange(
                bucketName, record.getStoragePath(), startByte, endByte);
         OutputStream outputStream = response.getOutputStream()) {
        
        byte[] buffer = new byte[8192];
        int bytesRead;
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, bytesRead);
        }
    }
}
```

#### 3. 前端实现

前端使用 Fetch API 实现以下高级特性：

**核心特性**:

1. **多线程并行下载**: 将文件分成多个分片并发下载（默认 3 个线程）
2. **速度限制**: 控制下载速度，避免占用过多带宽（默认 10MB/s）
3. **自动重试**: 网络错误时自动重试（最多 3 次，指数退避）
4. **IndexedDB 存储**: 使用 IndexedDB 存储分片数据，支持超大文件
5. **进度保存**: 实时保存下载进度，刷新页面后可恢复

**实现代码** ([upload.html](file:///D:/5_java_project/hzqserver/cloud-file-service/src/main/resources/static/upload.html)):

```javascript
// 配置参数
const DOWNLOAD_CONCURRENCY = 3; // 并发下载线程数
const DOWNLOAD_CHUNK_SIZE = 2 * 1024 * 1024; // 下载分片大小: 2MB
const DOWNLOAD_SPEED_LIMIT = 10 * 1024 * 1024; // 下载速度限制: 10MB/s
const MAX_RETRY_COUNT = 3; // 最大重试次数

// 使用 IndexedDB 存储下载进度（支持大文件）
const initIndexedDB = () => {
    return new Promise((resolve, reject) => {
        const request = indexedDB.open('FileDownloadDB', 1);
        request.onerror = () => reject(request.error);
        request.onsuccess = () => resolve(request.result);
        request.onupgradeneeded = (event) => {
            const db = event.target.result;
            if (!db.objectStoreNames.contains('downloads')) {
                db.createObjectStore('downloads', { keyPath: 'fileMd5' });
            }
        };
    });
};

// 带速度限制的下载函数（含自动重试）
const downloadWithSpeedLimit = async (url, startByte, endByte, retryCount = 0) => {
    try {
        const response = await fetch(url, {
            headers: { 'Range': `bytes=${startByte}-${endByte}` }
        });
        
        if (!response.ok) {
            throw new Error(`HTTP ${response.status}: ${response.statusText}`);
        }
        
        const reader = response.body.getReader();
        const chunks = [];
        let receivedBytes = 0;
        const startTime = Date.now();
        
        while (true) {
            const { done, value } = await reader.read();
            if (done) break;
            
            chunks.push(value);
            receivedBytes += value.length;
            
            // 速度限制：计算当前速度，如果超过限制则等待
            const elapsed = (Date.now() - startTime) / 1000;
            const currentSpeed = receivedBytes / elapsed;
            
            if (currentSpeed > DOWNLOAD_SPEED_LIMIT) {
                const expectedTime = receivedBytes / DOWNLOAD_SPEED_LIMIT;
                const waitTime = (expectedTime - elapsed) * 1000;
                if (waitTime > 0) {
                    await new Promise(resolve => setTimeout(resolve, waitTime));
                }
            }
        }
        
        const blob = new Blob(chunks);
        return await blob.arrayBuffer();
        
    } catch (error) {
        // 自动重试机制（指数退避）
        if (retryCount < MAX_RETRY_COUNT) {
            console.warn(`下载失败，重试 ${retryCount + 1}/${MAX_RETRY_COUNT}`);
            await new Promise(resolve => setTimeout(resolve, 1000 * (retryCount + 1)));
            return downloadWithSpeedLimit(url, startByte, endByte, retryCount + 1);
        }
        throw error;
    }
};

// 多线程并发下载
const downloadWithConcurrency = async (file, downloadedChunks, totalChunks, progressMsg) => {
    const baseUrl = `${API_BASE_URL}/download/${file.fileMd5}`;
    
    // 创建下载任务队列
    const tasks = [];
    for (let i = 0; i < totalChunks; i++) {
        if (!downloadedChunks[i]) {
            const startByte = i * DOWNLOAD_CHUNK_SIZE;
            const endByte = Math.min(startByte + DOWNLOAD_CHUNK_SIZE - 1, file.size - 1);
            tasks.push({ index: i, startByte, endByte });
        }
    }
    
    // 并发执行下载任务
    let completedTasks = 0;
    const totalTasks = tasks.length;
    
    const worker = async () => {
        while (tasks.length > 0) {
            const task = tasks.shift();
            if (!task) break;
            
            const chunkData = await downloadWithSpeedLimit(
                baseUrl, task.startByte, task.endByte
            );
            
            // 保存分片数据到 IndexedDB
            await saveDownloadProgress(
                file.fileMd5, task.index, chunkData, chunkData.byteLength
            );
            
            completedTasks++;
            
            // 更新进度
            const totalCompleted = Object.keys(downloadedChunks).length + completedTasks;
            const progress = ((totalCompleted / totalChunks) * 100).toFixed(2);
            progressMsg.message = `${file.name}<br/>进度: ${progress}%`;
        }
    };
    
    // 启动多个工作线程
    const workers = Array(Math.min(DOWNLOAD_CONCURRENCY, tasks.length))
        .fill(null)
        .map(() => worker());
    
    await Promise.all(workers);
};

// 合并分片并触发下载
const mergeAndDownloadChunks = async (file, chunks, progressMsg) => {
    // 按索引排序分片
    const sortedIndexes = Object.keys(chunks).map(Number).sort((a, b) => a - b);
    
    // 合并所有分片
    const blobs = sortedIndexes.map(index => new Blob([chunks[index].data]));
    const finalBlob = new Blob(blobs, { type: file.file?.type || 'application/octet-stream' });
    
    const url = URL.createObjectURL(finalBlob);
    
    // 触发下载
    const a = document.createElement('a');
    a.href = url;
    a.download = file.name;
    document.body.appendChild(a);
    a.click();
    document.body.removeChild(a);
    
    // 清理
    await clearDownloadProgress(file.fileMd5);
    URL.revokeObjectURL(url);
    ElementPlus.ElMessage.success('下载完成！');
};
```

### 性能优势

| 特性 | 传统下载 | 优化后下载 |
|------|---------|----------|
| 断点续传 | ❌ 不支持 | ✅ 支持 |
| 多线程 | ❌ 单线程 | ✅ 3 线程并发 |
| 速度限制 | ❌ 无限制 | ✅ 可配置 |
| 自动重试 | ❌ 需手动 | ✅ 最多 3 次 |
| 大文件支持 | ⚠️ localStorage 限制 | ✅ IndexedDB |
| 内存占用 | ⚠️ 全部加载 | ✅ 分片处理 |

### 参考资源

- HTTP Range 请求规范: https://developer.mozilla.org/zh-CN/docs/Web/HTTP/Range_requests
- MinIO Range 下载: https://min.io/docs/minio/linux/developers/java/API.html#getobject
- OSS Range 下载: https://help.aliyun.com/zh/oss/developer-reference/copy-objects
- IndexedDB API: https://developer.mozilla.org/zh-CN/docs/Web/API/IndexedDB_API

---

## 📊 性能优化建议

### 1. 分片大小配置

| 网络环境 | 推荐分片大小 | 说明 |
|---------|-------------|------|
| 局域网 | 10-20 MB | 带宽充足，大分片减少请求次数 |
| 家庭宽带 | 5-10 MB | 平衡稳定性和速度 |
| 移动网络 | 2-5 MB | 网络不稳定，小分片提高成功率 |

### 2. 并发上传/下载

前端可以支持多个分片并发处理：

**并发上传**:
```javascript
// 并发上传 3 个分片
const concurrency = 3;
for (let i = 0; i < file.totalChunks; i += concurrency) {
    const chunkIndexes = Array.from(
        { length: concurrency }, 
        (_, j) => i + j
    ).filter(idx => idx < file.totalChunks);
    
    await Promise.all(
        chunkIndexes.map(idx => uploadChunk(index, idx))
    );
}
```

**并发下载**（已实现）:
```javascript
const DOWNLOAD_CONCURRENCY = 3; // 默认 3 个线程
// 系统会自动将文件分成多个分片并发下载
```

### 3. 下载速度限制（已实现）

避免下载占用过多带宽，影响其他网络操作：

```javascript
const DOWNLOAD_SPEED_LIMIT = 10 * 1024 * 1024; // 10MB/s

// 在 downloadWithSpeedLimit 函数中自动应用
// 根据实际下载速度动态调整等待时间
```

### 4. 自动重试机制（已实现）

网络波动时自动重试，提高下载成功率：

```javascript
const MAX_RETRY_COUNT = 3; // 最多重试 3 次

// 使用指数退避策略：
// 第 1 次重试：等待 1 秒
// 第 2 次重试：等待 2 秒
// 第 3 次重试：等待 3 秒
await new Promise(resolve => setTimeout(resolve, 1000 * (retryCount + 1)));
```

### 5. IndexedDB 存储（已实现）

对于超大文件（>100MB），使用 IndexedDB 替代 localStorage：

**优势**:
- ✅ 存储空间更大（通常 50MB - 几GB）
- ✅ 支持二进制数据存储
- ✅ 异步操作，不阻塞主线程
- ✅ 结构化查询能力

**降级策略**:
```javascript
try {
    // 优先使用 IndexedDB
    const db = await initIndexedDB();
    // ... 保存数据
} catch (error) {
    // 降级到 localStorage（仅保存元数据）
    console.warn('IndexedDB 失败，降级到 localStorage');
    localStorage.setItem(`download_meta_${fileMd5}`, JSON.stringify(metadata));
}
```

### 6. 对象存储优化

- **MinIO**: 启用纠删码模式，提高数据可靠性
- **OSS**: 配置 CDN 加速下载
- **通用**: 定期清理未完成的分片文件（定时任务）

### 7. Tomcat 超时配置

大文件下载需要增加超时时间：

```yaml
server:
  tomcat:
    connection-timeout: 3600000  # 1 小时（3600 秒）
    max-connections: 10000       # 最大连接数
```

---

## 🐛 常见问题

### 1. 分片上传失败怎么办？

- 检查 MinIO/OSS 是否正常运行
- 检查数据库连接
- 查看日志：`tail -f logs/file-service.log`

### 2. 下载超时怎么办？

已配置 Tomcat 超时为 1 小时，如果还超时：

- 检查网络速度
- 考虑实现断点续传下载（见上文）
- 增大 `connection-timeout` 配置

### 3. 文件合并失败怎么办？

- 检查是否所有分片都已上传
- 查看 `file_chunk_record` 表是否有缺失的分片
- 检查 MinIO/OSS 分片文件是否完整

### 4. 如何清理未完成的上传？

可以添加定时任务清理超过 7 天未完成的上传：

```java
@Scheduled(cron = "0 0 2 * * ?")  // 每天凌晨 2 点
public void cleanIncompleteUploads() {
    // 删除超过 7 天的未完成上传记录
    // 删除对应的分片文件
}
```

---

## 🔒 漏洞问题排查

### 1. 文件上传安全漏洞

#### 1.1 文件类型绕过攻击

**风险描述**: 攻击者上传恶意文件（如 `.jsp`、`.php` 脚本），导致远程代码执行（RCE）。

**排查步骤**:

1. **检查文件类型校验**:
```java
// ❌ 不安全：仅检查扩展名
if (fileName.endsWith(".jpg")) {
    // 允许上传
}

// ✅ 安全：检查文件魔数（Magic Number）
public boolean isValidImage(MultipartFile file) {
    try {
        byte[] magic = new byte[4];
        file.getInputStream().read(magic);
        // JPEG: FF D8 FF
        // PNG: 89 50 4E 47
        // GIF: 47 49 46 38
        return magic[0] == (byte) 0xFF && magic[1] == (byte) 0xD8;
    } catch (IOException e) {
        return false;
    }
}
```

2. **白名单校验**:
```java
// 允许的文件类型
private static final Set<String> ALLOWED_EXTENSIONS = Set.of(
    "jpg", "jpeg", "png", "gif", "pdf", "doc", "docx"
);

private String getFileExtension(String fileName) {
    String ext = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
    if (!ALLOWED_EXTENSIONS.contains(ext)) {
        throw new RuntimeException("不允许的文件类型: " + ext);
    }
    return ext;
}
```

3. **MIME Type 校验**:
```java
String contentType = file.getContentType();
if (!contentType.startsWith("image/")) {
    throw new RuntimeException("不允许的文件类型");
}
```

#### 1.2 文件名路径遍历攻击

**风险描述**: 文件名包含 `../` 导致文件被写入到非预期目录。

**修复方案**:
```java
// ❌ 不安全：直接使用原始文件名
String path = uploadDir + fileName;

// ✅ 安全：清理文件名，只保留基本字符
String safeFileName = fileName.replaceAll("[^a-zA-Z0-9._-]", "_");
String path = uploadDir + File.separator + safeFileName;

// ✅ 更安全：使用 MD5 重命名文件
String safeFileName = DigestUtil.md5Hex(fileMd5) + "." + getFileExtension(fileName);
```

#### 1.3 超大文件拒绝服务（DoS）

**风险描述**: 上传超大文件导致服务器磁盘空间耗尽或内存溢出。

**排查与修复**:

1. **配置文件大小限制**:
```yaml
spring:
  servlet:
    multipart:
      max-file-size: 100MB       # 单个文件最大 100MB
      max-request-size: 100MB    # 整个请求最大 100MB
```

2. **代码层面二次校验**:
```java
// 检查文件大小
if (file.getSize() > MAX_FILE_SIZE) {
    throw new RuntimeException("文件大小超过限制");
}

// 检查磁盘空间
File disk = new File("/");
if (disk.getFreeSpace() < file.getSize() * 2) {
    throw new RuntimeException("磁盘空间不足");
}
```

3. **流式处理，避免全文件加载到内存**:
```java
// ❌ 不安全：将文件全部加载到内存
byte[] fileBytes = file.getBytes();

// ✅ 安全：使用流式处理
try (InputStream in = file.getInputStream()) {
    // 逐块处理
}
```

---

### 2. 文件下载安全漏洞

#### 2.1 任意文件读取漏洞

**风险描述**: 攻击者通过修改 `fileMd5` 或路径参数，读取服务器上的敏感文件。

**排查步骤**:

1. **检查下载接口是否有权限校验**:
```java
// ❌ 不安全：没有验证文件所有权
@GetMapping("/download/{fileMd5}")
public void downloadFile(@PathVariable String fileMd5, HttpServletResponse response) {
    // 直接查询并返回文件
}

// ✅ 安全：验证文件是否存在且用户有权限
@GetMapping("/download/{fileMd5}")
public void downloadFile(@PathVariable String fileMd5, 
                        @AuthenticationPrincipal User currentUser,
                        HttpServletResponse response) {
    FileUploadRecord record = fileUploadService.getFileRecordByMd5(fileMd5);
    
    // 检查文件是否存在
    if (record == null || record.getStatus() != 2) {
        throw new RuntimeException("文件不存在");
    }
    
    // 检查用户权限（可选：只有上传者或管理员可下载）
    if (!record.getUploadUserId().equals(currentUser.getId()) 
        && !currentUser.isAdmin()) {
        throw new RuntimeException("无权访问此文件");
    }
    
    // 继续下载逻辑...
}
```

2. **检查存储路径是否安全**:
```java
// ❌ 不安全：直接使用用户提供的路径
String path = request.getParameter("path");
File file = new File(path);

// ✅ 安全：从数据库查询存储路径，不信任用户输入
FileUploadRecord record = fileUploadRecordMapper.selectByFileMd5(fileMd5);
String safePath = record.getStoragePath(); // 从数据库获取
```

#### 2.2 HTTP Range 请求漏洞

**风险描述**: Range 请求参数未校验，可能导致服务器资源耗尽。

**修复方案**:
```java
// 校验 Range 参数
if (startByte < 0 || endByte >= fileSize || startByte > endByte) {
    response.setStatus(HttpServletResponse.SC_REQUESTED_RANGE_NOT_SATISFIABLE);
    response.setHeader("Content-Range", "bytes */" + fileSize);
    return;
}

// 限制单次下载大小（防止恶意请求超大范围）
long rangeSize = endByte - startByte + 1;
if (rangeSize > MAX_RANGE_SIZE) {  // 例如 100MB
    throw new RuntimeException("请求范围过大");
}
```

---

### 3. 对象存储安全漏洞

#### 3.1 MinIO/OSS 访问密钥泄露

**排查步骤**:

1. **检查配置文件是否提交到 Git**:
```bash
# 检查是否有敏感配置
grep -r "access-key" cloud-file-service/src/main/resources/
```

2. **使用环境变量或密钥管理服务**:
```yaml
# ❌ 不安全：硬编码密钥
minio:
  access-key: minioadmin
  secret-key: minioadmin

# ✅ 安全：使用环境变量
minio:
  access-key: ${MINIO_ACCESS_KEY}
  secret-key: ${MINIO_SECRET_KEY}
```

3. **检查 `.gitignore` 是否忽略敏感文件**:
```gitignore
application-local.yml
*.env
application-prod.yml
```

#### 3.2 存储桶公开访问

**风险描述**: MinIO/OSS 存储桶设置为公开，导致敏感文件被任意访问。

**修复方案**:

1. **MinIO 设置私有存储桶**:
```java
// 确保存储桶为私有
minioClient.setBucketPolicy(
    SetBucketPolicyArgs.builder()
        .bucket(bucketName)
        .config("{\"Version\":\"2012-10-17\",\"Statement\":[]}")  // 空策略 = 私有
        .build()
);
```

2. **阿里云 OSS 设置私有权限**:
```java
// 设置存储桶为私有
ossClient.setBucketAcl(bucketName, CannedAccessControlList.Private);
```

3. **使用预签名 URL 临时授权**:
```java
// 生成 1 小时有效的下载链接
String presignedUrl = storageService.getPresignedUrl(bucketName, objectName, 60);
```

---

### 4. 数据库安全漏洞

#### 4.1 SQL 注入

**排查步骤**:

1. **检查是否使用 MyBatis-Plus 参数化查询**:
```java
// ✅ 安全：使用参数化查询
LambdaQueryWrapper<FileUploadRecord> wrapper = new LambdaQueryWrapper<>();
wrapper.eq(FileUploadRecord::getFileMd5, fileMd5);
FileUploadRecord record = fileUploadRecordMapper.selectOne(wrapper);

// ❌ 危险：直接拼接 SQL（不要这样做）
String sql = "SELECT * FROM file_upload_record WHERE file_md5 = '" + fileMd5 + "'";
```

2. **检查 XML Mapper 是否使用 `${}` 拼接**:
```xml
<!-- ❌ 危险：使用 ${} 可能 SQL 注入 -->
<select id="selectByFileMd5">
    SELECT * FROM file_upload_record WHERE file_md5 = ${fileMd5}
</select>

<!-- ✅ 安全：使用 #{} 参数化 -->
<select id="selectByFileMd5">
    SELECT * FROM file_upload_record WHERE file_md5 = #{fileMd5}
</select>
```

#### 4.2 敏感信息泄露

**排查步骤**:

1. **检查日志是否打印敏感信息**:
```java
// ❌ 不安全：日志中包含用户凭证
log.info("下载文件: fileMd5={}, userId={}, token={}", fileMd5, userId, token);

// ✅ 安全：脱敏处理
log.info("下载文件: fileMd5={}, userId={}", fileMd5, userId);
```

2. **检查错误响应是否包含敏感信息**:
```java
// ❌ 不安全：返回详细错误信息
} catch (Exception e) {
    return ResponseResult.error(e.getMessage());  // 可能泄露数据库结构
}

// ✅ 安全：返回通用错误信息
} catch (Exception e) {
    log.error("下载文件失败", e);  // 详细错误记录到日志
    return ResponseResult.error("下载失败，请稍后重试");  // 用户看到通用提示
}
```

---

### 5. 分片上传安全漏洞

#### 5.1 分片 MD5 校验绕过

**风险描述**: 攻击者上传恶意分片，由于未校验 MD5 导致文件损坏或被篡改。

**影响**:
- 文件内容被恶意替换
- 合并后的文件无法正常使用
- 可能植入恶意代码或病毒

**修复方案** (已实现):

```java
// 在 FileUploadServiceImpl.uploadChunk() 方法中

// 1. 计算实际分片MD5
String chunkMd5 = DigestUtil.md5Hex(file.getInputStream());

// 2. 与前端提供的MD5对比（如果提供了）
if (request.getChunkMd5() != null && !request.getChunkMd5().equals(chunkMd5)) {
    log.error("分片MD5校验失败: expected={}, actual={}, chunkIndex={}", 
            request.getChunkMd5(), chunkMd5, request.getChunkIndex());
    throw new RuntimeException("分片MD5校验失败，文件可能被篡改");
}

// 3. 记录MD5到数据库，供后续审计
chunkRecord.setChunkMd5(chunkMd5);
```

**最佳实践**:
- ✅ 前端必须计算并传递分片 MD5
- ✅ 后端必须重新计算并校验 MD5
- ✅ MD5 不匹配时立即拒绝上传
- ✅ 记录详细日志用于安全审计

#### 5.2 分片重放攻击

**风险描述**: 攻击者重复上传同一分片，导致数据库记录异常。

**影响**:
- 数据库中出现重复的分片记录
- 分片计数不准确，导致合并失败
- 浪费存储空间和带宽
- 可能导致唯一索引冲突异常

**修复方案** (已实现):

```java
// 在 FileUploadServiceImpl.uploadChunk() 方法中

// 安全检查1: 查询数据库中是否已存在该分片
FileChunkRecord existingChunk = fileChunkRecordMapper.selectOne(
    new LambdaQueryWrapper<FileChunkRecord>()
        .eq(FileChunkRecord::getFileMd5, request.getFileMd5())
        .eq(FileChunkRecord::getChunkIndex, request.getChunkIndex())
);

if (existingChunk != null) {
    log.warn("分片已存在，跳过上传: fileMd5={}, chunkIndex={}", 
            request.getFileMd5(), request.getChunkIndex());
    return FileUploadResponse.builder()
            .message(String.format("分片 %d/%d 已存在，跳过上传", 
                    request.getChunkIndex() + 1, request.getTotalChunks()))
            .build();
}

// 安全检查2: 检查对象存储中是否已存在该分片（双重保险）
if (storageService.fileExists(bucketName, chunkObjectName)) {
    log.warn("对象存储中分片已存在: {}", chunkObjectName);
    // 虽然文件已存在，但仍需记录到数据库
    // ...
}
```

**数据库层面防护**:
```sql
-- file_chunk_record 表已设置唯一索引
ALTER TABLE file_chunk_record 
ADD UNIQUE INDEX uk_file_chunk (file_md5, chunk_index);
```

**最佳实践**:
- ✅ 上传前先查询数据库是否存在
- ✅ 检查对象存储中文件是否存在
- ✅ 利用数据库唯一索引作为最后防线
- ✅ 返回友好提示，允许前端继续上传其他分片

#### 5.3 分片索引连续性校验

**风险描述**: 攻击者构造异常的分片索引（如跳跃、重复），导致文件合并错误。

**修复方案** (已实现):

```java
// 在 FileUploadServiceImpl.mergeChunks() 方法中

// 校验分片索引连续性（防止索引跳跃或重复）
List<Integer> chunkIndexes = chunkRecords.stream()
        .map(FileChunkRecord::getChunkIndex)
        .sorted()
        .collect(Collectors.toList());

for (int i = 0; i < chunkIndexes.size(); i++) {
    if (chunkIndexes.get(i) != i) {
        log.error("分片索引不连续: expected={}, actual={}, fileMd5={}", 
                i, chunkIndexes.get(i), fileMd5);
        throw new RuntimeException(String.format(
                "分片索引异常：期望索引 %d，实际索引 %d，可能存在数据篡改",
                i, chunkIndexes.get(i)));
    }
}
```

#### 5.4 分片总大小校验

**风险描述**: 分片总大小与文件大小差异过大，可能存在数据损坏或恶意篡改。

**修复方案** (已实现):

```java
// 在 FileUploadServiceImpl.mergeChunks() 方法中

// 校验分片大小合理性
long totalChunkSize = chunkRecords.stream()
        .mapToLong(FileChunkRecord::getChunkSize)
        .sum();

// 最后一个分片可能小于标准分片大小，所以允许一定的误差
long sizeDiff = Math.abs(totalChunkSize - record.getFileSize());
if (sizeDiff > record.getChunkSize()) {
    log.error("分片总大小与文件大小不匹配: totalChunkSize={}, fileSize={}, diff={}",
            totalChunkSize, record.getFileSize(), sizeDiff);
    throw new RuntimeException(String.format(
            "分片总大小(%d)与文件大小(%d)不匹配，差异过大(%d)，可能存在数据损坏",
            totalChunkSize, record.getFileSize(), sizeDiff));
}
```

**最佳实践**:
- ✅ 合并前校验分片索引连续性
- ✅ 校验分片总大小与文件大小的一致性
- ✅ 允许最后一个分片的正常误差
- ✅ 发现异常立即终止合并并记录日志

---

### 6. 跨域（CORS）安全

**风险描述**: CORS 配置过于宽松，导致任意域名可访问。

**排查与修复**:
```java
// ❌ 不安全：允许所有来源
configuration.addAllowedOrigin("*");

// ✅ 安全：指定允许的域名
configuration.addAllowedOrigin("https://yourdomain.com");
configuration.addAllowedOrigin("https://app.yourdomain.com");

// ✅ 限制允许的请求头和方法
configuration.addAllowedHeader("Content-Type", "Authorization");
configuration.addAllowedMethod("GET", "POST", "PUT", "DELETE");
```

---

### 7. 安全审计清单

#### 定期检查项

- [ ] **文件类型白名单**: 确保只允许上传安全的文件类型
- [ ] **文件内容校验**: 使用魔数校验，不信任扩展名
- [ ] **文件名安全**: 清理或重命名上传的文件
- [ ] **文件大小限制**: 配置文件大小上限，防止 DoS
- [ ] **权限校验**: 下载时验证用户权限
- [ ] **密钥管理**: 使用环境变量存储敏感配置
- [ ] **存储桶权限**: 确保存储桶为私有
- [ ] **SQL 注入防护**: 使用参数化查询
- [ ] **错误信息脱敏**: 不返回详细的错误堆栈
- [ ] **日志审计**: 记录所有文件操作日志
- [ ] **依赖漏洞扫描**: 定期运行 `mvn dependency-check:check`
- [ ] **HTTPS 强制**: 生产环境强制使用 HTTPS

#### 安全工具推荐

1. **依赖漏洞扫描**:
```bash
# OWASP Dependency Check
mvn org.owasp:dependency-check-maven:check
```

2. **静态代码分析**:
```bash
# SonarQube
sonar-scanner
```

3. **安全测试**:
```bash
# ZAP（OWASP Zed Attack Proxy）
zap.sh -quickurl http://localhost:8084/api/file
```

---

### 8. 应急响应

#### 发现安全漏洞时的处理流程

1. **立即隔离**:
   - 停止受影响的服务
   - 撤销泄露的访问密钥

2. **影响评估**:
   - 检查日志确定漏洞利用情况
   - 评估数据泄露范围

3. **修复漏洞**:
   - 修复代码漏洞
   - 更新依赖版本
   - 修改配置

4. **恢复服务**:
   - 部署修复版本
   - 验证修复效果
   - 恢复服务

5. **事后分析**:
   - 编写事故报告
   - 完善安全措施
   - 团队分享经验

---

## 📞 技术支持

- **项目地址**: `D:\5_java_project\hzqserver\cloud-file-service`
- **负责人**: hzqserver 团队
- **更新时间**: 2026-04-06

---

## 📝 更新日志

### v1.0.0 (2026-04-06)

- ✅ 分片上传功能
- ✅ 断点续传（上传）
- ✅ 秒传功能
- ✅ 文件下载
- ✅ Tomcat 超时配置优化
- ✅ 支持 MinIO/OSS 切换
- 🔜 断点续传下载（待实现）
