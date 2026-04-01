package com.npcagent.service;

import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealVector;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * 向量存储服务
 *
 * 管理语义向量的存储和相似度计算：
 * 1. 存储剧情节点的语义向量
 * 2. 计算玩家输入与剧情节点的相似度
 * 3. 提供向量检索和匹配功能
 *
 * 实现说明：
 * - 使用内存存储模拟向量数据库
 * - 使用余弦相似度计算语义相似度
 * - 支持向量的添加、更新和检索
 */
@Service
public class VectorStorageService {

    /**
     * 向量存储
     * Key: 节点ID
     * Value: 语义向量
     */
    private final Map<String, RealVector> vectorStorage;

    public VectorStorageService() {
        this.vectorStorage = new HashMap<>();
        // 初始化一些示例向量
        initializeVectors();
    }

    /**
     * 初始化示例向量
     */
    private void initializeVectors() {
        // 为每个剧情节点创建示例向量
        // 实际应用中，这些向量应该通过嵌入模型生成
        
        // 村庄节点向量
        vectorStorage.put("village_001", createVector(new double[]{0.8, 0.6, 0.2, 0.1, 0.9}));
        vectorStorage.put("village_002", createVector(new double[]{0.6, 0.8, 0.9, 0.3, 0.2}));
        
        // 山脉节点向量
        vectorStorage.put("mountain_001", createVector(new double[]{0.3, 0.5, 0.7, 0.9, 0.4}));
        vectorStorage.put("mountain_002", createVector(new double[]{0.5, 0.3, 0.6, 0.8, 0.7}));
        
        // 洞穴节点向量
        vectorStorage.put("cave_001", createVector(new double[]{0.9, 0.7, 0.4, 0.2, 0.5}));
        vectorStorage.put("cave_002", createVector(new double[]{0.7, 0.9, 0.5, 0.6, 0.3}));
    }

    /**
     * 创建向量
     *
     * @param values 向量值
     * @return 向量对象
     */
    private RealVector createVector(double[] values) {
        return new ArrayRealVector(values);
    }

    /**
     * 添加向量
     *
     * @param nodeId 节点ID
     * @param vector 向量
     */
    public void addVector(String nodeId, RealVector vector) {
        vectorStorage.put(nodeId, vector);
    }

    /**
     * 获取向量
     *
     * @param nodeId 节点ID
     * @return 向量
     */
    public RealVector getVector(String nodeId) {
        return vectorStorage.get(nodeId);
    }

    /**
     * 计算两个向量的余弦相似度
     *
     * @param vector1 向量1
     * @param vector2 向量2
     * @return 相似度分数（0-1）
     */
    public double calculateSimilarity(RealVector vector1, RealVector vector2) {
        if (vector1 == null || vector2 == null) {
            return 0.0;
        }
        try {
            return vector1.cosine(vector2);
        } catch (Exception e) {
            return 0.0;
        }
    }

    /**
     * 查找最相似的向量
     *
     * @param inputVector 输入向量
     * @return 最相似的节点ID和相似度
     */
    public Map<String, Object> findMostSimilar(RealVector inputVector) {
        double maxSimilarity = -1.0;
        String mostSimilarNode = null;

        for (Map.Entry<String, RealVector> entry : vectorStorage.entrySet()) {
            double similarity = calculateSimilarity(inputVector, entry.getValue());
            if (similarity > maxSimilarity) {
                maxSimilarity = similarity;
                mostSimilarNode = entry.getKey();
            }
        }

        Map<String, Object> result = new HashMap<>();
        result.put("nodeId", mostSimilarNode);
        result.put("similarity", maxSimilarity);
        return result;
    }

    /**
     * 生成输入文本的向量表示
     *
     * @param text 输入文本
     * @return 向量
     */
    public RealVector generateVector(String text) {
        // 简单的文本向量生成
        // 实际应用中，应该使用预训练的嵌入模型
        
        // 基于关键词的简单向量生成
        double[] vector = new double[5];
        
        // 关键词权重
        Map<String, double[]> keywordWeights = new HashMap<>();
        keywordWeights.put("修仙", new double[]{0.9, 0.2, 0.1, 0.3, 0.8});
        keywordWeights.put("拜师", new double[]{0.8, 0.6, 0.2, 0.1, 0.9});
        keywordWeights.put("灵根", new double[]{0.6, 0.8, 0.9, 0.3, 0.2});
        keywordWeights.put("青云门", new double[]{0.3, 0.5, 0.7, 0.9, 0.4});
        keywordWeights.put("秘境", new double[]{0.9, 0.7, 0.4, 0.2, 0.5});
        
        // 计算向量
        for (Map.Entry<String, double[]> entry : keywordWeights.entrySet()) {
            if (text.contains(entry.getKey())) {
                for (int i = 0; i < vector.length; i++) {
                    vector[i] += entry.getValue()[i];
                }
            }
        }
        
        // 归一化
        double norm = new ArrayRealVector(vector).getNorm();
        if (norm > 0) {
            for (int i = 0; i < vector.length; i++) {
                vector[i] /= norm;
            }
        }
        
        return createVector(vector);
    }
}
