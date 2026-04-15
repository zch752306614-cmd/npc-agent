package com.npcagent.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.npcagent.common.exception.BusinessException;
import com.npcagent.mapper.ItemMapper;
import com.npcagent.mapper.PlayerInventoryMapper;
import com.npcagent.mapper.PlayerMapper;
import com.npcagent.model.Item;
import com.npcagent.model.PlayerInventory;
import com.npcagent.model.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger logger = LoggerFactory.getLogger(InventoryService.class);
    private static final int STARTER_HERB_COUNT = 3;
    private static final int STARTER_STONE_COUNT = 10;
    private static final int STARTER_PILL_COUNT = 1;

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
        requireExistingPlayer(playerId);
        logger.info("Load inventory, playerId={}", playerId);

        initializeStarterInventoryIfAbsent(playerId);
        List<PlayerInventory> rows = getPlayerInventoryRows(playerId);
        return buildInventoryResponse(rows);
    }

    private List<Map<String, Object>> buildInventoryResponse(List<PlayerInventory> rows) {
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
        requireExistingPlayer(playerId);
        logger.info("Use item, playerId={}, itemId={}", playerId, itemId);

        initializeStarterInventoryIfAbsent(playerId);
        PlayerInventory inventoryRow = findInventoryRow(playerId, itemId);
        if (inventoryRow == null || inventoryRow.getQuantity() == null || inventoryRow.getQuantity() <= 0) {
            throw BusinessException.notFound("背包中没有该物品");
        }

        Item item = itemMapper.selectById(itemId);
        if (item == null) {
            throw BusinessException.notFound("物品不存在");
        }

        int remain = inventoryRow.getQuantity() - 1;
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
        requireExistingPlayer(playerId);
        if (item == null) {
            throw BusinessException.badRequest("item 不能为空");
        }
        logger.info("Add item, playerId={}, itemId={}, itemName={}", playerId, item.getId(), item.getName());

        initializeStarterInventoryIfAbsent(playerId);
        long itemId = item.getId() == null ? -1L : item.getId();
        if (itemId <= 0) {
            throw BusinessException.badRequest("物品ID无效");
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
        requireExistingPlayer(playerId);
        logger.info("Remove item, playerId={}, itemId={}, quantity={}", playerId, itemId, quantity);

        if (quantity <= 0) {
            throw BusinessException.badRequest("数量必须大于0");
        }

        initializeStarterInventoryIfAbsent(playerId);
        PlayerInventory inventoryRow = findInventoryRow(playerId, itemId);
        int currentCount = inventoryRow == null ? 0 : inventoryRow.getQuantity();
        if (inventoryRow == null || currentCount < quantity) {
            throw BusinessException.badRequest("背包物品数量不足");
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
            throw BusinessException.badRequest("数量必须大于0");
        }
        requireExistingPlayer(playerId);
        logger.info("Add item by name, playerId={}, itemName={}, quantity={}", playerId, itemName, quantity);

        QueryWrapper<Item> query = new QueryWrapper<>();
        query.eq("name", itemName);
        Item item = itemMapper.selectOne(query);
        if (item == null) {
            throw BusinessException.notFound("奖励物品不存在: " + itemName);
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

    private void requireExistingPlayer(String playerId) {
        if (playerId == null || playerId.isBlank()) {
            throw BusinessException.badRequest("playerId 不能为空");
        }
        if (getPlayer(playerId) == null) {
            throw BusinessException.notFound("玩家不存在");
        }
    }

    private void initializeStarterInventoryIfAbsent(String playerId) {
        QueryWrapper<PlayerInventory> query = new QueryWrapper<>();
        query.eq("player_id", playerId);
        Long count = playerInventoryMapper.selectCount(query);
        if (count != null && count > 0) {
            return;
        }

        addIfExists(playerId, "灵草", STARTER_HERB_COUNT);
        addIfExists(playerId, "灵石", STARTER_STONE_COUNT);
        addIfExists(playerId, "基础丹药", STARTER_PILL_COUNT);
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

    private List<PlayerInventory> getPlayerInventoryRows(String playerId) {
        QueryWrapper<PlayerInventory> query = new QueryWrapper<>();
        query.eq("player_id", playerId);
        return playerInventoryMapper.selectList(query);
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
