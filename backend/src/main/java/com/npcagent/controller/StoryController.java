package com.npcagent.controller;

import com.npcagent.model.StoryData;
import com.npcagent.service.DialogueManager;
import com.npcagent.rag.PlayerState;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 故事控制器
 *
 * 本控制器提供RESTful API接口，处理前端的所有请求：
 * 1. 获取场景信息：返回游戏中的所有场景数据
 * 2. 获取NPC对话：返回指定NPC的对话选项
 * 3. NPC对话交互：处理玩家与NPC的对话，返回NPC回复
 * 4. 获取玩家状态：返回当前玩家的游戏进度和状态
 *
 * API设计说明：
 * - 所有接口都以/api为前缀
 * - 支持跨域访问（CORS）
 * - 使用JSON格式进行数据传输
 * - 路径参数使用驼峰命名法（npcCode）
 */
@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class StoryController {

    /**
     * 剧情数据对象
     * 包含所有场景、NPC、对话等静态数据
     */
    private final StoryData storyData;

    /**
     * 对话管理器
     * 处理所有与NPC对话相关的业务逻辑
     */
    private final DialogueManager dialogueManager;

    /**
     * 构造函数
     *
     * @param storyData 剧情数据对象
     * @param dialogueManager 对话管理器
     */
    public StoryController(StoryData storyData, DialogueManager dialogueManager) {
        this.storyData = storyData;
        this.dialogueManager = dialogueManager;
    }

    /**
     * 获取所有场景信息
     *
     * 返回游戏中的所有场景数据，包括：
     * - 场景名称
     * - 场景描述
     * - 场景中的NPC列表
     *
     * @return 场景集合对象
     */
    @GetMapping("/scenes")
    public StoryData.Scenes getScenes() {
        return storyData.getScenes();
    }

    /**
     * 获取指定NPC的对话列表
     *
     * 根据NPC代码返回该NPC的所有对话选项
     * 用于游戏界面显示NPC的初始对话
     *
     * @param npcCode NPC代码（如 "elder", "villagerA" 等）
     * @return 对话列表
     */
    @GetMapping("/npcs/{npcCode}/dialogues")
    public List<StoryData.Dialogue> getNpcDialogues(@PathVariable String npcCode) {
        return dialogueManager.getNpcDialogues(npcCode);
    }

    /**
     * 与NPC进行对话
     *
     * 核心API接口，处理玩家与NPC的对话交互：
     * 1. 接收玩家输入和对话历史
     * 2. 调用对话管理器生成NPC回复
     * 3. 返回NPC的回复内容
     *
     * 请求体格式：
     * {
     *   "input": "玩家输入的文本",
     *   "history": [
     *     {"speaker": "player", "text": "玩家说的话"},
     *     {"speaker": "elder", "text": "NPC说的话"}
     *   ],
     *   "playerId": "玩家唯一标识（可选，默认为'default'）"
     * }
     *
     * AI处理流程：
     * - 首先尝试匹配剧情节点（规则匹配）
     * - 如果匹配失败，调用Ollama API生成AI回复
     * - AI回复基于角色设定、对话历史和游戏规则
     *
     * @param npcCode NPC代码
     * @param data 请求体数据
     * @return 包含NPC回复的Map对象
     */
    @PostMapping("/npcs/{npcCode}/chat")
    public Map<String, String> chatWithNpc(@PathVariable String npcCode, @RequestBody Map<String, Object> data) {
        // 提取玩家输入
        String playerInput = (String) data.get("input");

        // 提取对话历史
        @SuppressWarnings("unchecked")
        List<Map<String, String>> dialogueHistory = (List<Map<String, String>>) data.get("history");

        // 提取玩家ID，默认为"default"
        String playerId = (String) data.getOrDefault("playerId", "default");

        // 调用对话管理器生成回复
        String response = dialogueManager.generateResponse(npcCode, playerInput, dialogueHistory, playerId);

        // 返回JSON格式的回复
        return Map.of("response", response);
    }

    /**
     * 获取玩家状态
     *
     * 返回指定玩家的游戏进度和状态：
     * - 已完成的剧情节点列表
     * - 玩家背包（获得的物品）
     * - 当前所在场景
     *
     * 用于前端显示玩家的游戏进度和状态
     *
     * @param playerId 玩家唯一标识（默认为"default"）
     * @return 玩家状态对象
     */
    @GetMapping("/player/state")
    public PlayerState getPlayerState(@RequestParam(defaultValue = "default") String playerId) {
        return dialogueManager.getPlayerState(playerId);
    }
}
