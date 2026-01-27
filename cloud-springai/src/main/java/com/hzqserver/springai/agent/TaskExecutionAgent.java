package com.hzqserver.springai.agent;

import com.hzqserver.springai.service.AiService;
import org.springframework.stereotype.Component;

/**
 * 任务执行代理类
 * 提供各种基于AI模型的任务处理功能，包括文本摘要、情感分析、问答和翻译
 */
@Component
public class TaskExecutionAgent {

    private final AiService aiService;

    /**
     * 构造函数，注入AI服务实例
     * @param aiService AI服务实例
     */
    public TaskExecutionAgent(AiService aiService) {
        this.aiService = aiService;
    }

    /**
     * 执行文本摘要任务
     * @param textToSummarize 需要进行摘要的文本
     * @return 摘要后的文本结果
     */
    public String summarizeText(String textToSummarize) {
        String prompt = String.format("请对以下文本进行简洁准确的摘要：%s", textToSummarize);
        return aiService.chat(prompt);
    }

    /**
     * 执行情感分析任务
     * @param textToAnalyze 需要进行情感分析的文本
     * @return 情感分析结果（正面、负面或中性）
     */
    public String analyzeSentiment(String textToAnalyze) {
        String prompt = String.format("请分析以下文本的情感倾向（正面、负面或中性）：%s", textToAnalyze);
        return aiService.chat(prompt);
    }

    /**
     * 执行问答任务
     * @param question 问题内容
     * @param context 参考上下文（可选）
     * @return 问题的答案
     */
    public String answerQuestion(String question, String context) {
        String prompt;
        if (context != null && !context.isEmpty()) {
            prompt = String.format("基于以下上下文回答问题：%s\n\n问题：%s", context, question);
        } else {
            prompt = String.format("请回答以下问题：%s", question);
        }
        return aiService.chat(prompt);
    }

    /**
     * 执行翻译任务
     * @param textToTranslate 需要翻译的文本
     * @param targetLanguage 目标语言
     * @return 翻译后的文本
     */
    public String translateText(String textToTranslate, String targetLanguage) {
        String prompt = String.format("请将以下文本翻译成%s：%s", targetLanguage, textToTranslate);
        return aiService.chat(prompt);
    }
}