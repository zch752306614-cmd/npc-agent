package com.npcagent.service;

import com.npcagent.common.exception.BusinessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * 寻宝服务
 *
 * 实现游戏的寻宝系统：
 * 1. 触发寻宝
 * 2. 随机掉落生成
 * 3. 稀有度计算
 * 4. 奖励发放
 */
@Service
public class TreasureHuntService {

    private static final Logger logger = LoggerFactory.getLogger(TreasureHuntService.class);
    private static final double COMMON_THRESHOLD = 0.6;
    private static final double RARE_THRESHOLD = 0.85;
    private static final double EPIC_THRESHOLD = 0.95;
    private static final String REWARD_PREFIX = "获得：";
    private static final String REWARD_SEPARATOR = " x";

    private final Random random = new Random();
    private final InventoryService inventoryService;

    public TreasureHuntService(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    /**
     * 开始寻宝
     *
     * @param playerId 玩家ID
     * @param location 寻宝地点
     * @return 寻宝结果
     */
    public Map<String, Object> startTreasureHunt(String playerId, String location) {
        validateRequest(playerId, location);
        logger.info("Start treasure hunt, playerId={}, location={}", playerId, location);

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("location", location);
        result.put("treasureFound", true);

        String reward = generateRandomReward();
        result.put("reward", reward);
        result.put("rarity", getRarityLevel(reward));
        result.put("message", "你发现了一个宝藏！");

        RewardParsed rewardParsed = parseReward(reward);
        Map<String, Object> grantResult = inventoryService.addItemByName(playerId, rewardParsed.itemName(), rewardParsed.quantity());
        result.put("rewardGranted", Boolean.TRUE.equals(grantResult.get("success")));
        result.put("grantDetail", grantResult);

        return result;
    }

    /**
     * 生成随机奖励
     *
     * @return 奖励描述
     */
    private String generateRandomReward() {
        // 不同稀有度的奖励池
        String[] commonRewards = {
                "获得：灵草 x3",
                "获得：灵石 x10",
                "获得：普通丹药 x2",
                "获得：基础材料 x5"
        };
        
        String[] rareRewards = {
                "获得：优质灵草 x2",
                "获得：灵石 x50",
                "获得：中级丹药 x1",
                "获得：稀有材料 x2"
        };
        
        String[] epicRewards = {
                "获得：极品灵草 x1",
                "获得：灵石 x100",
                "获得：高级丹药 x1",
                "获得：炼器材料 x1"
        };
        
        String[] legendaryRewards = {
                "获得：仙草 x1",
                "获得：灵石 x500",
                "获得：极品丹药 x1",
                "获得：传承之宝 x1"
        };
        
        // 根据概率选择奖励池
        double randomValue = random.nextDouble();
        if (randomValue < COMMON_THRESHOLD) {
            return commonRewards[random.nextInt(commonRewards.length)];
        } else if (randomValue < RARE_THRESHOLD) {
            return rareRewards[random.nextInt(rareRewards.length)];
        } else if (randomValue < EPIC_THRESHOLD) {
            return epicRewards[random.nextInt(epicRewards.length)];
        } else {
            return legendaryRewards[random.nextInt(legendaryRewards.length)];
        }
    }

    /**
     * 获取奖励稀有度
     *
     * @param reward 奖励描述
     * @return 稀有度等级
     */
    private String getRarityLevel(String reward) {
        if (reward.contains("仙草") || reward.contains("传承之宝")) {
            return "传说级";
        } else if (reward.contains("极品") || reward.contains("炼器材料")) {
            return "史诗级";
        } else if (reward.contains("优质") || reward.contains("稀有材料")) {
            return "稀有级";
        } else {
            return "普通级";
        }
    }

    private RewardParsed parseReward(String rewardText) {
        String normalized = rewardText.replace(REWARD_PREFIX, "").trim();
        String[] parts = normalized.split(REWARD_SEPARATOR);
        String itemName = parts.length > 0 ? parts[0].trim() : normalized;
        int quantity = 1;
        if (parts.length > 1) {
            try {
                quantity = Integer.parseInt(parts[1].trim());
            } catch (NumberFormatException ignored) {
                quantity = 1;
            }
        }
        return new RewardParsed(itemName, quantity);
    }

    private void validateRequest(String playerId, String location) {
        if (playerId == null || playerId.isBlank()) {
            throw BusinessException.badRequest("playerId 不能为空");
        }
        if (location == null || location.isBlank()) {
            throw BusinessException.badRequest("location 不能为空");
        }
    }

    private record RewardParsed(String itemName, int quantity) {}
}
