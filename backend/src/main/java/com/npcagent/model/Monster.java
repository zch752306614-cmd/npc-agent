package com.npcagent.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 怪物模型
 *
 * 定义游戏中的怪物：
 * - 基本信息：ID、名称、类型
 * - 属性：等级、生命值、攻击力、防御力
 * - 掉落：可能掉落的物品
 * - 行为：AI行为模式
 */
@Data
@TableName("monster")
public class Monster {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("name")
    private String name;

    @TableField("type")
    private String type;

    @TableField("level")
    private int level;

    @TableField("health")
    private int health;

    @TableField("max_health")
    private int maxHealth;

    @TableField("attack")
    private int attack;

    @TableField("defense")
    private int defense;

    @TableField("spiritual_power")
    private int spiritualPower;

    @TableField("drops")
    private String drops;

    @TableField("ai_behavior")
    private String aiBehavior;

    @TableField("aggro_range")
    private int aggroRange;

    @TableField("patrol_range")
    private int patrolRange;

    @TableField("current_scene")
    private String currentScene;

    @TableField("position_x")
    private int positionX;

    @TableField("position_y")
    private int positionY;

    @TableField("is_alive")
    private boolean isAlive;

    @TableField("respawn_time")
    private long respawnTime;

    @TableField("appearance")
    private String appearance;

    @TableField("icon")
    private String icon;
}
