package com.hzqserver.springai.controller;

import com.hzqserver.springai.service.ChatAgentService;
import org.springframework.web.bind.annotation.*;

/**
 * 聊天代理控制器
 * 提供聊天对话相关的API接口
 */
@RestController
@RequestMapping("/api/agent")
@CrossOrigin(origins = "*")
public class ChatAgentController {

    private final ChatAgentService chatAgentService;

    /**
     * 构造函数，注入聊天代理服务
     *
     * @param chatAgentService 聊天代理服务实例
     */
    public ChatAgentController(ChatAgentService chatAgentService) {
        this.chatAgentService = chatAgentService;
    }

    /**
     * 发送聊天消息并获取AI回复（POST请求）
     *
     * @param request 包含用户消息的请求对象
     * @return AI模型返回的响应消息
     */
    @PostMapping("/chat")
    public String chat(@RequestBody ChatRequest request) {
        return chatAgentService.chatWithClient(request.getMessage());
    }

    /**
     * 发送简单聊天消息并获取AI回复（GET请求）
     *
     * @param message 用户消息内容
     * @return AI模型返回的响应消息
     */
    @GetMapping("/chat")
    public String chatSimple(@RequestParam String message) {
        return chatAgentService.chatWithClient(message);
    }

    /**
     * 发送高级聊天消息并获取AI回复
     *
     * @param request 包含用户消息的请求对象
     * @return AI模型返回的响应消息
     */
    @PostMapping("/advanced-chat")
    public String advancedChat(@RequestBody ChatRequest request) {
        return chatAgentService.chatWithModel(request.getMessage());
    }

    /**
     * 聊天请求数据传输对象
     */
    public static class ChatRequest {
        private String message;

        /**
         * 获取消息内容
         *
         * @return 消息内容
         */
        public String getMessage() {
            return message;
        }

        /**
         * 设置消息内容
         *
         * @param message 消息内容
         */
        public void setMessage(String message) {
            this.message = message;
        }
    }
}