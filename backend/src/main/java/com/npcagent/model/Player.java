package com.npcagent.model;

import lombok.Data;

import jakarta.persistence.*;
import java.util.List;

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
@Entity
@Table(name = "player")
public class Player {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String playerId; // 玩家唯一标识

    @Column(nullable = false)
    private String name; // 玩家名称

    private int level; // 等级

    // 修仙属性
    private int cultivationLevel; // 修为等级
    private String realm; // 境界（炼气、筑基、金丹等）
    private int spiritualPower; // 灵力
    private int physicalStrength; // 体魄
    private int talent; // 悟性

    // 装备
    private Long weaponId; // 武器ID
    private Long armorId; // 防具ID
    private Long accessoryId; // 饰品ID

    // 背包（使用JSON存储，或单独创建背包表）
    @Column(columnDefinition = "TEXT")
    private String inventory; // 背包物品JSON

    // 技能（使用JSON存储，或单独创建技能表）
    @Column(columnDefinition = "TEXT")
    private String skills; // 技能列表JSON

    // 剧情进度
    @Column(columnDefinition = "TEXT")
    private String completedNodes; // 已完成的剧情节点JSON

    // 位置信息
    private String currentScene; // 当前所在场景
    private int positionX; // 场景内X坐标
    private int positionY; // 场景内Y坐标

    // 状态
    private boolean isOnline; // 是否在线
    private long lastLoginTime; // 最后登录时间
}
