package com.npcagent.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 玩家模型
 *
 * 定义玩家的基本信息和游戏状态：
 * - 基本信息：ID、名称、等级
 * - 修仙属性：修为、境界、灵力
 * - 装备：武器、防具、饰品
 * - 背包：道具列表
 * - 技能：已学习的功法
 * - 剧情进度：已完成的剧情节点
 */
@Data
@TableName("player")
public class Player {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("player_id")
    private String playerId;

    @TableField("name")
    private String name;

    @TableField("level")
    private int level;

    @TableField("cultivation_level")
    private int cultivationLevel;

    @TableField("realm")
    private String realm;

    @TableField("spiritual_power")
    private int spiritualPower;

    @TableField("physical_strength")
    private int physicalStrength;

    @TableField("talent")
    private int talent;

    @TableField("weapon_id")
    private Long weaponId;

    @TableField("armor_id")
    private Long armorId;

    @TableField("accessory_id")
    private Long accessoryId;

    @TableField("inventory")
    private String inventory;

    @TableField("skills")
    private String skills;

    @TableField("completed_nodes")
    private String completedNodes;

    @TableField("current_scene")
    private String currentScene;

    @TableField("position_x")
    private int positionX;

    @TableField("position_y")
    private int positionY;

    @TableField("is_online")
    private boolean isOnline;

    @TableField("last_login_time")
    private long lastLoginTime;
}
