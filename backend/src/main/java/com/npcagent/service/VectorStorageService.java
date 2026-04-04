package com.npcagent.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.util.*;

/**
 * 向量存储服务
 *
 * 提供语义向量的存储和检索功能：
 * 1. 使用EmbeddingService生成向量
 * 2. 使用Milvus存储和检索向量
 * 3. 计算向量相似度
 *
 * 实现说明：
 * - 使用bge-m3模型生成768维向量
 * - 使用Milvus进行高效的向量相似度搜索
 * - 支持余弦相似度计算
 */
@Service
public class VectorStorageService {

    private static final Logger logger = LoggerFactory.getLogger(VectorStorageService.class);

    private final MilvusService milvusService;
    private final EmbeddingService embeddingService;

    public VectorStorageService(MilvusService milvusService, EmbeddingService embeddingService) {
        this.milvusService = milvusService;
        this.embeddingService = embeddingService;
    }

    @PostConstruct
    public void init() {
        initializeVectors();
    }

    /**
     * 初始化向量存储
     *
     * 将预设的剧情节点文本通过bge-m3生成向量并存入Milvus
     */
    private void initializeVectors() {
        Map<String, String> storyNodes = new HashMap<>();
        storyNodes.put("village_001", "初入修仙世界，遇到神秘老者");
        storyNodes.put("village_002", "老者传授基础修炼心法");
        storyNodes.put("village_003", "发现村庄异常，调查灵草失窃");
        storyNodes.put("sect_001", "加入青云宗，成为外门弟子");
        storyNodes.put("sect_002", "参加宗门大比，展现实力");
        storyNodes.put("cave_001", "探索上古洞府，寻找机缘");

        int count = 0;
        for (Map.Entry<String, String> entry : storyNodes.entrySet()) {
            String nodeId = entry.getKey();
            String content = entry.getValue();

            List<Float> vector = embeddingService.embed(content);
            if (vector != null && !vector.isEmpty()) {
                milvusService.insertVector(nodeId, content, vector);
                count++;
            }
        }

        logger.info("Initialized {} vectors in Milvus", count);
    }

    /**
     * 添加向量
     *
     * @param nodeId 节点ID
     * @param content 文本内容
     */
    public void addVector(String nodeId, String content) {
        List<Float> vector = embeddingService.embed(content);
        if (vector != null && !vector.isEmpty()) {
            milvusService.insertVector(nodeId, content, vector);
            logger.debug("Added vector for node: {}", nodeId);
        } else {
            logger.warn("Failed to generate embedding for node: {}", nodeId);
        }
    }

    /**
     * 查找最相似的向量
     *
     * @param inputText 输入文本
     * @return 最相似的结果
     */
    public Map<String, Object> findMostSimilar(String inputText) {
        List<Float> inputVector = embeddingService.embed(inputText);

        if (inputVector == null || inputVector.isEmpty()) {
            logger.warn("Failed to generate embedding for input: {}", inputText);
            Map<String, Object> result = new HashMap<>();
            result.put("nodeId", null);
            result.put("similarity", 0.0);
            return result;
        }

        return findMostSimilar(inputVector);
    }

    /**
     * 查找最相似的向量
     *
     * @param inputVector 输入向量
     * @return 最相似的结果
     */
    public Map<String, Object> findMostSimilar(List<Float> inputVector) {
        if (!milvusService.isConnected()) {
            logger.warn("Milvus service is not connected, returning empty result");
            Map<String, Object> result = new HashMap<>();
            result.put("nodeId", null);
            result.put("similarity", 0.0);
            return result;
        }

        List<Map<String, Object>> searchResults = milvusService.searchSimilarVectors(inputVector, 1);

        if (searchResults.isEmpty()) {
            Map<String, Object> result = new HashMap<>();
            result.put("nodeId", null);
            result.put("similarity", 0.0);
            return result;
        }

        Map<String, Object> firstResult = searchResults.get(0);
        Map<String, Object> result = new HashMap<>();
        result.put("nodeId", firstResult.get("nodeId"));
        result.put("similarity", firstResult.get("score"));
        result.put("content", firstResult.get("content"));
        return result;
    }

    /**
     * 计算两个向量的余弦相似度
     *
     * @param vectorA 向量A
     * @param vectorB 向量B
     * @return 余弦相似度
     */
    public double calculateCosineSimilarity(List<Float> vectorA, List<Float> vectorB) {
        if (vectorA == null || vectorB == null || vectorA.size() != vectorB.size()) {
            return 0.0;
        }

        double dotProduct = 0.0;
        double normA = 0.0;
        double normB = 0.0;

        for (int i = 0; i < vectorA.size(); i++) {
            dotProduct += vectorA.get(i) * vectorB.get(i);
            normA += Math.pow(vectorA.get(i), 2);
            normB += Math.pow(vectorB.get(i), 2);
        }

        if (normA == 0.0 || normB == 0.0) {
            return 0.0;
        }

        return dotProduct / (Math.sqrt(normA) * Math.sqrt(normB));
    }

    /**
     * 搜索相似向量 - 返回多个结果
     *
     * @param inputText 输入文本
     * @param topK 返回结果数量
     * @return 相似结果列表
     */
    public List<Map<String, Object>> searchSimilar(String inputText, int topK) {
        List<Float> inputVector = embeddingService.embed(inputText);

        if (inputVector == null || inputVector.isEmpty()) {
            logger.warn("Failed to generate embedding for input: {}", inputText);
            return Collections.emptyList();
        }

        return searchSimilar(inputVector, topK);
    }

    /**
     * 搜索相似向量
     *
     * @param inputVector 输入向量
     * @param topK 返回结果数量
     * @return 相似结果列表
     */
    public List<Map<String, Object>> searchSimilar(List<Float> inputVector, int topK) {
        if (!milvusService.isConnected()) {
            logger.warn("Milvus service is not connected, returning empty result");
            return Collections.emptyList();
        }

        List<Map<String, Object>> searchResults = milvusService.searchSimilarVectors(inputVector, topK);
        List<Map<String, Object>> results = new ArrayList<>();

        for (Map<String, Object> searchResult : searchResults) {
            Map<String, Object> result = new HashMap<>();
            result.put("nodeId", searchResult.get("nodeId"));
            result.put("similarity", searchResult.get("score"));
            result.put("content", searchResult.get("content"));
            results.add(result);
        }

        return results;
    }

    /**
     * 检查服务是否可用
     *
     * @return true if available
     */
    public boolean isAvailable() {
        return embeddingService.isAvailable() && milvusService.isConnected();
    }
}
