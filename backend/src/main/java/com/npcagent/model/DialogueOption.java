package com.npcagent.model;

import lombok.Data;

/**
 * 对话选项模型
 *
 * 存储玩家可选择的对话选项和对应的回复
 */
@Data
public class DialogueOption {

    /**
     * 选项ID
     */
    private String optionId;

    /**
     * 选项文本
     */
    private String text;

    /**
     * 所属NPC代码
     */
    private String npcCode;

    /**
     * 所属剧情节点ID
     */
    private String nodeId;

    /**
     * 后续节点ID
     */
    private String nextNodeId;

    /**
     * 回复模板（可选）
     */
    private String responseTemplate;

    /**
     * 选项顺序
     */
    private Integer orderIndex;

    /**
     * 是否启用
     */
    private Boolean enabled;
}
