package com.hzqserver.springai.service;

import org.springframework.stereotype.Service;

/**
 * 聊天代理服务类
 * 提供与AI模型进行对话交互的功能
 */
@Service
public class ChatAgentService {

    private final AiService aiService;

    /**
     * 构造函数，注入AI服务实例
     * @param aiService AI服务实例
     */
    public ChatAgentService(AiService aiService) {
        this.aiService = aiService;
    }

    /**
     * 使用聊天模型与客户端进行简单对话
     * @param userInput 用户输入的消息
     * @return AI模型返回的响应消息
     */
    public String chatWithClient(String userInput) {
        return aiService.chat(userInput);
    }

    /**
     * 使用聊天模型进行高级对话
     * @param userInput 用户输入的消息
     * @return AI模型返回的响应消息
     */
    public String chatWithModel(String userInput) {
        return aiService.chat(userInput);
    }
}