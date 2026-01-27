package com.hzqserver.springai.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 多提供商聊天模型提供者
 * 支持阿里云百炼、本地部署模型等多种AI服务
 */
@Component
@Primary
public class MultiProviderChatModelProvider implements ChatModelProvider {
    
    @Value("${ai.provider.type:mock}") // 默认为mock模式
    private String providerType;
    
    @Value("${spring.ai.dashscope.api-key:#{null}}")
    private String dashscopeApiKey;
    
    @Value("${spring.ai.openai.api-key:#{null}}")
    private String openaiApiKey;
    
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    @Override
    public ChatModel getCurrentChatModel() {
        switch (providerType.toLowerCase()) {
            case "dashscope":
                if (isValidApiKey(dashscopeApiKey)) {
                    return createDashScopeChatModel();
                }
                // 如果百炼API密钥无效，回退到模拟模式
                System.out.println("DashScope API key is not valid, using mock model");
                return createMockChatModel();
            case "openai":
                if (isValidApiKey(openaiApiKey)) {
                    return createOpenAiChatModel();
                }
                // 如果OpenAI API密钥无效，回退到模拟模式
                System.out.println("OpenAI API key is not valid, using mock model");
                return createMockChatModel();
            case "local":
                return createLocalChatModel();
            case "mock":
            default:
                System.out.println("Using mock AI model");
                return createMockChatModel();
        }
    }
    
    private boolean isValidApiKey(String apiKey) {
        return apiKey != null && !apiKey.trim().isEmpty() && !apiKey.equals("your-dashscope-api-key-here") ;
    }
    
    private ChatModel createDashScopeChatModel() {
        return new ChatModel() {
            @Override
            public String call(String prompt) {
                try {
                    // 使用与curl示例完全一致的请求格式
                    String url = "https://dashscope.aliyuncs.com/compatible-mode/v1/chat/completions";
                    
                    // 构建JSON请求体
                    String jsonBody = String.format(
                        "{\n" +
                        "  \"model\": \"qwen-plus\",\n" +
                        "  \"messages\": [\n" +
                        "    {\n" +
                        "      \"role\": \"user\",\n" +
                        "      \"content\": \"%s\"\n" +
                        "    }\n" +
                        "  ]\n" +
                        "}",
                        prompt.replace("\"", "\\\"")
                    );
                    
                    // 创建HTTP请求
                    HttpHeaders headers = new HttpHeaders();
                    headers.set("Authorization", "Bearer " + dashscopeApiKey);
                    headers.setContentType(MediaType.APPLICATION_JSON);
                    
                    HttpEntity<String> requestEntity = new HttpEntity<>(jsonBody, headers);
                    
                    // 发送POST请求
                    ResponseEntity<String> response = restTemplate.postForEntity(url, requestEntity, String.class);
                    
                    // 解析响应
                    if (response.getStatusCode().is2xxSuccessful()) {
                        JsonNode rootNode = objectMapper.readTree(response.getBody());
                        
                        // 尝试从不同路径获取内容
                        if (rootNode.has("choices") && rootNode.get("choices").isArray() && 
                            rootNode.get("choices").size() > 0) {
                            JsonNode choice = rootNode.get("choices").get(0);
                            
                            // 尝试 qwen-plus 的响应格式
                            if (choice.has("message") && choice.get("message").has("content")) {
                                return choice.get("message").get("content").asText();
                            } else if (choice.has("text")) {
                                return choice.get("text").asText();
                            }
                        }
                        
                        return "Response: " + response.getBody();
                    }
                    
                    return "Error: HTTP " + response.getStatusCode() + " - " + response.getBody();
                } catch (Exception e) {
                    return "Error: " + e.getMessage();
                }
            }
        };
    }
    
    private ChatModel createOpenAiChatModel() {
        return new ChatModel() {
            @Override
            public String call(String prompt) {
                return "[OpenAI] Mock response for: " + prompt + ". Actual implementation requires spring-ai-openai dependency.";
            }
        };
    }
    
    private ChatModel createLocalChatModel() {
        return new ChatModel() {
            @Override
            public String call(String prompt) {
                return "[Local Model] Mock response for: " + prompt + ". Actual implementation requires local AI model setup.";
            }
        };
    }
    
    private ChatModel createMockChatModel() {
        return new ChatModel() {
            @Override
            public String call(String prompt) {
                return "[Mock] This is a mock AI response since no valid provider is configured. Prompt was: " + prompt;
            }
        };
    }
}