package com.npcagent.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;

/**
 * 剧情数据模型
 *
 * 本类定义了游戏的完整剧情数据结构，包括：
 * 1. 场景（Scenes）：游戏中的各个地点
 * 2. NPC（Npcs）：游戏中的非玩家角色
 * 3. 剧情弧（StoryArcs）：游戏的主线和支线任务
 *
 * 数据加载：
 * - 数据从story-data.json文件加载
 * - 使用Jackson进行JSON反序列化
 * - 通过StoryDataConfig配置类加载到Spring容器中
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StoryData {

    /**
     * 场景集合
     * 包含游戏中所有的场景（村庄、山脉、洞穴等）
     */
    private Scenes scenes;

    /**
     * NPC集合
     * 包含游戏中所有的非玩家角色
     */
    private Npcs npcs;

    /**
     * 剧情弧列表
     * 定义游戏的主线和支线任务流程
     */
    private List<StoryArc> storyArcs;

    /**
     * 场景集合类
     *
     * 包含游戏中的所有场景，每个场景都是一个Scene对象
     */
    @Data
    public static class Scenes {
        private Scene village;   // 青牛镇
        private Scene mountain;  // 青云山
        private Scene cave;      // 神秘洞穴
    }

    /**
     * 场景类
     *
     * 定义单个场景的详细信息：
     * - name: 场景名称
     * - description: 场景描述
     * - npcs: 该场景中的NPC列表
     */
    @Data
    public static class Scene {
        private String name;
        private String description;
        private List<String> npcs;
    }

    /**
     * NPC集合类
     *
     * 包含游戏中所有的NPC，每个NPC都是一个Npc对象
     * 使用驼峰命名法，与JSON数据中的字段名对应
     */
    @Data
    public static class Npcs {
        private Npc elder;           // 老者
        private Npc villagerA;       // 村民甲
        private Npc pharmacyOwner;   // 药铺老板
        private Npc sectMaster;      // 掌门（青云子）
        private Npc seniorBrother;   // 师兄
        private Npc seniorSister;    // 师姐
        private Npc mysteriousElder; // 神秘老人
    }

    /**
     * NPC类
     *
     * 定义单个NPC的详细信息：
     * - name: NPC名称
     * - personality: 性格特点
     * - backstory: 背景故事
     * - dialogues: 对话列表
     */
    @Data
    public static class Npc {
        private String name;
        private String personality;
        private String backstory;
        private List<Dialogue> dialogues;
    }

    /**
     * 对话类
     *
     * 定义NPC的单条对话：
     * - id: 对话ID
     * - text: 对话文本
     * - options: 玩家可选的回复选项
     */
    @Data
    public static class Dialogue {
        private int id;
        private String text;
        private List<String> options;
    }

    /**
     * 剧情弧类
     *
     * 定义游戏的一个完整剧情线：
     * - id: 剧情弧ID
     * - name: 剧情弧名称
     * - description: 剧情描述
     * - scenes: 涉及的场景列表
     * - tasks: 任务列表
     */
    @Data
    public static class StoryArc {
        private int id;
        private String name;
        private String description;
        private List<String> scenes;
        private List<String> tasks;
    }
}
