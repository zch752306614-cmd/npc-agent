package com.npcagent.controller;

import com.npcagent.model.DialogueOption;
import com.npcagent.model.DialogueResult;
import com.npcagent.model.SemanticMatchResult;
import com.npcagent.service.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 游戏控制器
 *
 * 提供完整的游戏RESTful API接口：
 * 1. 对话系统接口
 * 2. 探索系统接口
 * 3. 战斗系统接口
 * 4. 寻宝系统接口
 * 5. 背包系统接口
 * 6. 剧情系统接口
 */
@RestController
@RequestMapping("/api/game")
@CrossOrigin(origins = "*")
public class GameController {

    private final DialogueService dialogueService;
    private final ExplorationService explorationService;
    private final CombatService combatService;
    private final TreasureHuntService treasureHuntService;
    private final InventoryService inventoryService;
    private final StoryNodeService storyNodeService;

    public GameController(DialogueService dialogueService, ExplorationService explorationService,
                         CombatService combatService, TreasureHuntService treasureHuntService,
                         InventoryService inventoryService, StoryNodeService storyNodeService) {
        this.dialogueService = dialogueService;
        this.explorationService = explorationService;
        this.combatService = combatService;
        this.treasureHuntService = treasureHuntService;
        this.inventoryService = inventoryService;
        this.storyNodeService = storyNodeService;
    }

    // ==================== 对话系统接口 ====================

    /**
     * 获取NPC对话选项
     *
     * @param npcCode NPC代码
     * @param playerId 玩家ID
     * @return 对话选项列表
     */
    @GetMapping("/dialogue/options")
    public List<DialogueOption> getDialogueOptions(@RequestParam String npcCode, @RequestParam String playerId) {
        return dialogueService.getNpcDialogueOptions(npcCode, playerId);
    }

    /**
     * 处理固定选项选择
     *
     * @param npcCode NPC代码
     * @param optionId 选项ID
     * @param playerId 玩家ID
     * @return 对话结果
     */
    @PostMapping("/dialogue/fixed")
    public DialogueResult handleFixedOption(@RequestParam String npcCode, @RequestParam String optionId, @RequestParam String playerId) {
        return dialogueService.handleFixedOption(npcCode, optionId, playerId);
    }

    /**
     * 处理自由输入
     *
     * @param data 请求数据
     * @return 对话结果
     */
    @PostMapping("/dialogue/free")
    public DialogueResult handleFreeInput(@RequestBody Map<String, Object> data) {
        String npcCode = (String) data.get("npcCode");
        String playerInput = (String) data.get("input");
        List<Map<String, String>> history = (List<Map<String, String>>) data.get("history");
        String playerId = (String) data.get("playerId");

        // 转换对话历史格式
        List<com.npcagent.rag.DialogueHistory> dialogueHistory = new java.util.ArrayList<>();
        for (Map<String, String> msg : history) {
            com.npcagent.rag.DialogueHistory dh = new com.npcagent.rag.DialogueHistory();
            dh.setSpeaker(msg.get("speaker"));
            if ("player".equals(msg.get("speaker"))) {
                dh.setPlayerInput(msg.get("text"));
            } else {
                dh.setNpcResponse(msg.get("text"));
            }
            dialogueHistory.add(dh);
        }

        return dialogueService.handleFreeInput(npcCode, playerInput, dialogueHistory, playerId);
    }

    /**
     * 获取对话历史
     *
     * @param playerId 玩家ID
     * @param npcCode NPC代码
     * @return 对话历史
     */
    @GetMapping("/dialogue/history")
    public List<com.npcagent.rag.DialogueHistory> getDialogueHistory(@RequestParam String playerId, @RequestParam String npcCode) {
        return dialogueService.getDialogueHistory(playerId, npcCode);
    }

    // ==================== 探索系统接口 ====================

    /**
     * 切换场景
     *
     * @param playerId 玩家ID
     * @param sceneCode 场景代码
     * @return 切换结果
     */
    @PostMapping("/exploration/change-scene")
    public Map<String, Object> changeScene(@RequestParam String playerId, @RequestParam String sceneCode) {
        return explorationService.changeScene(playerId, sceneCode);
    }

    /**
     * 探索场景
     *
     * @param data 请求数据
     * @return 探索结果
     */
    @PostMapping("/exploration/explore")
    public Map<String, Object> exploreScene(@RequestBody Map<String, Object> data) {
        String playerId = (String) data.get("playerId");
        String sceneCode = (String) data.get("sceneCode");
        int positionX = (int) data.get("positionX");
        int positionY = (int) data.get("positionY");

        return explorationService.exploreScene(playerId, sceneCode, positionX, positionY);
    }

    // ==================== 战斗系统接口 ====================

    /**
     * 开始战斗
     *
     * @param playerId 玩家ID
     * @param monsterId 怪物ID
     * @return 战斗初始化信息
     */
    @PostMapping("/combat/start")
    public Map<String, Object> startCombat(@RequestParam String playerId, @RequestParam String monsterId) {
        return combatService.startCombat(playerId, monsterId);
    }

    /**
     * 执行战斗回合
     *
     * @param data 请求数据
     * @return 战斗结果
     */
    @PostMapping("/combat/turn")
    public Map<String, Object> executeTurn(@RequestBody Map<String, Object> data) {
        String battleId = (String) data.get("battleId");
        String action = (String) data.get("action");

        return combatService.executeTurn(battleId, action);
    }

    /**
     * 结束战斗
     *
     * @param battleId 战斗ID
     * @param winner 胜利者
     * @return 战斗结算结果
     */
    @PostMapping("/combat/end")
    public Map<String, Object> endCombat(@RequestParam String battleId, @RequestParam String winner) {
        return combatService.endCombat(battleId, winner);
    }

    // ==================== 寻宝系统接口 ====================

    /**
     * 开始寻宝
     *
     * @param playerId 玩家ID
     * @param location 寻宝地点
     * @return 寻宝结果
     */
    @PostMapping("/treasure-hunt/start")
    public Map<String, Object> startTreasureHunt(@RequestParam String playerId, @RequestParam String location) {
        return treasureHuntService.startTreasureHunt(playerId, location);
    }

    // ==================== 背包系统接口 ====================

    /**
     * 获取背包
     *
     * @param playerId 玩家ID
     * @return 背包物品列表
     */
    @GetMapping("/inventory")
    public List<Map<String, Object>> getInventory(@RequestParam String playerId) {
        return inventoryService.getInventory(playerId);
    }

    /**
     * 使用物品
     *
     * @param playerId 玩家ID
     * @param itemId 物品ID
     * @return 使用结果
     */
    @PostMapping("/inventory/use")
    public Map<String, Object> useItem(@RequestParam String playerId, @RequestParam long itemId) {
        return inventoryService.useItem(playerId, itemId);
    }

    /**
     * 添加物品
     *
     * @param playerId 玩家ID
     * @param item 物品信息
     * @return 添加结果
     */
    @PostMapping("/inventory/add")
    public Map<String, Object> addItem(@RequestParam String playerId, @RequestBody com.npcagent.model.Item item) {
        return inventoryService.addItem(playerId, item);
    }

    /**
     * 移除物品
     *
     * @param playerId 玩家ID
     * @param itemId 物品ID
     * @param quantity 数量
     * @return 移除结果
     */
    @PostMapping("/inventory/remove")
    public Map<String, Object> removeItem(@RequestParam String playerId, @RequestParam long itemId, @RequestParam int quantity) {
        return inventoryService.removeItem(playerId, itemId, quantity);
    }

    // ==================== 剧情系统接口 ====================

    /**
     * 获取剧情进度
     *
     * @param playerId 玩家ID
     * @return 剧情进度信息
     */
    /*
    @GetMapping("/story/progress")
    public Map<String, Object> getStoryProgress(@RequestParam String playerId) {
        return storyNodeService.getStoryProgress(playerId);
    }
    */
}
