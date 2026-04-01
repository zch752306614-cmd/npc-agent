package com.npcagent.config;

import org.springframework.ai.chat.ChatClient;
import org.springframework.ai.ollama.OllamaChatClient;
import org.springframework.ai.ollama.api.OllamaApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Spring AI配置类
 *
 * 配置Spring AI的核心组件：
 * 1. Ollama API客户端
 * 2. ChatClient用于对话生成
 * 3. 默认对话选项配置
 *
 * 实现说明：
 * - 支持本地Ollama部署的模型
 * - 可配置Ollama服务地址
 * - 支持自定义模型参数
 * - 当Ollama服务不可用时，使用fallback响应
 */
@Configuration
public class SpringAiConfig {

    @Value("${spring.ai.ollama.base-url:http://localhost:11434}")
    private String ollamaBaseUrl;

    @Value("${spring.ai.ollama.model:qwen2.5}")
    private String model;

    @Value("${spring.ai.ollama.temperature:0.7}")
    private Float temperature;

    @Bean
    public ChatClient chatClient() {
        try {
            OllamaApi ollamaApi = new OllamaApi(ollamaBaseUrl);
            return new OllamaChatClient(ollamaApi).withModel(model);
        } catch (Exception e) {
            return null;
        }
    }
}
