package com.npcagent.config;

import org.springframework.ai.chat.ChatClient;
import org.springframework.ai.ollama.OllamaChatClient;
import org.springframework.ai.ollama.api.OllamaApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Ollama配置类
 *
 * 统一管理Ollama服务配置：
 * 1. LLM对话模型（qwen3.5）
 * 2. Embedding模型（bge-m3）
 *
 * 实现说明：
 * - 使用Ollama API进行AI交互
 * - 支持本地部署的模型
 * - 当服务不可用时使用fallback响应
 */
@Configuration
public class OllamaConfig {

    @Value("${ollama.base-url:http://localhost:11434}")
    private String ollamaBaseUrl;

    @Value("${ollama.chat.model:qwen3.5}")
    private String chatModel;

    @Value("${ollama.embedding.model:bge-m3}")
    private String embeddingModel;

    @Bean
    public OllamaApi ollamaApi() {
        return new OllamaApi(ollamaBaseUrl);
    }

    @Bean
    public ChatClient chatClient(OllamaApi ollamaApi) {
        try {
            return new OllamaChatClient(ollamaApi).withModel(chatModel);
        } catch (Exception e) {
            return null;
        }
    }

    public String getChatModel() {
        return chatModel;
    }

    public String getEmbeddingModel() {
        return embeddingModel;
    }
}
