package com.npcagent.rag;

import java.util.List;

/**
 * 剧情节点类
 *
 * 代表游戏中的一个关键剧情点，包含：
 * - 节点ID：唯一标识
 * - 节点类型：关键节点、引导节点、结束节点
 * - 前置条件：必须先完成的节点列表
 * - 触发关键词：玩家输入中匹配的关键词列表
 * - NPC回复模板：匹配成功时返回的预设回复
 * - 奖励：完成节点后获得的奖励
 * - 失败后果：未完成前置条件时的后果（可选）
 */
public class StoryNode {
    private final String nodeId;
    private final String nodeType;
    private final List<String> preconditions;
    private final List<String> triggerKeywords;
    private final String storyDescription;
    private final String npcReplyTemplate;
    private final List<String> nextStoryNodes;
    private final List<String> rewards;
    private final List<String> failureConsequences;

    public StoryNode(String nodeId, String nodeType, List<String> preconditions, List<String> triggerKeywords,
                     String storyDescription, String npcReplyTemplate, List<String> nextStoryNodes,
                     List<String> rewards, List<String> failureConsequences) {
        this.nodeId = nodeId;
        this.nodeType = nodeType;
        this.preconditions = preconditions;
        this.triggerKeywords = triggerKeywords;
        this.storyDescription = storyDescription;
        this.npcReplyTemplate = npcReplyTemplate;
        this.nextStoryNodes = nextStoryNodes;
        this.rewards = rewards;
        this.failureConsequences = failureConsequences;
    }

    public String getNodeId() {
        return nodeId;
    }

    public String getNodeType() {
        return nodeType;
    }

    public List<String> getPreconditions() {
        return preconditions;
    }

    public List<String> getTriggerKeywords() {
        return triggerKeywords;
    }

    public String getStoryDescription() {
        return storyDescription;
    }

    public String getNpcReplyTemplate() {
        return npcReplyTemplate;
    }

    public List<String> getNextStoryNodes() {
        return nextStoryNodes;
    }

    public List<String> getRewards() {
        return rewards;
    }

    public List<String> getFailureConsequences() {
        return failureConsequences;
    }
}
