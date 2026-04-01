package com.npcagent.model;

import lombok.Data;

import jakarta.persistence.*;

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
@Entity
@Table(name = "monster")
public class Monster {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name; // 怪物名称

    private String type; // 怪物类型：beast, demon, spirit, undead
    private int level; // 怪物等级

    // 属性
    private int health; // 生命值
    private int maxHealth; // 最大生命值
    private int attack; // 攻击力
    private int defense; // 防御力
    private int spiritualPower; // 灵力

    // 掉落
    @Column(columnDefinition = "TEXT")
    private String drops; // 掉落物品JSON

    // 行为
    private String aiBehavior; // AI行为模式
    private int aggroRange; // 仇恨范围
    private int patrolRange; // 巡逻范围

    // 位置
    private String currentScene; // 当前所在场景
    private int positionX; // 场景内X坐标
    private int positionY; // 场景内Y坐标

    // 状态
    private boolean isAlive; // 是否存活
    private long respawnTime; // 刷新时间

    // 外观
    private String appearance; // 外观描述
    private String icon; // 图标路径
}
