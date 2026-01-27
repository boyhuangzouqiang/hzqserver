package com.hzqserver.springai.service;

import org.springframework.stereotype.Service;

/**
 * AI服务接口
 * 提供统一的AI服务调用接口，支持多种AI提供商
 */
@Service
public class AiService {

    private final ChatModelProvider chatModelProvider;

    public AiService(ChatModelProvider chatModelProvider) {
        this.chatModelProvider = chatModelProvider;
    }

    /**
     * 与AI模型进行对话
     * @param prompt 用户输入的提示
     * @return AI模型返回的响应
     */
    public String chat(String prompt) {
        return chatModelProvider.getCurrentChatModel().call(prompt);
    }
}