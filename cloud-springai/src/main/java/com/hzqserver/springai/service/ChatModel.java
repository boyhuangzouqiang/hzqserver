package com.hzqserver.springai.service;

/**
 * 聊天模型接口
 * 定义AI聊天模型的基本操作
 */
public interface ChatModel {
    /**
     * 执行聊天调用
     * @param prompt 输入提示
     * @return AI模型响应
     */
    String call(String prompt);
}