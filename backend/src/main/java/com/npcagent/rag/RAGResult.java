package com.npcagent.rag;

import lombok.Data;

/**
 * RAG处理结果
 *
 * 存储RAG处理后的回复内容和相关信息
 */
@Data
public class RAGResult {

    /**
     * 回复内容
     */
    private String responseContent;

    /**
     * 回复类型：关键剧情、自由对话
     */
    private String responseType;

    /**
     * 触发的剧情节点ID
     */
    private String triggeredNodeId;

    /**
     * 是否推进剧情
     */
    private boolean storyAdvance;
}
