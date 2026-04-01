package com.npcagent.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

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
@TableName("scene")
public class Scene {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("scene_code")
    private String sceneCode;

    @TableField("name")
    private String name;

    @TableField("description")
    private String description;

    @TableField("type")
    private String type;

    @TableField("level_requirement")
    private int levelRequirement;

    @TableField("danger_level")
    private int dangerLevel;

    @TableField("npcs")
    private String npcs;

    @TableField("resource_points")
    private String resourcePoints;

    @TableField("monsters")
    private String monsters;

    @TableField("interactive_objects")
    private String interactiveObjects;

    @TableField("adjacent_scenes")
    private String adjacentScenes;

    @TableField("is_unlocked")
    private boolean isUnlocked;

    @TableField("background_music")
    private String backgroundMusic;

    @TableField("background_image")
    private String backgroundImage;
}
