package com.hzqserver.springai.service;

/**
 * 聊天模型提供者接口
 * 用于根据不同配置提供相应的聊天模型实现
 */
public interface ChatModelProvider {
    
    /**
     * 获取当前激活的聊天模型
     * @return 当前聊天模型实例
     */
    ChatModel getCurrentChatModel();
}