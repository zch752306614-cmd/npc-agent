package com.npcagent.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

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
@TableName("npc")
public class Npc {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("npc_code")
    private String npcCode;

    @TableField("name")
    private String name;

    @TableField("personality")
    private String personality;

    @TableField("backstory")
    private String backstory;

    @TableField("cultivation_level")
    private String cultivationLevel;

    @TableField("realm")
    private String realm;

    @TableField("dialogues")
    private String dialogues;

    @TableField("related_nodes")
    private String relatedNodes;

    @TableField("interaction_type")
    private String interactionType;

    @TableField("function")
    private String function;

    @TableField("current_scene")
    private String currentScene;

    @TableField("position_x")
    private int positionX;

    @TableField("position_y")
    private int positionY;

    @TableField("is_active")
    private boolean isActive;

    @TableField("schedule")
    private String schedule;

    @TableField("appearance")
    private String appearance;

    @TableField("icon")
    private String icon;
}
