package com.npcagent.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

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
@TableName("story_node")
public class StoryNode {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("node_id")
    private String nodeId;

    @TableField("node_type")
    private String nodeType;

    @TableField("name")
    private String name;

    @TableField("description")
    private String description;

    @TableField("preconditions")
    private String preconditions;

    @TableField("trigger_keywords")
    private String triggerKeywords;

    @TableField("npc_reply_template")
    private String npcReplyTemplate;

    @TableField("next_story_nodes")
    private String nextStoryNodes;

    @TableField("rewards")
    private String rewards;

    @TableField("failure_consequences")
    private String failureConsequences;

    @TableField("semantic_embedding")
    private String semanticEmbedding;

    @TableField("match_threshold")
    private double matchThreshold;

    @TableField("is_unlocked")
    private boolean isUnlocked;

    @TableField("is_completed")
    private boolean isCompleted;

    @TableField("related_npc")
    private String relatedNpc;

    @TableField("related_scene")
    private String relatedScene;
}
