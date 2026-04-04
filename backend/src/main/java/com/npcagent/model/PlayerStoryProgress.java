package com.npcagent.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 玩家剧情进度模型
 *
 * 对应数据库表 player_story_progress
 * 用于记录玩家在各个剧情节点的完成状态
 */
@Data
@TableName("player_story_progress")
public class PlayerStoryProgress {

    /**
     * 进度ID
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 玩家ID
     */
    private String playerId;

    /**
     * 剧情节点ID
     */
    private String nodeId;

    /**
     * 是否已完成
     */
    private Boolean completed;

    /**
     * 是否已解锁
     */
    private Boolean unlocked;

    /**
     * 完成时间
     */
    private LocalDateTime completedAt;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
}
