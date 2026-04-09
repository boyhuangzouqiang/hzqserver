# File 模块分片上传 API 文档

## 概述

File 模块提供完整的分片上传功能，支持：
- ✅ 大文件分片上传
- ✅ 断点续传
- ✅ 秒传（基于文件 MD5）
- ✅ 文件下载

**基础 URL**: `http://localhost:8083/api/file`

---

## 1. 获取上传配置

获取推荐的分片大小和限制参数。

### 请求

```
GET /upload/config
```

**请求参数：** 无

### 响应

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "recommendedChunkSize": 5242880,
    "maxChunkSize": 10485760,
    "minChunkSize": 1048576,
    "maxFileSize": 1073741824
  }
}
```

**字段说明：**
- `recommendedChunkSize`: 推荐分片大小（5MB）
- `maxChunkSize`: 最大分片大小（10MB）
- `minChunkSize`: 最小分片大小（1MB）
- `maxFileSize`: 最大文件大小（1GB）

---

## 2. 初始化上传任务

创建上传任务，返回任务 ID 和状态。如果文件已存在则直接返回（秒传）。

### 请求

```
POST /upload/init
Content-Type: application/json
```

**Body:**
```json
{
  "fileMd5": "d41d8cd98f00b204e9800998ecf8427e",
  "fileName": "test.pdf",
  "fileSize": 10485760,
  "contentType": "application/pdf",
  "totalChunks": 2,
  "chunkSize": 5242880
}
```

**Body 参数详细说明：**

| 参数名 | 类型 | 必填 | 说明 | 示例值 | 备注 |
|--------|------|------|------|--------|------|
| fileMd5 | String | ✅ 是 | 整个文件的 MD5 哈希值（32位十六进制） | `d41d8cd98f00b204e9800998ecf8427e` | 用于文件去重和秒传 |
| fileName | String | ✅ 是 | 原始文件名（包含扩展名） | `test.pdf` | 支持中文文件名 |
| fileSize | Long | ✅ 是 | 文件总大小（单位：字节） | `10485760` | 必须与实际文件大小一致 |
| contentType | String | ✅ 是 | 文件的 MIME 类型 | `application/pdf` | 常见类型见下方说明 |
| totalChunks | Integer | ✅ 是 | 分片总数 | `2` | 计算公式：ceil(fileSize / chunkSize) |
| chunkSize | Long | ✅ 是 | 每个分片的大小（单位：字节） | `5242880` | 范围：1MB - 10MB |

**常用 MIME 类型参考：**
- PDF: `application/pdf`
- Word: `application/msword` 或 `application/vnd.openxmlformats-officedocument.wordprocessingml.document`
- Excel: `application/vnd.ms-excel` 或 `application/vnd.openxmlformats-officedocument.spreadsheetml.sheet`
- 图片: `image/jpeg`, `image/png`, `image/gif`
- 视频: `video/mp4`, `video/avi`
- 压缩包: `application/zip`, `application/x-rar-compressed`
- 通用二进制: `application/octet-stream`

**参数计算示例：**
```javascript
// 假设文件大小为 12MB (12582912 字节)，分片大小为 5MB (5242880 字节)
const fileSize = 12582912;
const chunkSize = 5242880;
const totalChunks = Math.ceil(fileSize / chunkSize); // 结果：3

// 实际分片情况：
// 分片 0: 5242880 字节 (5MB)
// 分片 1: 5242880 字节 (5MB)
// 分片 2: 2097152 字节 (2MB) - 最后一个分片可能不足 chunkSize
```

### 响应

**场景 1：初始化成功**
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "fileId": 1,
    "fileMd5": "d41d8cd98f00b204e9800998ecf8427e",
    "fileName": "test.pdf",
    "fileSize": 10485760,
    "status": 0,
    "uploadedChunks": [],
    "message": "初始化成功"
  }
}
```

**场景 2：文件已存在（秒传）**
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "fileId": 1,
    "fileMd5": "d41d8cd98f00b204e9800998ecf8427e",
    "fileName": "test.pdf",
    "fileSize": 10485760,
    "accessUrl": "http://minio-server:9000/bucket/files/xxx/test.pdf",
    "status": 2,
    "message": "文件已存在"
  }
}
```

**场景 3：断点续传（有未完成的上传）**
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "fileId": 1,
    "fileMd5": "d41d8cd98f00b204e9800998ecf8427e",
    "fileName": "test.pdf",
    "fileSize": 10485760,
    "status": 1,
    "uploadedChunks": [0],
    "message": "检测到未完成的上传，已上传 1/2 个分片"
  }
}
```

**状态码说明：**
- `0`: 初始化
- `1`: 上传中
- `2`: 已完成
- `3`: 失败

---

## 3. 上传分片

上传单个分片文件。需要为每个分片调用一次此接口。

### 请求

```
POST /upload/chunk
Content-Type: multipart/form-data
```

**Form Data 参数详细说明：**

| 参数名 | 类型 | 必填 | 说明 | 示例值 | 备注 |
|--------|------|------|------|--------|------|
| fileMd5 | Text | ✅ 是 | 文件 MD5（与初始化时一致） | `d41d8cd98f00b204e9800998ecf8427e` | 必须与 init 接口传入的相同 |
| fileName | Text | ✅ 是 | 文件名（与初始化时一致） | `test.pdf` | 必须与 init 接口传入的相同 |
| fileSize | Text | ✅ 是 | 文件总大小（与初始化时一致） | `10485760` | 必须与 init 接口传入的相同 |
| contentType | Text | ✅ 是 | MIME 类型（与初始化时一致） | `application/pdf` | 必须与 init 接口传入的相同 |
| totalChunks | Text | ✅ 是 | 分片总数（与初始化时一致） | `2` | 必须与 init 接口传入的相同 |
| chunkSize | Text | ✅ 是 | 分片大小（与初始化时一致） | `5242880` | 必须与 init 接口传入的相同 |
| **chunkIndex** | **Text** | **✅ 是** | **当前分片索引（从 0 开始）** | **`0`** | **第1个分片=0，第2个=1，以此类推** |
| chunkMd5 | Text | ❌ 否 | 当前分片的 MD5 值 | `a1b2c3d4...` | 用于校验分片完整性，不传则后端自动计算 |
| **file** | **File** | **✅ 是** | **分片文件数据** | **[选择文件]** | **必须是实际切割后的分片文件** |

**重要说明：**

1. **chunkIndex 索引规则：**
   - 从 `0` 开始计数
   - 第一个分片：`chunkIndex = 0`
   - 第二个分片：`chunkIndex = 1`
   - 最后一个分片：`chunkIndex = totalChunks - 1`

2. **file 字段要求：**
   - 必须是实际切割后的分片文件
   - 前 N-1 个分片大小应等于 `chunkSize`
   - 最后一个分片大小可能小于 `chunkSize`
   - 文件格式建议：`.dat` 或保持原扩展名

3. **可选参数 chunkMd5：**
   - 如果提供，后端会校验分片 MD5 是否匹配
   - 如果不提供，后端会自动计算并存储
   - 建议提供以增强数据完整性校验

4. **其他参数一致性：**
   - 除 `chunkIndex`、`chunkMd5`、`file` 外，其他参数必须与初始化时完全一致
   - 不一致会导致后端校验失败

### 响应

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "fileMd5": "d41d8cd98f00b204e9800998ecf8427e",
    "fileName": "test.pdf",
    "fileSize": 10485760,
    "status": 1,
    "message": "分片 1/2 上传成功"
  }
}
```

---

## 4. 检查上传状态

查询文件的上传进度和已上传的分片列表，用于断点续传。

### 请求

```
GET /upload/status?fileMd5=d41d8cd98f00b204e9800998ecf8427e
```

**请求参数：**

| 参数名 | 类型 | 必填 | 位置 | 说明 | 示例值 |
|--------|------|------|------|------|--------|
| fileMd5 | String | ✅ 是 | Query | 文件 MD5 | `d41d8cd98f00b204e9800998ecf8427e` |

### 响应

**场景 1：上传中**
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "fileId": 1,
    "fileMd5": "d41d8cd98f00b204e9800998ecf8427e",
    "fileName": "test.pdf",
    "fileSize": 10485760,
    "status": 1,
    "uploadedChunks": [0, 1],
    "message": "已上传 2/3 个分片"
  }
}
```

**场景 2：已完成**
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "fileId": 1,
    "fileMd5": "d41d8cd98f00b204e9800998ecf8427e",
    "fileName": "test.pdf",
    "fileSize": 10485760,
    "accessUrl": "http://minio-server:9000/bucket/files/xxx/test.pdf",
    "status": 2,
    "uploadedChunks": [],
    "message": "文件已上传完成"
  }
}
```

**场景 3：未初始化**
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "fileMd5": "d41d8cd98f00b204e9800998ecf8427e",
    "status": -1,
    "uploadedChunks": [],
    "message": "文件未初始化"
  }
}
```

---

## 5. 合并分片

所有分片上传完成后，调用此接口合并分片生成完整文件。

### 请求

```
POST /upload/merge?fileMd5=d41d8cd98f00b204e9800998ecf8427e
```

**请求参数：**

| 参数名 | 类型 | 必填 | 位置 | 说明 | 示例值 |
|--------|------|------|------|------|--------|
| fileMd5 | String | ✅ 是 | Query | 文件 MD5 | `d41d8cd98f00b204e9800998ecf8427e` |

**前置条件：**
- 所有分片必须已上传完成
- 已上传分片数 = totalChunks
- 调用此接口前建议先调用 status 接口确认

### 响应

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "fileId": 1,
    "fileMd5": "d41d8cd98f00b204e9800998ecf8427e",
    "fileName": "test.pdf",
    "fileSize": 10485760,
    "accessUrl": "http://minio-server:9000/bucket/files/d41d8cd98f00b204e9800998ecf8427e/test.pdf",
    "status": 2,
    "message": "文件上传完成"
  }
}
```

**注意：** 合并成功后，系统会自动删除 MinIO/OSS 中的临时分片文件以节省存储空间。

---

## 6. 取消上传

取消正在进行的上传任务，清理相关资源。

### 请求

```
DELETE /upload/cancel?fileMd5=d41d8cd98f00b204e9800998ecf8427e
```

**请求参数：**

| 参数名 | 类型 | 必填 | 位置 | 说明 | 示例值 |
|--------|------|------|------|------|--------|
| fileMd5 | String | ✅ 是 | Query | 文件 MD5 | `d41d8cd98f00b204e9800998ecf8427e` |

**注意：** 取消后会删除所有已上传的分片和记录，无法恢复

### 响应

```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```

---

## 7. 下载文件（支持断点续传）

下载已上传的文件，支持 HTTP Range 协议实现断点续传。

### 请求

```
GET /download/{fileMd5}
```

**路径参数：**

| 参数名 | 类型 | 必填 | 说明 | 示例值 |
|--------|------|------|------|--------|
| fileMd5 | String | ✅ 是 | 文件 MD5 | `d41d8cd98f00b204e9800998ecf8427e` |

**查询参数（可选）：**

| 参数名 | 类型 | 必填 | 说明 | 示例值 |
|--------|------|------|------|--------|
| Range | String | ❌ 否 | HTTP请求头，字节范围 | `bytes=0-1048575` |

**示例请求：**
```
GET /download/d41d8cd98f00b204e9800998ecf8427e
Range: bytes=0-1048575
```

**前置条件：**
- 文件必须已完成上传（status = 2）
- 文件未被删除

### 响应

#### 成功响应（完整下载）

**状态码：** `200 OK`

**响应头：**
```
Content-Type: application/pdf
Content-Length: 10485760
Content-Disposition: attachment; filename=test.pdf
Accept-Ranges: bytes
```

**响应体：** 文件二进制流

---

#### 成功响应（部分下载/断点续传）

**状态码：** `206 Partial Content`

**响应头：**
```
Content-Type: application/pdf
Content-Length: 1048576
Content-Range: bytes 0-1048575/10485760
Content-Disposition: attachment; filename=test.pdf
Accept-Ranges: bytes
```

**响应体：** 指定范围的二进制数据

---

#### 错误响应

**状态码：** `404 Not Found`

**响应体：**
```
文件不存在
```

---

### Postman 使用指南

#### 方式一：基础下载（完整文件）

1. **创建新请求**
   - Method: `GET`
   - URL: `http://localhost:8084/api/file/download/d41d8cd98f00b204e9800998ecf8427e`

2. **发送请求**
   - 点击 `Send`
   - 在响应区域点击 `Save Response` → `Save to a file`
   - 选择保存路径和文件名

---

#### 方式二：断点续传（指定范围）

1. **创建新请求**
   - Method: `GET`
   - URL: `http://localhost:8084/api/file/download/d41d8cd98f00b204e9800998ecf8427e`

2. **设置Headers**
   ```
   Range: bytes=0-1048575
   ```

3. **发送请求**
   - 查看响应状态码应为 `206 Partial Content`
   - 响应头包含 `Content-Range: bytes 0-1048575/10485760`

---

#### 方式三：使用环境变量

1. **设置环境变量**
   - 点击右上角齿轮图标 ⚙️
   - 添加变量：
     ```
     baseUrl: http://localhost:8084
     fileMd5: d41d8cd98f00b204e9800998ecf8427e
     fileName: test.pdf
     ```

2. **配置请求**
   - Method: `GET`
   - URL: `{{baseUrl}}/api/file/download/{{fileMd5}}`

3. **自动保存文件（使用Tests脚本）**
   - 切换到 `Tests` 标签
   - 添加以下代码：
     ```javascript
     // 检查响应状态
     if (pm.response.code === 200 || pm.response.code === 206) {
         console.log('下载成功');
         console.log('文件大小:', pm.response.headers.get('Content-Length'));
         console.log('文件名:', pm.response.headers.get('Content-Disposition'));
     } else {
         console.error('下载失败:', pm.response.text());
     }
     ```

---

#### 方式四：批量下载测试（Collection Runner）

1. **创建Collection**
   - 名称：`File Download Tests`

2. **添加多个请求**
   - Request 1: 下载文件A
   - Request 2: 下载文件B
   - Request 3: 断点续传测试

3. **运行Collection**
   - 点击 Collection 右侧的 `...`
   - 选择 `Run collection`
   - 点击 `Run` 开始批量测试

---

### cURL 示例

#### 完整下载
```bash
curl http://localhost:8084/api/file/download/d41d8cd98f00b204e9800998ecf8427e \
  --output downloaded_file.pdf
```

#### 断点续传
```bash
curl http://localhost:8084/api/file/download/d41d8cd98f00b204e9800998ecf8427e \
  -H "Range: bytes=0-1048575" \
  --output partial_file.dat
```

---

### JavaScript/Fetch 示例

```javascript
// 完整下载
async function downloadFile(fileMd5) {
  const response = await fetch(`http://localhost:8084/api/file/download/${fileMd5}`, {
    method: 'GET'
  });
  
  if (!response.ok) {
    throw new Error(`下载失败: ${response.status}`);
  }
  
  const blob = await response.blob();
  const url = window.URL.createObjectURL(blob);
  const a = document.createElement('a');
  a.href = url;
  a.download = 'downloaded_file.pdf';
  a.click();
  window.URL.revokeObjectURL(url);
}

// 断点续传
async function downloadWithResume(fileMd5, startByte, endByte) {
  const response = await fetch(`http://localhost:8084/api/file/download/${fileMd5}`, {
    method: 'GET',
    headers: {
      'Range': `bytes=${startByte}-${endByte}`
    }
  });
  
  if (response.status === 206) {
    console.log('部分下载成功');
    const contentRange = response.headers.get('Content-Range');
    console.log('范围:', contentRange);
  }
  
  return await response.arrayBuffer();
}
```

---

## 8. 获取上传历史

分页查询已完成的上传记录。

### 请求

```
GET /upload/history?page=1&pageSize=20
```

**请求参数：**

| 参数名 | 类型 | 必填 | 位置 | 说明 | 默认值 | 示例值 |
|--------|------|------|------|------|--------|--------|
| page | Integer | ❌ 否 | Query | 页码（从 1 开始） | `1` | `2` |
| pageSize | Integer | ❌ 否 | Query | 每页数量 | `20` | `50` |

**参数说明：**
- `page`: 页码，最小值为 1
- `pageSize`: 每页显示数量，建议 10-100 之间

### 响应

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "list": [
      {
        "id": 1,
        "fileMd5": "d41d8cd98f00b204e9800998ecf8427e",
        "fileName": "test.pdf",
        "fileExt": "pdf",
        "fileSize": 10485760,
        "contentType": "application/pdf",
        "storagePath": "files/d41d8cd98f00b204e9800998ecf8427e/test.pdf",
        "accessUrl": "http://minio-server:9000/bucket/files/xxx/test.pdf",
        "status": 2,
        "createTime": "2024-01-01T10:00:00",
        "updateTime": "2024-01-01T10:05:00"
      }
    ],
    "total": 100,
    "page": 1,
    "pageSize": 20
  }
}
```

---

## 完整上传流程示例

### 场景：上传一个 12MB 的 PDF 文件

#### 步骤 1：计算文件 MD5

**PowerShell:**
```powershell
Get-FileHash -Path "D:\test.pdf" -Algorithm MD5
```

假设得到 MD5: `abc123def456...`

#### 步骤 2：分割文件

**PowerShell 脚本：**
```powershell
$chunkSize = 5 * 1024 * 1024  # 5MB
$filePath = "D:\test.pdf"
$file = Get-Item $filePath
$totalChunks = [Math]::Ceiling($file.Length / $chunkSize)

for ($i = 0; $i -lt $totalChunks; $i++) {
    $start = $i * $chunkSize
    $length = [Math]::Min($chunkSize, $file.Length - $start)
    
    $stream = [System.IO.File]::OpenRead($filePath)
    $stream.Seek($start, 0) | Out-Null
    $bytes = New-Object byte[] $length
    $stream.Read($bytes, 0, $length) | Out-Null
    $stream.Close()
    
    [System.IO.File]::WriteAllBytes("$PWD\chunk_$i.dat", $bytes)
}
```

生成 3 个分片：`chunk_0.dat`, `chunk_1.dat`, `chunk_2.dat`

#### 步骤 3：初始化上传

```http
POST http://localhost:8083/api/file/upload/init
Content-Type: application/json

{
  "fileMd5": "abc123def456...",
  "fileName": "test.pdf",
  "fileSize": 12582912,
  "contentType": "application/pdf",
  "totalChunks": 3,
  "chunkSize": 5242880
}
```

#### 步骤 4：上传分片

**分片 0:**
```http
POST http://localhost:8083/api/file/upload/chunk
Content-Type: multipart/form-data

fileMd5: abc123def456...
fileName: test.pdf
fileSize: 12582912
contentType: application/pdf
totalChunks: 3
chunkSize: 5242880
chunkIndex: 0
file: [选择 chunk_0.dat]
```

**分片 1:**
```http
同上，但 chunkIndex: 1，file: [选择 chunk_1.dat]
```

**分片 2:**
```http
同上，但 chunkIndex: 2，file: [选择 chunk_2.dat]
```

#### 步骤 5：合并分片

```http
POST http://localhost:8083/api/file/upload/merge?fileMd5=abc123def456...
```

#### 步骤 6：验证下载

**方式一：完整下载**
```http
GET http://localhost:8084/api/file/download/abc123def456...
```

**方式二：断点续传（下载前1MB）**
```http
GET http://localhost:8084/api/file/download/abc123def456...
Range: bytes=0-1048575
```

---

## Postman 使用技巧

### 1. 设置环境变量

在 Postman 中创建环境变量：
- `baseUrl`: `http://localhost:8084`
- `fileMd5`: `你的文件MD5`
- `fileName`: `test.pdf`
- `fileSize`: `12582912`

**设置步骤：**
1. 点击右上角齿轮图标 ⚙️
2. 点击 `Add` 创建新环境
3. 添加上述变量
4. 选择环境为当前激活状态

### 2. 使用 Collection Runner

可以创建 Collection，按顺序执行：
1. Init Upload
2. Upload Chunk 0
3. Upload Chunk 1
4. Upload Chunk 2
5. Merge Chunks
6. Download File (完整下载)
7. Download File (断点续传测试)

---

### 3. Postman Collection 导入示例

你可以将以下 JSON 保存为 `file-upload-collection.json`，然后导入到 Postman：

```json
{
  "info": {
    "name": "File Upload & Download API",
    "schema": "https://schema.getpostman.com/json/collection/v2.1.0/collection.json"
  },
  "item": [
    {
      "name": "1. Initialize Upload",
      "request": {
        "method": "POST",
        "header": [{"key": "Content-Type", "value": "application/json"}],
        "body": {
          "mode": "raw",
          "raw": "{\n  \"fileMd5\": \"{{fileMd5}}\",\n  \"fileName\": \"{{fileName}}\",\n  \"fileSize\": {{fileSize}},\n  \"contentType\": \"application/pdf\",\n  \"totalChunks\": 3,\n  \"chunkSize\": 5242880\n}"
        },
        "url": {
          "raw": "{{baseUrl}}/api/file/upload/init",
          "host": ["{{baseUrl}}"],
          "path": ["api", "file", "upload", "init"]
        }
      }
    },
    {
      "name": "2. Upload Chunk",
      "request": {
        "method": "POST",
        "header": [],
        "body": {
          "mode": "formdata",
          "formdata": [
            {"key": "fileMd5", "value": "{{fileMd5}}", "type": "text"},
            {"key": "fileName", "value": "{{fileName}}", "type": "text"},
            {"key": "fileSize", "value": "{{fileSize}}", "type": "text"},
            {"key": "contentType", "value": "application/pdf", "type": "text"},
            {"key": "totalChunks", "value": "3", "type": "text"},
            {"key": "chunkSize", "value": "5242880", "type": "text"},
            {"key": "chunkIndex", "value": "0", "type": "text"},
            {"key": "file", "type": "file", "src": "/path/to/chunk_0.dat"}
          ]
        },
        "url": {
          "raw": "{{baseUrl}}/api/file/upload/chunk",
          "host": ["{{baseUrl}}"],
          "path": ["api", "file", "upload", "chunk"]
        }
      }
    },
    {
      "name": "3. Merge Chunks",
      "request": {
        "method": "POST",
        "header": [],
        "url": {
          "raw": "{{baseUrl}}/api/file/upload/merge?fileMd5={{fileMd5}}",
          "host": ["{{baseUrl}}"],
          "path": ["api", "file", "upload", "merge"],
          "query": [{"key": "fileMd5", "value": "{{fileMd5}}"}]
        }
      }
    },
    {
      "name": "4. Download File (Complete)",
      "request": {
        "method": "GET",
        "header": [],
        "url": {
          "raw": "{{baseUrl}}/api/file/download/{{fileMd5}}",
          "host": ["{{baseUrl}}"],
          "path": ["api", "file", "download", "{{fileMd5}}"]
        }
      }
    },
    {
      "name": "5. Download File (Resume from byte 0-1MB)",
      "request": {
        "method": "GET",
        "header": [
          {"key": "Range", "value": "bytes=0-1048575"}
        ],
        "url": {
          "raw": "{{baseUrl}}/api/file/download/{{fileMd5}}",
          "host": ["{{baseUrl}}"],
          "path": ["api", "file", "download", "{{fileMd5}}"]
        }
      }
    },
    {
      "name": "6. Get Upload History",
      "request": {
        "method": "GET",
        "header": [],
        "url": {
          "raw": "{{baseUrl}}/api/file/upload/history?page=1&pageSize=20",
          "host": ["{{baseUrl}}"],
          "path": ["api", "file", "upload", "history"],
          "query": [
            {"key": "page", "value": "1"},
            {"key": "pageSize", "value": "20"}
          ]
        }
      }
    }
  ],
  "variable": [
    {"key": "baseUrl", "value": "http://localhost:8084"},
    {"key": "fileMd5", "value": "d41d8cd98f00b204e9800998ecf8427e"},
    {"key": "fileName", "value": "test.pdf"},
    {"key": "fileSize", "value": "12582912"}
  ]
}
```

**导入步骤：**
1. 复制上面的 JSON 内容
2. 保存为 `file-upload-collection.json`
3. 打开 Postman
4. 点击左上角 `Import`
5. 选择文件并导入
6. 设置环境变量后开始测试

---

### 4. 批量测试

使用 Postman 的 Pre-request Script 自动计算 MD5 和分片信息。

---

## 错误码说明

| 错误信息 | 原因 | 解决方案 |
|---------|------|---------|
| 文件大小超过限制 | 文件 > 1GB | 使用更小的文件或调整配置 |
| 分片大小超过限制 | chunkSize > 10MB | 减小分片大小 |
| 分片大小过小 | chunkSize < 1MB | 增大分片大小 |
| 分片MD5校验失败 | 分片数据损坏 | 重新上传该分片 |
| 分片不完整 | 部分分片未上传 | 检查 uploadedChunks，补传缺失分片 |
| 文件上传记录不存在 | 未初始化或已取消 | 重新调用 init 接口 |

---

## 注意事项

1. **分片大小建议**：推荐使用 5MB，可根据网络状况在 1-10MB 之间调整
2. **并发上传**：可以同时上传多个分片，提高上传速度
3. **断点续传**：上传中断后，先调用 status 接口查询已上传分片，跳过这些分片继续上传
4. **秒传机制**：相同文件（MD5 相同）只需上传一次，后续直接返回访问地址
5. **资源清理**：合并成功后自动删除临时分片；取消上传时也会清理资源
6. **超时配置**：大文件上传需确保 Tomcat 连接超时时间足够长

---

## 技术支持

如有问题，请检查：
1. 服务是否正常运行
2. MinIO/OSS 配置是否正确
3. 数据库连接是否正常
4. 查看服务端日志定位具体错误
