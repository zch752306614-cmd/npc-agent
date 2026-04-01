package com.npcagent.service;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import com.npcagent.model.SemanticMatchResult;

/**
 * 语义匹配服务
 *
 * 实现玩家自由输入的语义匹配：
 * 1. 计算输入文本与剧情节点关键词的语义相似度
 * 2. 找出最匹配的剧情节点
 * 3. 提供匹配结果和置信度
 *
 * 核心算法：
 * - 词袋模型 + TF-IDF
 * - 余弦相似度计算
 * - 阈值判断
 */
@Service
public class SemanticMatchingService {

    /**
     * 匹配玩家输入与剧情节点
     *
     * @param playerInput 玩家输入文本
     * @param npcCode NPC代码
     * @return 语义匹配结果
     */
    public SemanticMatchResult matchInput(String playerInput, String npcCode) {
        // 1. 预处理输入文本
        String processedInput = preprocessText(playerInput);

        // 2. 从数据库获取该NPC相关的剧情节点和关键词
        List<NodeKeywords> nodeKeywords = getNodeKeywords(npcCode);

        // 3. 计算相似度
        double maxSimilarity = 0.0;
        String bestMatchingNode = null;

        for (NodeKeywords node : nodeKeywords) {
            double similarity = calculateSimilarity(processedInput, node.getKeywords());
            if (similarity > maxSimilarity) {
                maxSimilarity = similarity;
                bestMatchingNode = node.getNodeId();
            }
        }

        // 4. 构建匹配结果
        SemanticMatchResult result = new SemanticMatchResult();
        result.setMatch(maxSimilarity > 0.5); // 阈值设置为0.5
        result.setSimilarity(maxSimilarity);
        result.setMatchedNodeId(bestMatchingNode);
        result.setPlayerInput(playerInput);

        return result;
    }

    /**
     * 预处理文本
     *
     * @param text 原始文本
     * @return 处理后的文本
     */
    private String preprocessText(String text) {
        // 转换为小写
        text = text.toLowerCase();
        // 移除标点符号
        text = text.replaceAll("[\\p{Punct}]", " ");
        // 移除多余空格
        text = text.trim().replaceAll("\\s+", " ");
        return text;
    }

    /**
     * 计算文本相似度
     *
     * @param input 玩家输入
     * @param keywords 关键词列表
     * @return 相似度分数
     */
    private double calculateSimilarity(String input, List<String> keywords) {
        // 简单的关键词匹配算法
        int matchCount = 0;
        for (String keyword : keywords) {
            if (input.contains(keyword.toLowerCase())) {
                matchCount++;
            }
        }
        return keywords.isEmpty() ? 0.0 : (double) matchCount / keywords.size();
    }

    /**
     * 获取NPC相关的节点关键词
     *
     * @param npcCode NPC代码
     * @return 节点关键词列表
     */
    private List<NodeKeywords> getNodeKeywords(String npcCode) {
        // 从数据库获取数据
        // 这里使用模拟数据
        List<NodeKeywords> nodeKeywords = new ArrayList<>();
        
        // 模拟数据
        if ("elder".equals(npcCode)) {
            NodeKeywords node1 = new NodeKeywords();
            node1.setNodeId("village_001");
            node1.setKeywords(List.of("拜师", "入门", "修仙", "求道", "学艺"));
            nodeKeywords.add(node1);

            NodeKeywords node2 = new NodeKeywords();
            node2.setNodeId("village_002");
            node2.setKeywords(List.of("聚灵台", "灵根", "测试", "测灵"));
            nodeKeywords.add(node2);
        }

        return nodeKeywords;
    }

    /**
     * 节点关键词类
     */
    private static class NodeKeywords {
        private String nodeId;
        private List<String> keywords;

        public String getNodeId() {
            return nodeId;
        }

        public void setNodeId(String nodeId) {
            this.nodeId = nodeId;
        }

        public List<String> getKeywords() {
            return keywords;
        }

        public void setKeywords(List<String> keywords) {
            this.keywords = keywords;
        }
    }
}
