package com.npcagent.model;

import lombok.Data;

/**
 * 语义匹配结果
 *
 * 用于存储玩家自由输入与剧情节点的语义匹配结果：
 * - 匹配的节点ID
 * - 匹配相似度
 * - 是否匹配成功
 * - 玩家输入文本
 */
@Data
public class SemanticMatchResult {
    
    /**
     * 匹配的剧情节点ID
     */
    private String matchedNodeId;
    
    /**
     * 匹配相似度
     */
    private double similarity;
    
    /**
     * 是否匹配成功
     */
    private boolean matched;
    
    /**
     * 匹配成功时的置信度
     */
    private double confidence;
    
    /**
     * 玩家输入文本
     */
    private String playerInput;
    
    /**
     * 设置匹配状态
     * @param match 是否匹配成功
     */
    public void setMatch(boolean match) {
        this.matched = match;
    }
    
    /**
     * 获取匹配状态
     * @return 是否匹配成功
     */
    public boolean isMatch() {
        return this.matched;
    }
    
    /**
     * 设置玩家输入
     * @param playerInput 玩家输入文本
     */
    public void setPlayerInput(String playerInput) {
        this.playerInput = playerInput;
    }
}
