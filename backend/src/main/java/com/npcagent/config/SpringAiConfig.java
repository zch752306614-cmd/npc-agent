package com.npcagent.config;

import org.springframework.ai.chat.ChatClient;
import org.springframework.ai.openai.OpenAiChatClient;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Spring AI配置类
 *
 * 配置Spring AI的核心组件：
 * 1. OpenAI API客户端
 * 2. ChatClient用于对话生成
 * 3. 默认对话选项配置
 *
 * 实现说明：
 * - 支持OpenAI API
 * - 可配置API Key和基础URL
 * - 支持自定义模型参数
 * - 当API Key未配置时，不创建相关Bean
 */
@Configuration
public class SpringAiConfig {

    @Value("${spring.ai.openai.api-key:}")
    private String apiKey;

    @Value("${spring.ai.openai.base-url:https://api.openai.com}")
    private String baseUrl;

    @Value("${spring.ai.openai.model:gpt-3.5-turbo}")
    private String model;

    @Value("${spring.ai.openai.temperature:0.7}")
    private Float temperature;

    @Value("${spring.ai.openai.max-tokens:500}")
    private Integer maxTokens;

    @Bean
    public ChatClient chatClient() {
        if (apiKey == null || apiKey.isEmpty()) {
            return null;
        }

        OpenAiApi openAiApi = new OpenAiApi(baseUrl, apiKey);
        
        OpenAiChatOptions options = OpenAiChatOptions.builder()
                .withModel(model)
                .withTemperature(temperature)
                .withMaxTokens(maxTokens)
                .build();

        return new OpenAiChatClient(openAiApi, options);
    }
}
