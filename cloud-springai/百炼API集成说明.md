# 阿里云百炼API集成说明

## 项目变更总结

本项目已从OpenAI API迁移到阿里云百炼(DashScope) API，支持通义千问系列模型。

### 主要变更文件

1. **application.yml** - 更新配置属性
2. **pom.xml** - 更新依赖配置
3. **AiConfig.java** - 更新API配置逻辑
4. **所有服务和控制器** - 保留原有逻辑，适配新API

### 配置说明

#### application.yml 配置
```yaml
spring:
  ai:
    dashscope:
      api-key: ${DASHSCOPE_API_KEY:your-dashscope-api-key-here}  # 百炼API密钥
      enabled: ${SPRING_AI_DASHSCOPE_ENABLED:true}               # 是否启用
      chat:
        options:
          model: qwen-plus  # 使用通义千问plus模型
          temperature: 0.7  # 控制随机性
          max-tokens: 2048  # 最大生成token数
```

### 依赖说明

项目使用以下关键依赖：
- `spring-ai-alibaba-starter-dashscope`: 阿里云百炼API集成
- `spring-ai-bom`: Spring AI依赖管理
- `spring-ai-alibaba-bom`: Spring AI Alibaba依赖管理

### 部署说明

1. **获取API密钥**：在阿里云百炼平台获取DASHSCOPE_API_KEY
2. **环境配置**：将API密钥设置为环境变量
3. **启动应用**：应用将自动连接到通义千问服务

### 开发模式

- 当未设置有效的API密钥时，应用将使用模拟响应模式继续运行
- 模拟模式下所有AI调用将返回预设的模拟响应
- 此设计确保在开发和测试环境中应用可正常启动

### 验证步骤

修复Maven配置后，执行以下命令验证：

```bash
cd cloud-springai
mvn clean compile
mvn spring-boot:run
```

### 注意事项

1. 需要修复Maven配置错误才能正常编译
2. 生产环境必须配置有效的API密钥
3. 模拟模式仅适用于开发和测试