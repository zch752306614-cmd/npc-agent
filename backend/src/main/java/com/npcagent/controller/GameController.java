package com.npcagent.controller;

import com.npcagent.common.Result;
import com.npcagent.common.exception.BusinessException;
import com.npcagent.api.dto.CombatTurnRequest;
import com.npcagent.api.dto.ExploreSceneRequest;
import com.npcagent.api.dto.FreeDialogueRequest;
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

    @GetMapping("/dialogue/options")
    public Result<List<DialogueOption>> getDialogueOptions(
            @RequestParam String npcCode,
            @RequestParam String playerId
    ) {
        requireNotBlank(npcCode, "npcCode 不能为空");
        requireNotBlank(playerId, "playerId 不能为空");
        return Result.success(dialogueService.getNpcDialogueOptions(npcCode, playerId));
    }

    @PostMapping("/dialogue/fixed")
    public Result<DialogueResult> handleFixedOption(
            @RequestParam String npcCode,
            @RequestParam String optionId,
            @RequestParam String playerId
    ) {
        requireNotBlank(npcCode, "npcCode 不能为空");
        requireNotBlank(optionId, "optionId 不能为空");
        requireNotBlank(playerId, "playerId 不能为空");
        return Result.success(dialogueService.handleFixedOption(npcCode, optionId, playerId));
    }

    @PostMapping("/dialogue/free")
    public Result<DialogueResult> handleFreeInput(@RequestBody FreeDialogueRequest request) {
        validateFreeDialogueRequest(request);
        return Result.success(dialogueService.handleFreeInput(request));
    }

    @GetMapping("/dialogue/history")
    public Result<List<com.npcagent.rag.DialogueHistory>> getDialogueHistory(
            @RequestParam String playerId,
            @RequestParam String npcCode
    ) {
        requireNotBlank(playerId, "playerId 不能为空");
        requireNotBlank(npcCode, "npcCode 不能为空");
        return Result.success(dialogueService.getDialogueHistory(playerId, npcCode));
    }

    @PostMapping("/exploration/change-scene")
    public Result<Map<String, Object>> changeScene(
            @RequestParam String playerId,
            @RequestParam String sceneCode
    ) {
        requireNotBlank(playerId, "playerId 不能为空");
        requireNotBlank(sceneCode, "sceneCode 不能为空");
        return Result.success(explorationService.changeScene(playerId, sceneCode));
    }

    @PostMapping("/exploration/explore")
    public Result<Map<String, Object>> exploreScene(@RequestBody ExploreSceneRequest request) {
        validateExploreRequest(request);
        return Result.success(explorationService.exploreScene(
                request.getPlayerId(),
                request.getSceneCode(),
                defaultInt(request.getPositionX()),
                defaultInt(request.getPositionY())
        ));
    }

    @PostMapping("/combat/start")
    public Result<Map<String, Object>> startCombat(
            @RequestParam String playerId,
            @RequestParam String monsterId
    ) {
        requireNotBlank(playerId, "playerId 不能为空");
        requireNotBlank(monsterId, "monsterId 不能为空");
        return Result.success(combatService.startCombat(playerId, monsterId));
    }

    @PostMapping("/combat/turn")
    public Result<Map<String, Object>> executeTurn(@RequestBody CombatTurnRequest request) {
        validateCombatTurnRequest(request);
        return Result.success(combatService.executeTurn(request.getBattleId(), request.getAction()));
    }

    @PostMapping("/combat/end")
    public Result<Map<String, Object>> endCombat(
            @RequestParam String battleId,
            @RequestParam String winner
    ) {
        requireNotBlank(battleId, "battleId 不能为空");
        requireNotBlank(winner, "winner 不能为空");
        return Result.success(combatService.endCombat(battleId, winner));
    }

    @PostMapping("/treasure-hunt/start")
    public Result<Map<String, Object>> startTreasureHunt(
            @RequestParam String playerId,
            @RequestParam String location
    ) {
        requireNotBlank(playerId, "playerId 不能为空");
        requireNotBlank(location, "location 不能为空");
        return Result.success(treasureHuntService.startTreasureHunt(playerId, location));
    }

    @GetMapping("/inventory")
    public Result<List<Map<String, Object>>> getInventory(@RequestParam String playerId) {
        requireNotBlank(playerId, "playerId 不能为空");
        return Result.success(inventoryService.getInventory(playerId));
    }

    @PostMapping("/inventory/use")
    public Result<Map<String, Object>> useItem(
            @RequestParam String playerId,
            @RequestParam long itemId
    ) {
        requireNotBlank(playerId, "playerId 不能为空");
        requirePositive(itemId, "itemId 必须大于0");
        return Result.success(inventoryService.useItem(playerId, itemId));
    }

    @PostMapping("/inventory/add")
    public Result<Map<String, Object>> addItem(
            @RequestParam String playerId,
            @RequestBody com.npcagent.model.Item item
    ) {
        requireNotBlank(playerId, "playerId 不能为空");
        return Result.success(inventoryService.addItem(playerId, item));
    }

    @PostMapping("/inventory/remove")
    public Result<Map<String, Object>> removeItem(
            @RequestParam String playerId,
            @RequestParam long itemId,
            @RequestParam int quantity
    ) {
        requireNotBlank(playerId, "playerId 不能为空");
        requirePositive(itemId, "itemId 必须大于0");
        requirePositive(quantity, "quantity 必须大于0");
        return Result.success(inventoryService.removeItem(playerId, itemId, quantity));
    }

    @GetMapping("/story/progress")
    public Result<Map<String, Object>> getStoryProgress(@RequestParam String playerId) {
        requireNotBlank(playerId, "playerId 不能为空");
        return Result.success(storyNodeService.getStoryProgress(playerId));
    }

    private void validateFreeDialogueRequest(FreeDialogueRequest request) {
        if (request == null) {
            throw BusinessException.badRequest("请求体不能为空");
        }
        requireNotBlank(request.getNpcCode(), "npcCode 不能为空");
        requireNotBlank(request.getInput(), "input 不能为空");
        requireNotBlank(request.getPlayerId(), "playerId 不能为空");
    }

    private void validateExploreRequest(ExploreSceneRequest request) {
        if (request == null) {
            throw BusinessException.badRequest("请求体不能为空");
        }
        requireNotBlank(request.getPlayerId(), "playerId 不能为空");
        requireNotBlank(request.getSceneCode(), "sceneCode 不能为空");
    }

    private void validateCombatTurnRequest(CombatTurnRequest request) {
        if (request == null) {
            throw BusinessException.badRequest("请求体不能为空");
        }
        requireNotBlank(request.getBattleId(), "battleId 不能为空");
        requireNotBlank(request.getAction(), "action 不能为空");
    }

    private void requireNotBlank(String value, String message) {
        if (value == null || value.isBlank()) {
            throw BusinessException.badRequest(message);
        }
    }

    private void requirePositive(long value, String message) {
        if (value <= 0) {
            throw BusinessException.badRequest(message);
        }
    }

    private void requirePositive(int value, String message) {
        if (value <= 0) {
            throw BusinessException.badRequest(message);
        }
    }

    private int defaultInt(Integer value) {
        return value == null ? 0 : value;
    }
}
