package com.hzqserver.springai.controller;

import com.hzqserver.springai.agent.TaskExecutionAgent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 任务代理控制器
 * 提供各种AI任务处理相关的API接口，包括文本摘要、情感分析、问答和翻译
 */
@RestController
@RequestMapping("/api/task-agent")
@CrossOrigin(origins = "*")
public class TaskAgentController {

    private final TaskExecutionAgent taskExecutionAgent;

    /**
     * 构造函数，注入任务执行代理服务
     *
     * @param taskExecutionAgent 任务执行代理服务实例
     */
    public TaskAgentController(TaskExecutionAgent taskExecutionAgent) {
        this.taskExecutionAgent = taskExecutionAgent;
    }

    /**
     * 对文本进行摘要处理
     *
     * @param request 包含待摘要文本的请求对象
     * @return 摘要后的文本结果
     */
    @PostMapping("/summarize")
    public String summarize(@RequestBody TextRequest request) {
        return taskExecutionAgent.summarizeText(request.getText());
    }

    /**
     * 对文本进行情感分析
     *
     * @param request 包含待分析文本的请求对象
     * @return 情感分析结果（正面、负面或中性）
     */
    @PostMapping("/sentiment")
    public String analyzeSentiment(@RequestBody TextRequest request) {
        return taskExecutionAgent.analyzeSentiment(request.getText());
    }

    /**
     * 回答指定问题
     *
     * @param request 包含问题和上下文的请求对象
     * @return 问题的答案
     */
    @PostMapping("/answer-question")
    public String answerQuestion(@RequestBody QuestionRequest request) {
        return taskExecutionAgent.answerQuestion(request.getQuestion(), request.getContext());
    }

    /**
     * 翻译指定文本到目标语言
     *
     * @param request 包含待翻译文本和目标语言的请求对象
     * @return 翻译后的文本
     */
    @PostMapping("/translate")
    public String translate(@RequestBody TranslateRequest request) {
        return taskExecutionAgent.translateText(request.getText(), request.getTargetLanguage());
    }

    /**
     * 文本请求数据传输对象
     */
    public static class TextRequest {
        private String text;

        /**
         * 获取文本内容
         *
         * @return 文本内容
         */
        public String getText() {
            return text;
        }

        /**
         * 设置文本内容
         *
         * @param text 文本内容
         */
        public void setText(String text) {
            this.text = text;
        }
    }

    /**
     * 问题请求数据传输对象
     */
    public static class QuestionRequest {
        private String question;
        private String context;

        /**
         * 获取问题内容
         *
         * @return 问题内容
         */
        public String getQuestion() {
            return question;
        }

        /**
         * 设置问题内容
         *
         * @param question 问题内容
         */
        public void setQuestion(String question) {
            this.question = question;
        }

        /**
         * 获取上下文内容
         *
         * @return 上下文内容
         */
        public String getContext() {
            return context;
        }

        /**
         * 设置上下文内容
         *
         * @param context 上下文内容
         */
        public void setContext(String context) {
            this.context = context;
        }
    }

    /**
     * 翻译请求数据传输对象
     */
    public static class TranslateRequest {
        private String text;
        private String targetLanguage;

        /**
         * 获取待翻译文本内容
         *
         * @return 待翻译文本内容
         */
        public String getText() {
            return text;
        }

        /**
         * 设置待翻译文本内容
         *
         * @param text 待翻译文本内容
         */
        public void setText(String text) {
            this.text = text;
        }

        /**
         * 获取目标语言
         *
         * @return 目标语言
         */
        public String getTargetLanguage() {
            return targetLanguage;
        }

        /**
         * 设置目标语言
         *
         * @param targetLanguage 目标语言
         */
        public void setTargetLanguage(String targetLanguage) {
            this.targetLanguage = targetLanguage;
        }
    }
}