package com.npcagent.model;

import lombok.Data;

import java.util.List;

/**
 * 对话结果模型
 *
 * 定义对话处理的结果：
 * - NPC回复内容
 * - 后续对话选项
 * - 触发的剧情节点
 * - 获得的奖励
 * - 对话类型
 */
@Data
public class DialogueResult {

    private String npcResponse; // NPC回复内容
    private List<DialogueOption> nextOptions; // 后续对话选项
    private String triggeredNodeId; // 触发的剧情节点
    private List<String> rewards; // 获得的奖励
    private String dialogueType; // 对话类型：fixed, free, semantic
    private boolean isStoryAdvance; // 是否推进剧情
    private String nextScene; // 可能的场景切换
}
