package com.npcagent.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 道具模型
 *
 * 定义游戏中的各种物品：
 * - 装备：武器、防具、饰品
 * - 丹药：提升修为、恢复灵力
 * - 材料：用于合成、炼器
 * - 消耗品：各种功能道具
 */
@Data
@TableName("item")
public class Item {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("name")
    private String name;

    @TableField("type")
    private String type;

    @TableField("description")
    private String description;

    @TableField("rarity")
    private int rarity;

    @TableField("attack")
    private int attack;

    @TableField("defense")
    private int defense;

    @TableField("spiritual_power")
    private int spiritualPower;

    @TableField("physical_strength")
    private int physicalStrength;

    @TableField("talent")
    private int talent;

    @TableField("cultivation_boost")
    private int cultivationBoost;

    @TableField("spiritual_power_recovery")
    private int spiritualPowerRecovery;

    @TableField("material_type")
    private String materialType;

    @TableField("quality")
    private int quality;

    @TableField("effect")
    private String effect;

    @TableField("duration")
    private int duration;

    @TableField("stack_size")
    private int stackSize;

    @TableField("max_stack_size")
    private int maxStackSize;

    @TableField("value")
    private int value;

    @TableField("icon")
    private String icon;
}
