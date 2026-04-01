package com.npcagent.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
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
 * - 使用Milvus向量数据库进行向量存储和检索
 * - 支持向量的添加、更新和检索
 * - 提供基于语义相似度的匹配功能
 */
@Service
public class VectorStorageService {

    private static final Logger logger = LoggerFactory.getLogger(VectorStorageService.class);

    private final MilvusService milvusService;

    public VectorStorageService(MilvusService milvusService) {
        this.milvusService = milvusService;
        initializeVectors();
    }

    /**
     * 初始化示例向量
     */
    private void initializeVectors() {
        if (!milvusService.isConnected()) {
            logger.warn("Milvus service is not connected, skipping vector initialization");
            return;
        }

        Map<String, String> nodeContents = new HashMap<>();
        nodeContents.put("village_001", "修仙村庄，新手玩家的起点，可以在这里拜师学艺");
        nodeContents.put("village_002", "村庄广场，村民聚集的地方，可以接取任务");
        nodeContents.put("mountain_001", "青云山脉，修仙者的圣地，蕴含丰富的灵气");
        nodeContents.put("mountain_002", "山顶秘境，传说中有仙人留下的宝藏");
        nodeContents.put("cave_001", "神秘洞穴，充满未知的危险和机遇");
        nodeContents.put("cave_002", "洞穴深处，据说藏有上古修仙者的遗物");

        Map<String, double[]> nodeVectors = new HashMap<>();
        nodeVectors.put("village_001", new double[]{0.8, 0.6, 0.2, 0.1, 0.9, 0.3, 0.5, 0.7});
        nodeVectors.put("village_002", new double[]{0.6, 0.8, 0.9, 0.3, 0.2, 0.4, 0.6, 0.8});
        nodeVectors.put("mountain_001", new double[]{0.3, 0.5, 0.7, 0.9, 0.4, 0.6, 0.8, 0.2});
        nodeVectors.put("mountain_002", new double[]{0.5, 0.3, 0.6, 0.8, 0.7, 0.9, 0.1, 0.3});
        nodeVectors.put("cave_001", new double[]{0.9, 0.7, 0.4, 0.2, 0.5, 0.1, 0.3, 0.9});
        nodeVectors.put("cave_002", new double[]{0.7, 0.9, 0.5, 0.6, 0.3, 0.2, 0.4, 0.6});

        for (Map.Entry<String, String> entry : nodeContents.entrySet()) {
            String nodeId = entry.getKey();
            String content = entry.getValue();
            double[] vector = nodeVectors.get(nodeId);
            
            if (vector != null) {
                List<Float> floatVector = convertToFloatList(vector);
                milvusService.insertVector(nodeId, content, floatVector);
            }
        }

        logger.info("Initialized {} vectors in Milvus", nodeContents.size());
    }

    /**
     * 将double数组转换为Float列表
     *
     * @param values double数组
     * @return Float列表
     */
    private List<Float> convertToFloatList(double[] values) {
        return java.util.Arrays.stream(values)
                .mapToObj(d -> (float) d)
                .collect(java.util.stream.Collectors.toList());
    }

    /**
     * 添加向量
     *
     * @param nodeId 节点ID
     * @param content 节点内容
     * @param vector 向量
     */
    public void addVector(String nodeId, String content, List<Float> vector) {
        if (milvusService.isConnected()) {
            milvusService.insertVector(nodeId, content, vector);
            logger.debug("Added vector for node: {}", nodeId);
        } else {
            logger.warn("Milvus service is not connected, cannot add vector");
        }
    }

    /**
     * 查找最相似的向量
     *
     * @param inputVector 输入向量
     * @return 最相似的节点ID和相似度
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
     * 生成输入文本的向量表示
     *
     * @param text 输入文本
     * @return 向量
     */
    public List<Float> generateVector(String text) {
        double[] vector = new double[8];

        Map<String, double[]> keywordWeights = new HashMap<>();
        keywordWeights.put("修仙", new double[]{0.9, 0.2, 0.1, 0.3, 0.8, 0.5, 0.6, 0.4});
        keywordWeights.put("拜师", new double[]{0.8, 0.6, 0.2, 0.1, 0.9, 0.3, 0.7, 0.5});
        keywordWeights.put("灵根", new double[]{0.6, 0.8, 0.9, 0.3, 0.2, 0.4, 0.8, 0.6});
        keywordWeights.put("青云门", new double[]{0.3, 0.5, 0.7, 0.9, 0.4, 0.6, 0.2, 0.8});
        keywordWeights.put("秘境", new double[]{0.9, 0.7, 0.4, 0.2, 0.5, 0.1, 0.9, 0.3});
        keywordWeights.put("村庄", new double[]{0.8, 0.6, 0.2, 0.1, 0.9, 0.3, 0.5, 0.7});
        keywordWeights.put("山脉", new double[]{0.3, 0.5, 0.7, 0.9, 0.4, 0.6, 0.8, 0.2});
        keywordWeights.put("洞穴", new double[]{0.9, 0.7, 0.4, 0.2, 0.5, 0.1, 0.3, 0.9});

        for (Map.Entry<String, double[]> entry : keywordWeights.entrySet()) {
            if (text.contains(entry.getKey())) {
                double[] weights = entry.getValue();
                for (int i = 0; i < vector.length && i < weights.length; i++) {
                    vector[i] += weights[i];
                }
            }
        }

        double norm = 0.0;
        for (double v : vector) {
            norm += v * v;
        }
        norm = Math.sqrt(norm);

        if (norm > 0) {
            for (int i = 0; i < vector.length; i++) {
                vector[i] /= norm;
            }
        }

        return convertToFloatList(vector);
    }
}
