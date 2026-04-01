package com.npcagent.rag;

import java.util.List;

/**
 * RAG处理结果类
 *
 * 封装对话处理的结果：
 * - responseType: 回复类型（"关键剧情"或"自由对话"）
 * - responseContent: 回复内容（剧情节点时为预设回复，自由对话时为null，由AI生成）
 * - nodeId: 触发的剧情节点ID（如果是关键剧情）
 * - rewards: 完成节点获得的奖励列表
 * - characterSetting: 角色设定（自由对话时使用）
 */
public class RAGResult {
    private String responseType;
    private String responseContent;
    private String nodeId;
    private List<String> rewards;
    private CharacterSetting characterSetting;

    public String getResponseType() {
        return responseType;
    }

    public void setResponseType(String responseType) {
        this.responseType = responseType;
    }

    public String getResponseContent() {
        return responseContent;
    }

    public void setResponseContent(String responseContent) {
        this.responseContent = responseContent;
    }

    public String getNodeId() {
        return nodeId;
    }

    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }

    public List<String> getRewards() {
        return rewards;
    }

    public void setRewards(List<String> rewards) {
        this.rewards = rewards;
    }

    public CharacterSetting getCharacterSetting() {
        return characterSetting;
    }

    public void setCharacterSetting(CharacterSetting characterSetting) {
        this.characterSetting = characterSetting;
    }
}
