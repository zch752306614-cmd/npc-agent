package com.npcagent.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 玩家状态模型
 *
 * 对应数据库表 player_state
 * 用于持久化存储玩家的游戏状态
 */
@Data
@TableName("player_state")
public class PlayerState {

    /**
     * 玩家ID
     */
    @TableId(type = IdType.INPUT)
    private String playerId;

    /**
     * 当前场景
     */
    private String currentScene;

    /**
     * 当前等级
     */
    private Integer currentLevel;

    /**
     * 修为等级
     */
    private Integer cultivationLevel;

    /**
     * 经验值
     */
    private Integer experience;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
}
