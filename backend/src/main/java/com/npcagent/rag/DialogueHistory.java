package com.npcagent.rag;

import lombok.Data;

/**
 * 对话历史
 *
 * 存储玩家和NPC之间的对话历史
 */
@Data
public class DialogueHistory {

    /**
     * 玩家输入
     */
    private String playerInput;

    /**
     * NPC回复
     */
    private String npcResponse;

    /**
     * 对话时间戳
     */
    private long timestamp;

    /**
     * 说话者：player或npc
     */
    private String speaker;

    /**
     * 触发的剧情节点ID
     */
    private String triggeredNodeId;
}
