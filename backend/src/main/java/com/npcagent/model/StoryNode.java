package com.npcagent.model;

import lombok.Data;

import jakarta.persistence.*;

/**
 * 剧情节点模型
 *
 * 定义游戏中的剧情节点：
 * - 基本信息：ID、节点代码、名称
 * - 剧情内容：描述、NPC回复
 * - 触发条件：前置节点、触发关键词
 * - 分支选项：后续节点、奖励
 * - 语义匹配：用于AI自由对话触发
 */
@Data
@Entity
@Table(name = "story_node")
public class StoryNode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String nodeId; // 节点代码（如 village_001）

    private String nodeType; // 节点类型：key, guide, end
    private String name; // 节点名称
    private String description; // 节点描述

    // 触发条件
    @Column(columnDefinition = "TEXT")
    private String preconditions; // 前置节点JSON

    @Column(columnDefinition = "TEXT")
    private String triggerKeywords; // 触发关键词JSON

    // 剧情内容
    private String npcReplyTemplate; // NPC回复模板

    // 分支选项
    @Column(columnDefinition = "TEXT")
    private String nextStoryNodes; // 后续节点JSON

    @Column(columnDefinition = "TEXT")
    private String rewards; // 奖励JSON

    @Column(columnDefinition = "TEXT")
    private String failureConsequences; // 失败后果JSON

    // 语义匹配
    @Column(columnDefinition = "TEXT")
    private String semanticEmbedding; // 语义嵌入向量JSON

    private double matchThreshold; // 匹配阈值

    // 状态
    private boolean isUnlocked; // 是否解锁
    private boolean isCompleted; // 是否完成

    // 关联
    private String relatedNpc; // 关联NPC
    private String relatedScene; // 关联场景
}
