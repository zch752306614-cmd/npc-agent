package com.npcagent.model;

import lombok.Data;

import jakarta.persistence.*;

/**
 * NPC模型
 *
 * 定义游戏中的非玩家角色：
 * - 基本信息：ID、名称、代码
 * - 角色属性：性格、背景故事、修为
 * - 对话系统：对话库、剧情关联
 * - 交互行为：可交互类型、功能
 */
@Data
@Entity
@Table(name = "npc")
public class Npc {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String npcCode; // NPC代码（如 elder, sectMaster）

    @Column(nullable = false)
    private String name; // NPC名称

    private String personality; // 性格特点
    private String backstory; // 背景故事
    private String cultivationLevel; // 修为等级
    private String realm; // 境界

    // 对话系统
    @Column(columnDefinition = "TEXT")
    private String dialogues; // 对话库JSON

    @Column(columnDefinition = "TEXT")
    private String relatedNodes; // 关联剧情节点JSON

    // 交互行为
    private String interactionType; // 交互类型：dialogue, trade, quest, training
    private String function; // 功能描述

    // 位置信息
    private String currentScene; // 当前所在场景
    private int positionX; // 场景内X坐标
    private int positionY; // 场景内Y坐标

    // 状态
    private boolean isActive; // 是否激活
    private String schedule; // 活动时间表

    // 外观
    private String appearance; // 外观描述
    private String icon; // 图标路径
}
