package com.npcagent.model;

import lombok.Data;

import java.util.List;

/**
 * 剧情节点模型
 *
 * 存储剧情内容、触发条件和对话选项
 */
@Data
public class StoryNode {

    /**
     * 节点ID
     */
    private String nodeId;

    /**
     * 节点标题
     */
    private String title;

    /**
     * 节点描述
     */
    private String description;

    /**
     * 剧情内容
     */
    private String content;

    /**
     * 所属NPC代码
     */
    private String npcCode;

    /**
     * 前置节点ID（可选）
     */
    private String prerequisiteNodeId;

    /**
     * 后续节点ID（可选）
     */
    private String nextNodeId;

    /**
     * 对话选项列表
     */
    private List<DialogueOption> dialogueOptions;

    /**
     * 是否为初始节点
     */
    private Boolean isInitial;

    /**
     * 是否启用
     */
    private Boolean enabled;
}
