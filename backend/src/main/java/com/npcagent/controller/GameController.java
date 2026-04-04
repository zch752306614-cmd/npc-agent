package com.npcagent.controller;

import com.npcagent.common.Result;
import com.npcagent.model.DialogueOption;
import com.npcagent.model.DialogueResult;
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
 *
 * 返回格式统一使用Result包装：
 * {
 *   "code": 200,
 *   "message": "success",
 *   "data": {},
 *   "timestamp": "2024-01-01T12:00:00"
 * }
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
     * @return 统一返回结果，包含对话选项列表
     */
    @GetMapping("/dialogue/options")
    public Result<List<DialogueOption>> getDialogueOptions(@RequestParam String npcCode, @RequestParam String playerId) {
        try {
            List<DialogueOption> options = dialogueService.getNpcDialogueOptions(npcCode, playerId);
            return Result.success(options);
        } catch (Exception e) {
            return Result.error("获取对话选项失败: " + e.getMessage());
        }
    }

    /**
     * 处理固定选项选择
     *
     * @param npcCode NPC代码
     * @param optionId 选项ID
     * @param playerId 玩家ID
     * @return 统一返回结果，包含对话结果
     */
    @PostMapping("/dialogue/fixed")
    public Result<DialogueResult> handleFixedOption(@RequestParam String npcCode, @RequestParam String optionId, @RequestParam String playerId) {
        try {
            DialogueResult result = dialogueService.handleFixedOption(npcCode, optionId, playerId);
            return Result.success(result);
        } catch (Exception e) {
            return Result.error("处理对话选项失败: " + e.getMessage());
        }
    }

    /**
     * 处理自由输入
     *
     * @param data 请求数据
     * @return 统一返回结果，包含对话结果
     */
    @PostMapping("/dialogue/free")
    public Result<DialogueResult> handleFreeInput(@RequestBody Map<String, Object> data) {
        try {
            String npcCode = (String) data.get("npcCode");
            String playerInput = (String) data.get("input");
            List<Map<String, String>> history = (List<Map<String, String>>) data.get("history");
            String playerId = (String) data.get("playerId");

            if (npcCode == null || playerInput == null || playerId == null) {
                return Result.badRequest("缺少必要参数: npcCode, input, playerId");
            }

            // 转换对话历史格式
            List<com.npcagent.rag.DialogueHistory> dialogueHistory = new java.util.ArrayList<>();
            if (history != null) {
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
            }

            DialogueResult result = dialogueService.handleFreeInput(npcCode, playerInput, dialogueHistory, playerId);
            return Result.success(result);
        } catch (Exception e) {
            return Result.error("处理自由输入失败: " + e.getMessage());
        }
    }

    /**
     * 获取对话历史
     *
     * @param playerId 玩家ID
     * @param npcCode NPC代码
     * @return 统一返回结果，包含对话历史
     */
    @GetMapping("/dialogue/history")
    public Result<List<com.npcagent.rag.DialogueHistory>> getDialogueHistory(@RequestParam String playerId, @RequestParam String npcCode) {
        try {
            List<com.npcagent.rag.DialogueHistory> history = dialogueService.getDialogueHistory(playerId, npcCode);
            return Result.success(history);
        } catch (Exception e) {
            return Result.error("获取对话历史失败: " + e.getMessage());
        }
    }

    // ==================== 探索系统接口 ====================

    /**
     * 切换场景
     *
     * @param playerId 玩家ID
     * @param sceneCode 场景代码
     * @return 统一返回结果，包含切换结果
     */
    @PostMapping("/exploration/change-scene")
    public Result<Map<String, Object>> changeScene(@RequestParam String playerId, @RequestParam String sceneCode) {
        try {
            Map<String, Object> result = explorationService.changeScene(playerId, sceneCode);
            return Result.success(result);
        } catch (Exception e) {
            return Result.error("切换场景失败: " + e.getMessage());
        }
    }

    /**
     * 探索场景
     *
     * @param data 请求数据
     * @return 统一返回结果，包含探索结果
     */
    @PostMapping("/exploration/explore")
    public Result<Map<String, Object>> exploreScene(@RequestBody Map<String, Object> data) {
        try {
            String playerId = (String) data.get("playerId");
            String sceneCode = (String) data.get("sceneCode");
            int positionX = (int) data.getOrDefault("positionX", 0);
            int positionY = (int) data.getOrDefault("positionY", 0);

            if (playerId == null || sceneCode == null) {
                return Result.badRequest("缺少必要参数: playerId, sceneCode");
            }

            Map<String, Object> result = explorationService.exploreScene(playerId, sceneCode, positionX, positionY);
            return Result.success(result);
        } catch (Exception e) {
            return Result.error("探索场景失败: " + e.getMessage());
        }
    }

    // ==================== 战斗系统接口 ====================

    /**
     * 开始战斗
     *
     * @param playerId 玩家ID
     * @param monsterId 怪物ID
     * @return 统一返回结果，包含战斗初始化信息
     */
    @PostMapping("/combat/start")
    public Result<Map<String, Object>> startCombat(@RequestParam String playerId, @RequestParam String monsterId) {
        try {
            Map<String, Object> result = combatService.startCombat(playerId, monsterId);
            return Result.success(result);
        } catch (Exception e) {
            return Result.error("开始战斗失败: " + e.getMessage());
        }
    }

    /**
     * 执行战斗回合
     *
     * @param data 请求数据
     * @return 统一返回结果，包含战斗结果
     */
    @PostMapping("/combat/turn")
    public Result<Map<String, Object>> executeTurn(@RequestBody Map<String, Object> data) {
        try {
            String battleId = (String) data.get("battleId");
            String action = (String) data.get("action");

            if (battleId == null || action == null) {
                return Result.badRequest("缺少必要参数: battleId, action");
            }

            Map<String, Object> result = combatService.executeTurn(battleId, action);
            return Result.success(result);
        } catch (Exception e) {
            return Result.error("执行战斗回合失败: " + e.getMessage());
        }
    }

    /**
     * 结束战斗
     *
     * @param battleId 战斗ID
     * @param winner 胜利者
     * @return 统一返回结果，包含战斗结算结果
     */
    @PostMapping("/combat/end")
    public Result<Map<String, Object>> endCombat(@RequestParam String battleId, @RequestParam String winner) {
        try {
            Map<String, Object> result = combatService.endCombat(battleId, winner);
            return Result.success(result);
        } catch (Exception e) {
            return Result.error("结束战斗失败: " + e.getMessage());
        }
    }

    // ==================== 寻宝系统接口 ====================

    /**
     * 开始寻宝
     *
     * @param playerId 玩家ID
     * @param location 寻宝地点
     * @return 统一返回结果，包含寻宝结果
     */
    @PostMapping("/treasure-hunt/start")
    public Result<Map<String, Object>> startTreasureHunt(@RequestParam String playerId, @RequestParam String location) {
        try {
            Map<String, Object> result = treasureHuntService.startTreasureHunt(playerId, location);
            return Result.success(result);
        } catch (Exception e) {
            return Result.error("开始寻宝失败: " + e.getMessage());
        }
    }

    // ==================== 背包系统接口 ====================

    /**
     * 获取背包
     *
     * @param playerId 玩家ID
     * @return 统一返回结果，包含背包物品列表
     */
    @GetMapping("/inventory")
    public Result<List<Map<String, Object>>> getInventory(@RequestParam String playerId) {
        try {
            List<Map<String, Object>> inventory = inventoryService.getInventory(playerId);
            return Result.success(inventory);
        } catch (Exception e) {
            return Result.error("获取背包失败: " + e.getMessage());
        }
    }

    /**
     * 使用物品
     *
     * @param playerId 玩家ID
     * @param itemId 物品ID
     * @return 统一返回结果，包含使用结果
     */
    @PostMapping("/inventory/use")
    public Result<Map<String, Object>> useItem(@RequestParam String playerId, @RequestParam long itemId) {
        try {
            Map<String, Object> result = inventoryService.useItem(playerId, itemId);
            return Result.success(result);
        } catch (Exception e) {
            return Result.error("使用物品失败: " + e.getMessage());
        }
    }

    /**
     * 添加物品
     *
     * @param playerId 玩家ID
     * @param item 物品信息
     * @return 统一返回结果，包含添加结果
     */
    @PostMapping("/inventory/add")
    public Result<Map<String, Object>> addItem(@RequestParam String playerId, @RequestBody com.npcagent.model.Item item) {
        try {
            Map<String, Object> result = inventoryService.addItem(playerId, item);
            return Result.success(result);
        } catch (Exception e) {
            return Result.error("添加物品失败: " + e.getMessage());
        }
    }

    /**
     * 移除物品
     *
     * @param playerId 玩家ID
     * @param itemId 物品ID
     * @param quantity 数量
     * @return 统一返回结果，包含移除结果
     */
    @PostMapping("/inventory/remove")
    public Result<Map<String, Object>> removeItem(@RequestParam String playerId, @RequestParam long itemId, @RequestParam int quantity) {
        try {
            Map<String, Object> result = inventoryService.removeItem(playerId, itemId, quantity);
            return Result.success(result);
        } catch (Exception e) {
            return Result.error("移除物品失败: " + e.getMessage());
        }
    }

    // ==================== 剧情系统接口 ====================

    /**
     * 获取剧情进度
     *
     * @param playerId 玩家ID
     * @return 统一返回结果，包含剧情进度信息
     */
    /*
    @GetMapping("/story/progress")
    public Result<Map<String, Object>> getStoryProgress(@RequestParam String playerId) {
        try {
            Map<String, Object> result = storyNodeService.getStoryProgress(playerId);
            return Result.success(result);
        } catch (Exception e) {
            return Result.error("获取剧情进度失败: " + e.getMessage());
        }
    }
    */
}
