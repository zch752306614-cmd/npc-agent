package com.npcagent.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Embedding服务
 *
 * 提供文本向量化功能，使用Ollama部署的bge-m3模型：
 * 1. 将文本转换为向量（Embedding）
 * 2. 支持批量文本向量化
 * 3. 用于语义匹配和相似度计算
 *
 * 实现说明：
 * - 调用Ollama的API进行文本嵌入
 * - 使用bge-m3模型生成768维向量
 * - 支持错误处理，失败时返回空向量并由上层降级
 */
@Service
public class EmbeddingService {

    private static final Logger logger = LoggerFactory.getLogger(EmbeddingService.class);

    @Value("${ollama.base-url:http://localhost:11434}")
    private String ollamaBaseUrl;

    @Value("${ollama.embedding.model:bge-m3}")
    private String embeddingModel;

    private final RestTemplate restTemplate;

    public EmbeddingService() {
        this.restTemplate = new RestTemplate();
    }

    /**
     * 将单个文本转换为向量
     *
     * @param text 输入文本
     * @return 向量列表（768维）
     */
    public List<Float> embed(String text) {
        try {
            String url = ollamaBaseUrl + "/api/embeddings";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            Map<String, String> requestBody = new HashMap<>();
            requestBody.put("model", embeddingModel);
            requestBody.put("prompt", text);

            HttpEntity<Map<String, String>> request = new HttpEntity<>(requestBody, headers);

            @SuppressWarnings("unchecked")
            Map<String, Object> response = (Map<String, Object>) restTemplate.postForObject(url, request, Map.class);

            if (response != null && response.containsKey("embedding")) {
                @SuppressWarnings("unchecked")
                List<Double> embedding = (List<Double>) response.get("embedding");
                logger.debug("Successfully generated embedding for text: {}", text.substring(0, Math.min(50, text.length())));
                return convertToFloatList(embedding);
            } else {
                logger.error("Failed to get embedding from Ollama: {}", response);
                return List.of();
            }
        } catch (Exception e) {
            logger.error("Error generating embedding: {}", e.getMessage());
            return List.of();
        }
    }

    /**
     * 批量将文本转换为向量
     *
     * @param texts 文本列表
     * @return 向量列表
     */
    public List<List<Float>> embedBatch(List<String> texts) {
        return texts.stream()
                .map(this::embed)
                .toList();
    }

    /**
     * 检查Embedding服务是否可用
     *
     * @return true if available
     */
    public boolean isAvailable() {
        try {
            String url = ollamaBaseUrl + "/api/tags";
            restTemplate.getForObject(url, String.class);
            return true;
        } catch (Exception e) {
            logger.warn("Ollama service is not available: {}", e.getMessage());
            return false;
        }
    }

    /**
     * 将Double列表转换为Float列表
     */
    private List<Float> convertToFloatList(List<Double> doubleList) {
        return doubleList.stream()
                .map(Double::floatValue)
                .toList();
    }

}
