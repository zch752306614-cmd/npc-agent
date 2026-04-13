package com.npcagent.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.npcagent.mapper.ItemMapper;
import com.npcagent.mapper.PlayerInventoryMapper;
import com.npcagent.mapper.PlayerMapper;
import com.npcagent.model.Item;
import com.npcagent.model.PlayerInventory;
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

    @Autowired
    private PlayerInventoryMapper playerInventoryMapper;

    /**
     * 获取玩家背包
     *
     * @param playerId 玩家ID
     * @return 背包物品列表
     */
    public List<Map<String, Object>> getInventory(String playerId) {
        Player player = getPlayer(playerId);
        if (player == null) {
            return new ArrayList<>();
        }

        initializeStarterInventoryIfAbsent(playerId);
        QueryWrapper<PlayerInventory> inventoryQuery = new QueryWrapper<>();
        inventoryQuery.eq("player_id", playerId);
        List<PlayerInventory> rows = playerInventoryMapper.selectList(inventoryQuery);
        List<Map<String, Object>> inventory = new ArrayList<>();

        for (PlayerInventory row : rows) {
            if (row.getQuantity() == null || row.getQuantity() <= 0) {
                continue;
            }
            Item item = itemMapper.selectById(row.getItemId());
            if (item == null) {
                continue;
            }

            Map<String, Object> itemInfo = new HashMap<>();
            itemInfo.put("id", item.getId());
            itemInfo.put("name", item.getName());
            itemInfo.put("type", item.getType());
            itemInfo.put("quantity", row.getQuantity());
            itemInfo.put("description", item.getDescription());
            itemInfo.put("rarity", item.getRarity());
            inventory.add(itemInfo);
        }

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
        Player player = getPlayer(playerId);
        if (player == null) {
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "玩家不存在");
            return result;
        }

        initializeStarterInventoryIfAbsent(playerId);
        PlayerInventory inventoryRow = findInventoryRow(playerId, itemId);
        int currentCount = inventoryRow == null ? 0 : inventoryRow.getQuantity();
        if (currentCount <= 0 || inventoryRow == null) {
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "背包中没有该物品");
            return result;
        }

        Item item = itemMapper.selectById(itemId);
        if (item == null) {
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "物品不存在");
            return result;
        }

        int remain = currentCount - 1;
        if (remain <= 0) {
            playerInventoryMapper.deleteById(inventoryRow.getId());
        } else {
            inventoryRow.setQuantity(remain);
            playerInventoryMapper.updateById(inventoryRow);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("itemId", itemId);
        result.put("itemName", item.getName());
        result.put("effect", "已消耗 1 个");
        result.put("message", "你使用了 " + item.getName() + "。");

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
        Player player = getPlayer(playerId);
        if (player == null) {
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "玩家不存在");
            return result;
        }

        initializeStarterInventoryIfAbsent(playerId);
        long itemId = item.getId() == null ? -1L : item.getId();
        if (itemId <= 0) {
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "物品ID无效");
            return result;
        }

        upsertInventory(playerId, itemId, 1);
        PlayerInventory row = findInventoryRow(playerId, itemId);

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("itemId", itemId);
        result.put("itemName", item.getName());
        result.put("quantity", row == null ? 0 : row.getQuantity());
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
        Player player = getPlayer(playerId);
        if (player == null) {
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "玩家不存在");
            return result;
        }

        if (quantity <= 0) {
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "数量必须大于0");
            return result;
        }

        initializeStarterInventoryIfAbsent(playerId);
        PlayerInventory inventoryRow = findInventoryRow(playerId, itemId);
        int currentCount = inventoryRow == null ? 0 : inventoryRow.getQuantity();
        if (inventoryRow == null || currentCount < quantity) {
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "背包物品数量不足");
            return result;
        }

        int remain = currentCount - quantity;
        if (remain == 0) {
            playerInventoryMapper.deleteById(inventoryRow.getId());
        } else {
            inventoryRow.setQuantity(remain);
            playerInventoryMapper.updateById(inventoryRow);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("itemId", itemId);
        result.put("quantity", quantity);
        result.put("remain", remain);
        result.put("message", "物品已从背包移除。");

        return result;
    }

    /**
     * 按物品名添加奖励（用于寻宝/战斗掉落）
     */
    public Map<String, Object> addItemByName(String playerId, String itemName, int quantity) {
        if (quantity <= 0) {
            return Map.of("success", false, "message", "数量必须大于0");
        }

        Player player = getPlayer(playerId);
        if (player == null) {
            return Map.of("success", false, "message", "玩家不存在");
        }

        QueryWrapper<Item> query = new QueryWrapper<>();
        query.eq("name", itemName);
        Item item = itemMapper.selectOne(query);
        if (item == null) {
            return Map.of("success", false, "message", "奖励物品不存在: " + itemName);
        }

        initializeStarterInventoryIfAbsent(playerId);
        upsertInventory(playerId, item.getId(), quantity);
        PlayerInventory row = findInventoryRow(playerId, item.getId());

        return Map.of(
                "success", true,
                "itemId", item.getId(),
                "itemName", itemName,
                "quantity", row == null ? quantity : row.getQuantity()
        );
    }

    private Player getPlayer(String playerId) {
        QueryWrapper<Player> playerQuery = new QueryWrapper<>();
        playerQuery.eq("player_id", playerId);
        return playerMapper.selectOne(playerQuery);
    }

    private void initializeStarterInventoryIfAbsent(String playerId) {
        QueryWrapper<PlayerInventory> query = new QueryWrapper<>();
        query.eq("player_id", playerId);
        Long count = playerInventoryMapper.selectCount(query);
        if (count != null && count > 0) {
            return;
        }

        addIfExists(playerId, "灵草", 3);
        addIfExists(playerId, "灵石", 10);
        addIfExists(playerId, "基础丹药", 1);
    }

    private void addIfExists(String playerId, String itemName, int quantity) {
        QueryWrapper<Item> query = new QueryWrapper<>();
        query.eq("name", itemName);
        Item item = itemMapper.selectOne(query);
        if (item != null && item.getId() != null) {
            upsertInventory(playerId, item.getId(), quantity);
        }
    }

    private PlayerInventory findInventoryRow(String playerId, long itemId) {
        QueryWrapper<PlayerInventory> query = new QueryWrapper<>();
        query.eq("player_id", playerId).eq("item_id", itemId);
        return playerInventoryMapper.selectOne(query);
    }

    private void upsertInventory(String playerId, long itemId, int delta) {
        if (delta == 0) {
            return;
        }
        PlayerInventory row = findInventoryRow(playerId, itemId);
        if (row == null) {
            PlayerInventory newRow = new PlayerInventory();
            newRow.setPlayerId(playerId);
            newRow.setItemId(itemId);
            newRow.setQuantity(Math.max(0, delta));
            newRow.setEquipped(false);
            if (newRow.getQuantity() > 0) {
                playerInventoryMapper.insert(newRow);
            }
            return;
        }
        int next = Math.max(0, row.getQuantity() + delta);
        if (next == 0) {
            playerInventoryMapper.deleteById(row.getId());
        } else {
            row.setQuantity(next);
            playerInventoryMapper.updateById(row);
        }
    }
}
