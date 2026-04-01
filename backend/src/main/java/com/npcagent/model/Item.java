package com.npcagent.model;

import lombok.Data;

import jakarta.persistence.*;

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
@Entity
@Table(name = "item")
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name; // 道具名称

    private String type; // 道具类型：weapon, armor, accessory, pill, material, consumable

    private String description; // 道具描述

    private int rarity; // 稀有度：1-普通，2-优秀，3-稀有，4-史诗，5-传说

    // 装备属性
    private int attack; // 攻击力（武器）
    private int defense; // 防御力（防具）
    private int spiritualPower; // 灵力加成
    private int physicalStrength; // 体魄加成
    private int talent; // 悟性加成

    // 丹药属性
    private int cultivationBoost; // 修为提升
    private int spiritualPowerRecovery; // 灵力恢复

    // 材料属性
    private String materialType; // 材料类型
    private int quality; // 材料品质

    // 消耗品属性
    private String effect; // 效果描述
    private int duration; // 持续时间（秒）

    private int stackSize; // 堆叠数量
    private int maxStackSize; // 最大堆叠数量

    private int value; // 价值（灵石）

    private String icon; // 图标路径
}
