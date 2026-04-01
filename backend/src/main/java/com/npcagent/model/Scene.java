package com.npcagent.model;

import lombok.Data;

import jakarta.persistence.*;
import java.util.List;

/**
 * 场景模型
 *
 * 定义游戏中的各个场景：
 * - 场景基本信息：ID、名称、描述
 * - 场景属性：类型、等级要求、危险度
 * - 场景内容：NPC、资源点、怪物、可交互对象
 * - 场景连接：相邻场景
 */
@Data
@Entity
@Table(name = "scene")
public class Scene {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String sceneCode; // 场景代码（如 village, mountain, cave）

    @Column(nullable = false)
    private String name; // 场景名称

    private String description; // 场景描述

    private String type; // 场景类型：village, mountain, forest, cave, city, sect

    private int levelRequirement; // 等级要求
    private int dangerLevel; // 危险度：1-安全，2-普通，3-危险，4-极危险

    // 场景内容（使用JSON存储）
    @Column(columnDefinition = "TEXT")
    private String npcs; // NPC列表JSON

    @Column(columnDefinition = "TEXT")
    private String resourcePoints; // 资源点列表JSON

    @Column(columnDefinition = "TEXT")
    private String monsters; // 怪物列表JSON

    @Column(columnDefinition = "TEXT")
    private String interactiveObjects; // 可交互对象列表JSON

    // 场景连接
    @Column(columnDefinition = "TEXT")
    private String adjacentScenes; // 相邻场景JSON

    // 场景状态
    private boolean isUnlocked; // 是否解锁
    private String backgroundMusic; // 背景音乐
    private String backgroundImage; // 背景图片
}
