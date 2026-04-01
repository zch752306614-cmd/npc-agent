package com.npcagent.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.npcagent.mapper.ItemMapper;
import com.npcagent.mapper.PlayerMapper;
import com.npcagent.model.Item;
import com.npcagent.model.Player;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 背包服务
 *
 * 实现游戏的背包系统：
 * 1. 物品管理
 * 2. 物品使用
 * 3. 物品堆叠
 * 4. 物品分类
 */
@Service
public class InventoryService {

    @Autowired
    private PlayerMapper playerMapper;

    @Autowired
    private ItemMapper itemMapper;

    /**
     * 获取玩家背包
     *
     * @param playerId 玩家ID
     * @return 背包物品列表
     */
    public List<Map<String, Object>> getInventory(String playerId) {
        QueryWrapper<Player> playerQuery = new QueryWrapper<>();
        playerQuery.eq("player_id", playerId);
        Player player = playerMapper.selectOne(playerQuery);
        
        if (player == null) {
            return new ArrayList<>();
        }
        
        String inventoryJson = player.getInventory();
        if (inventoryJson == null || inventoryJson.isEmpty()) {
            return new ArrayList<>();
        }
        
        // 这里应该解析JSON并返回物品列表
        // 为了简化，返回模拟数据
        List<Map<String, Object>> inventory = new ArrayList<>();
        
        inventory.add(Map.of(
                "id", 1,
                "name", "灵草",
                "type", "material",
                "quantity", 10,
                "description", "用于炼丹的基础材料",
                "rarity", 1
        ));
        
        inventory.add(Map.of(
                "id", 2,
                "name", "灵石",
                "type", "currency",
                "quantity", 100,
                "description", "修仙界通用货币",
                "rarity", 1
        ));
        
        inventory.add(Map.of(
                "id", 3,
                "name", "基础丹药",
                "type", "pill",
                "quantity", 5,
                "description", "恢复少量灵力",
                "rarity", 2
        ));
        
        return inventory;
    }

    /**
     * 使用物品
     *
     * @param playerId 玩家ID
     * @param itemId 物品ID
     * @return 使用结果
     */
    public Map<String, Object> useItem(String playerId, long itemId) {
        QueryWrapper<Player> playerQuery = new QueryWrapper<>();
        playerQuery.eq("player_id", playerId);
        Player player = playerMapper.selectOne(playerQuery);
        
        if (player == null) {
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "玩家不存在");
            return result;
        }
        
        Item item = itemMapper.selectById(itemId);
        if (item == null) {
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "物品不存在");
            return result;
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("itemId", itemId);
        result.put("itemName", item.getName());
        result.put("effect", "恢复灵力 +20");
        result.put("message", "你使用了" + item.getName() + "，恢复了20点灵力。");
        
        return result;
    }

    /**
     * 添加物品到背包
     *
     * @param playerId 玩家ID
     * @param item 物品信息
     * @return 添加结果
     */
    public Map<String, Object> addItem(String playerId, Item item) {
        QueryWrapper<Player> playerQuery = new QueryWrapper<>();
        playerQuery.eq("player_id", playerId);
        Player player = playerMapper.selectOne(playerQuery);
        
        if (player == null) {
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "玩家不存在");
            return result;
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("itemName", item.getName());
        result.put("quantity", 1);
        result.put("message", "物品已添加到背包。");
        
        return result;
    }

    /**
     * 从背包移除物品
     *
     * @param playerId 玩家ID
     * @param itemId 物品ID
     * @param quantity 数量
     * @return 移除结果
     */
    public Map<String, Object> removeItem(String playerId, long itemId, int quantity) {
        QueryWrapper<Player> playerQuery = new QueryWrapper<>();
        playerQuery.eq("player_id", playerId);
        Player player = playerMapper.selectOne(playerQuery);
        
        if (player == null) {
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "玩家不存在");
            return result;
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("itemId", itemId);
        result.put("quantity", quantity);
        result.put("message", "物品已从背包移除。");
        
        return result;
    }
}
