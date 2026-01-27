package com.hzqserver.springai.config;

import com.hzqserver.springai.service.ChatModel;
import com.hzqserver.springai.service.ChatModelProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * AI配置类
 * 支持多种AI服务提供商，包括阿里云百炼、本地部署模型等
 */
@Configuration
public class AiConfig {

    /**
     * 创建默认聊天模型实例
     * 这是一个通用的实现，具体的AI提供商由MultiProviderChatModelProvider处理
     * 
     * @param chatModelProvider 聊天模型提供商
     * @return 默认聊天模型实例
     */
    @Bean
    @Primary
    public ChatModel defaultChatModel(ChatModelProvider chatModelProvider) {
        return new ChatModel() {
            @Override
            public String call(String prompt) {
                // 委托给提供商获取当前活动的模型
                return chatModelProvider.getCurrentChatModel().call(prompt);
            }
        };
    }
}