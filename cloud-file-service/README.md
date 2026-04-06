# File Service - 文件服务模块

## 📋 模块概述

文件服务模块提供文件上传、下载、断点续传等功能，支持 MinIO 和阿里云 OSS 两种存储方式。

### 核心特性

- ✅ 大文件分片上传（支持 1GB+ 文件）
- ✅ 断点续传（上传中断后可继续）
- ✅ 秒传功能（相同文件自动跳过）
- ✅ 分片合并（自动合并所有分片）
- ✅ 文件下载（支持超时配置）
- 🔜 断点续传下载（HTTP Range 请求，待实现）

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

## 🔜 待实现功能：断点续传下载

### 功能描述

当前下载功能**不支持断点续传**，如果下载中断需要重新下载整个文件。需要实现 **HTTP Range 请求**，支持：

- ✅ 暂停/恢复下载
- ✅ 只下载未完成的部分
- ✅ 支持大文件下载（避免超时）

### 实现方案

#### 1. 支持 HTTP Range 请求头

浏览器会在下载时发送 `Range` 请求头：

```http
GET /api/file/download/80681fabe090cbc3db3734802625d51e HTTP/1.1
Range: bytes=1048576-
```

表示请求从第 1MB 开始到文件末尾的数据。

#### 2. 后端实现思路

**修改 `FileUploadController.downloadFile` 方法**:

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

#### 3. 存储服务接口扩展

**在 `StorageService` 接口中添加**:

```java
/**
 * 下载文件指定范围的数据（支持断点续传）
 */
InputStream downloadFileRange(String bucketName, String objectName, 
                              long startByte, long endByte);
```

**MinIO 实现**:

```java
@Override
public InputStream downloadFileRange(String bucketName, String objectName, 
                                     long startByte, long endByte) {
    try {
        // MinIO 支持 Range 请求
        GetObjectArgs args = GetObjectArgs.builder()
                .bucket(bucketName)
                .object(objectName)
                .offset(startByte)
                .length(endByte - startByte + 1)
                .build();
        
        GetObjectResponse response = minioClient.getObject(args);
        return response;
    } catch (Exception e) {
        throw new RuntimeException("下载文件失败: " + e.getMessage(), e);
    }
}
```

**阿里云 OSS 实现**:

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
        throw new RuntimeException("下载文件失败: " + e.getMessage(), e);
    } finally {
        // 注意：不能关闭 ossClient，因为返回的 InputStream 依赖于它
    }
}
```

#### 4. 前端支持断点续传下载

前端需要：

1. **监听下载进度**（需要创建隐藏的 `<a>` 标签或使用 Fetch API）
2. **保存已下载进度**（使用 localStorage 或 IndexedDB）
3. **中断后恢复下载**（从上次下载位置继续）

**示例实现思路**:

```javascript
// 使用 Fetch API 支持 Range 请求
async function downloadWithResume(fileMd5, fileName) {
    // 从 localStorage 获取已下载进度
    const downloadedBytes = localStorage.getItem(`download_${fileMd5}`) || 0;
    
    const headers = {};
    if (downloadedBytes > 0) {
        headers['Range'] = `bytes=${downloadedBytes}-`;
    }
    
    const response = await fetch(`/api/file/download/${fileMd5}`, { headers });
    
    // 获取总文件大小
    const contentRange = response.headers.get('Content-Range');
    const totalSize = contentRange 
        ? parseInt(contentRange.split('/')[1]) 
        : parseInt(response.headers.get('Content-Length'));
    
    // 读取数据流
    const reader = response.body.getReader();
    let receivedBytes = downloadedBytes;
    const chunks = [];
    
    while (true) {
        const { done, value } = await reader.read();
        
        if (done) break;
        
        chunks.push(value);
        receivedBytes += value.length;
        
        // 更新进度
        const progress = (receivedBytes / totalSize * 100).toFixed(2);
        console.log(`下载进度: ${progress}%`);
        
        // 保存到 localStorage
        localStorage.setItem(`download_${fileMd5}`, receivedBytes);
    }
    
    // 合并所有 chunks 并创建 Blob
    const blob = new Blob(chunks, { type: response.headers.get('Content-Type') });
    const url = URL.createObjectURL(blob);
    
    // 触发下载
    const a = document.createElement('a');
    a.href = url;
    a.download = fileName;
    a.click();
    
    // 清理
    localStorage.removeItem(`download_${fileMd5}`);
    URL.revokeObjectURL(url);
}
```

### 实现优先级

1. **P0（必须）**: 后端支持 HTTP Range 请求
2. **P1（建议）**: 前端支持断点续传下载
3. **P2（优化）**: 下载进度条显示
4. **P3（可选）**: 多线程并行下载（类似迅雷）

### 参考资源

- HTTP Range 请求规范: https://developer.mozilla.org/zh-CN/docs/Web/HTTP/Range_requests
- MinIO Range 下载: https://min.io/docs/minio/linux/developers/java/API.html#getobject
- OSS Range 下载: https://help.aliyun.com/zh/oss/developer-reference/copy-objects

---

## 📊 性能优化建议

### 1. 分片大小配置

| 网络环境 | 推荐分片大小 | 说明 |
|---------|-------------|------|
| 局域网 | 10-20 MB | 带宽充足，大分片减少请求次数 |
| 家庭宽带 | 5-10 MB | 平衡稳定性和速度 |
| 移动网络 | 2-5 MB | 网络不稳定，小分片提高成功率 |

### 2. 并发上传

前端可以支持多个分片并发上传：

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

### 3. 对象存储优化

- **MinIO**: 启用纠删码模式，提高数据可靠性
- **OSS**: 配置 CDN 加速下载
- **通用**: 定期清理未完成的分片文件（定时任务）

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
