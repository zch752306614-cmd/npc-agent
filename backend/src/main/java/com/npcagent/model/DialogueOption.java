package com.npcagent.model;

import lombok.Data;

/**
 * 对话选项模型
 *
 * 定义NPC对话中的选项：
 * - 选项ID
 * - 选项文本
 * - 选项类型
 * - 触发的剧情节点
 */
@Data
public class DialogueOption {

    private String optionId;
    private String text;
    private String type; // 选项类型：normal, branch, end
    private String nextNodeId; // 触发的剧情节点
    private boolean isAvailable; // 是否可用
}
