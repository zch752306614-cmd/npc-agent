package com.npcagent.service;

import org.springframework.stereotype.Service;

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

    private final Random random = new Random();

    /**
     * 开始寻宝
     *
     * @param playerId 玩家ID
     * @param location 寻宝地点
     * @return 寻宝结果
     */
    public Map<String, Object> startTreasureHunt(String playerId, String location) {
        // 检查寻宝地点
        // 生成随机掉落
        // 计算稀有度
        // 发放奖励
        
        Map<String, Object> result = new java.util.HashMap<>();
        result.put("success", true);
        result.put("location", location);
        result.put("treasureFound", true);
        
        // 生成随机掉落
        String reward = generateRandomReward();
        result.put("reward", reward);
        result.put("rarity", getRarityLevel(reward));
        result.put("message", "你发现了一个宝藏！");
        
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
        if (randomValue < 0.6) {
            return commonRewards[random.nextInt(commonRewards.length)];
        } else if (randomValue < 0.85) {
            return rareRewards[random.nextInt(rareRewards.length)];
        } else if (randomValue < 0.95) {
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
}
